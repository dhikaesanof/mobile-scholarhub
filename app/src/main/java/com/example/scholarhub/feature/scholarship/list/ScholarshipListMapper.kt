package com.example.scholarhub.feature.scholarship.list

import com.example.scholarhub.R
import com.example.scholarhub.domain.scholarship.EligibilityRule
import com.example.scholarhub.domain.scholarship.RuleOperator
import com.example.scholarhub.domain.scholarship.RuleValue
import com.example.scholarhub.domain.scholarship.Scholarship
import com.example.scholarhub.feature.scholarship.components.ScholarshipCardUi
import com.example.scholarhub.feature.scholarship.components.ScholarshipTagUi
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/** Status pendaftaran untuk kartu katalog, dihitung dari tanggal tutup dan waktu sekarang. */
enum class RegistrationState { Open, ClosingToday, Closed, Archived, NoDeadline }

data class CatalogItem(
    val card: ScholarshipCardUi,
    val state: RegistrationState,
    val daysLeft: Long?,
)

private val DegreeOrder = listOf("D3", "D4", "S1", "S2", "S3")

/**
 * Memetakan [Scholarship] ke kartu katalog.
 * - Sisa hari dihitung dari `closesOn` (+ `closesAtLocal` jika ada, selain itu akhir hari) di zona waktu pendaftaran,
 *   terhadap [now]. Status `archived` dari data selalu ditampilkan sebagai arsip.
 * - Tag jenjang, semester, dan IPK diringkas dari aturan eligibility (`commonRule` dan semua `tracks`).
 * - Skor relevansi tidak diisi; hanya hasil ScholarMatch yang boleh menampilkannya.
 */
fun Scholarship.toCatalogItem(now: Instant): CatalogItem {
    val reg = registration
    val (state, daysLeft) = when {
        reg.statusAtSnapshot == "archived" -> RegistrationState.Archived to null
        reg.closesOn == null -> RegistrationState.NoDeadline to null
        else -> {
            val zone = reg.timeZone
            val closesAt = ZonedDateTime.of(reg.closesOn, reg.closesAtLocal ?: LocalTime.MAX, zone)
            val nowZoned = now.atZone(zone)
            val days = ChronoUnit.DAYS.between(nowZoned.toLocalDate(), reg.closesOn)
            when {
                !nowZoned.isBefore(closesAt) -> RegistrationState.Closed to days
                days == 0L -> RegistrationState.ClosingToday to 0L
                else -> RegistrationState.Open to days
            }
        }
    }
    val deadlineLabel = when (state) {
        RegistrationState.Open -> "$daysLeft Hari Lagi"
        RegistrationState.ClosingToday -> "Hari Terakhir"
        RegistrationState.Closed -> "Ditutup"
        RegistrationState.Archived -> "Arsip"
        RegistrationState.NoDeadline -> null
    }

    val conditions = buildList {
        eligibility.commonRule?.let { collectConditions(it, this) }
        eligibility.tracks.forEach { track -> track.rule?.let { collectConditions(it, this) } }
    }
    val degrees = conditions.filter { it.field == "degree" }
        .flatMap { c -> c.values.mapNotNull { (it as? RuleValue.Text)?.value } }
        .distinct()
        .sortedBy { DegreeOrder.indexOf(it).let { i -> if (i < 0) Int.MAX_VALUE else i } }

    val tags = buildList {
        if (degrees.isNotEmpty()) add(ScholarshipTagUi(degrees.joinToString(", "), R.drawable.ic_home_school))
        semesterLabel(conditions.filter { it.field == "semester" })?.let { add(ScholarshipTagUi(it, R.drawable.ic_home_layers)) }
        gpaLabel(conditions.filter { it.field == "gpa" })?.let { add(ScholarshipTagUi(it, R.drawable.ic_home_stars)) }
    }

    val degreeInfo = listOfNotNull(
        degrees.takeIf { it.isNotEmpty() }?.let { "Jenjang ${it.joinToString(" / ")}" },
        "Periode $period",
    ).joinToString(" • ")

    return CatalogItem(
        card = ScholarshipCardUi(
            id = id,
            provider = provider.uppercase(),
            title = name,
            degreeInfo = degreeInfo,
            deadline = deadlineLabel,
            tags = tags,
            relevance = null,
            logo = null,
        ),
        state = state,
        daysLeft = daysLeft,
    )
}

/** Urutan katalog: yang masih buka (paling dekat tutup dulu), tanpa tenggat, lalu yang sudah tutup, lalu arsip. */
fun List<CatalogItem>.sortedForCatalog(): List<CatalogItem> = sortedWith(
    compareBy<CatalogItem> {
        when (it.state) {
            RegistrationState.ClosingToday, RegistrationState.Open -> 0
            RegistrationState.NoDeadline -> 1
            RegistrationState.Closed -> 2
            RegistrationState.Archived -> 3
        }
    }.thenBy { it.daysLeft ?: Long.MAX_VALUE },
)

val CatalogItem.isActive: Boolean
    get() = state == RegistrationState.Open || state == RegistrationState.ClosingToday || state == RegistrationState.NoDeadline

private fun collectConditions(rule: EligibilityRule, out: MutableList<EligibilityRule.Condition>) {
    when (rule) {
        is EligibilityRule.All -> rule.rules.forEach { collectConditions(it, out) }
        is EligibilityRule.AnyOf -> rule.rules.forEach { collectConditions(it, out) }
        is EligibilityRule.Condition -> out += rule
    }
}

private fun EligibilityRule.Condition.numbers(): List<BigDecimal> = values.mapNotNull { (it as? RuleValue.Number)?.value }

private fun semesterLabel(rules: List<EligibilityRule.Condition>): String? {
    if (rules.isEmpty()) return null
    val exact = rules.filter { it.operator == RuleOperator.EQ || it.operator == RuleOperator.IN }
        .flatMap { it.numbers() }.map { it.toInt() }.distinct().sorted()
    if (exact.isNotEmpty()) return "Semester ${exact.joinToString(", ")}"
    val min = rules.filter { it.operator == RuleOperator.GTE || it.operator == RuleOperator.GT }
        .flatMap { c -> c.numbers().map { if (c.operator == RuleOperator.GT) it.toInt() + 1 else it.toInt() } }.minOrNull()
    val max = rules.filter { it.operator == RuleOperator.LTE }.flatMap { it.numbers() }.map { it.toInt() }.maxOrNull()
    return when {
        min != null && max != null -> "Semester $min-$max"
        min != null -> "Semester $min+"
        max != null -> "Semester 1-$max"
        else -> null
    }
}

private fun gpaLabel(rules: List<EligibilityRule.Condition>): String? {
    // Ambil batas terendah (jalur paling longgar) agar tag tidak menyiratkan syarat lebih ketat dari data.
    val bound = rules
        .filter { it.operator == RuleOperator.GTE || it.operator == RuleOperator.GT }
        .flatMap { c -> c.numbers().map { c.operator to it } }
        .minByOrNull { it.second }
        ?: return null
    val symbol = if (bound.first == RuleOperator.GT) ">" else ">="
    return "IPK $symbol ${bound.second.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
}
