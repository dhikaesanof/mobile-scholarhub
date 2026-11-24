package com.example.scholarhub.feature.scholarship.list

import com.example.scholarhub.R
import com.example.scholarhub.domain.scholarship.EligibilityRule
import com.example.scholarhub.domain.scholarship.FundingType
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
enum class RegistrationState { Upcoming, Open, ClosingToday, Closed, Archived, NoDeadline }

data class CatalogItem(
    val card: ScholarshipCardUi,
    val state: RegistrationState,
    val daysLeft: Long?,
    /** Jenjang dari aturan eligibility, urut [DegreeOrder]; dipakai filter jenjang. */
    val degrees: List<String> = emptyList(),
    val location: StudyLocation = StudyLocation.Domestic,
    val funding: FundingType = FundingType.Partial,
)

enum class StudyLocation(val label: String) { Domestic("Dalam Negeri"), Abroad("Luar Negeri") }

/** Urutan jenjang, juga pilihan filter "Jenjang Pendidikan". */
val DegreeOrder = listOf("D3", "D4", "S1", "S2", "S3")

/**
 * Memetakan [Scholarship] ke kartu katalog.
 * - Sisa hari dihitung dari `closesOn` (+ `closesAtLocal` jika ada, selain itu akhir hari) di zona waktu pendaftaran,
 *   terhadap [now]. Status `archived` dari data selalu ditampilkan sebagai arsip.
 * - Baris info: jenjang + lokasi studi. Tag: jenis pendanaan, lalu semester dan IPK yang diringkas dari aturan
 *   eligibility (`commonRule` dan semua `tracks`).
 * - Skor relevansi tidak diisi; hanya hasil ScholarMatch yang boleh menampilkannya.
 */
fun Scholarship.toCatalogItem(now: Instant): CatalogItem {
    val reg = registration
    val (state, daysLeft) = when {
        reg.statusAtSnapshot == "archived" -> RegistrationState.Archived to null
        reg.opensOn != null && now.atZone(reg.timeZone).toLocalDate().isBefore(reg.opensOn) -> RegistrationState.Upcoming to null
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
        RegistrationState.Upcoming -> "Segera Dibuka"
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

    val location = if (studyCountry.equals("Indonesia", ignoreCase = true)) StudyLocation.Domestic else StudyLocation.Abroad
    val tags = buildList {
        add(ScholarshipTagUi(fundingType.label, R.drawable.ic_home_school))
        semesterLabel(conditions.filter { it.field == "semester" })?.let { add(ScholarshipTagUi(it, R.drawable.ic_home_layers)) }
        gpaLabel(conditions.filter { it.field == "gpa" })?.let { add(ScholarshipTagUi(it, R.drawable.ic_home_stars)) }
    }

    val degreeInfo = listOfNotNull(
        degrees.takeIf { it.isNotEmpty() }?.let { "Jenjang ${it.joinToString(" / ")}" },
        location.label,
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
        degrees = degrees,
        location = location,
        funding = fundingType,
    )
}

/** Urutan katalog: yang masih buka (paling dekat tutup dulu), segera dibuka dan tanpa tenggat, lalu yang sudah tutup, lalu arsip. */
fun List<CatalogItem>.sortedForCatalog(): List<CatalogItem> = sortedWith(
    compareBy<CatalogItem> {
        when (it.state) {
            RegistrationState.ClosingToday, RegistrationState.Open -> 0
            RegistrationState.Upcoming, RegistrationState.NoDeadline -> 1
            RegistrationState.Closed -> 2
            RegistrationState.Archived -> 3
        }
    }.thenBy { it.daysLeft ?: Long.MAX_VALUE },
)

/** Pilihan filter "Status" (Figma 229:4307). Arsip dihitung ditutup. */
enum class StatusFilter(val label: String) { Open("Dibuka"), Closed("Ditutup"), Upcoming("Segera Dibuka") }

val CatalogItem.statusFilter: StatusFilter
    get() = when (state) {
        RegistrationState.Upcoming -> StatusFilter.Upcoming
        RegistrationState.Open, RegistrationState.ClosingToday, RegistrationState.NoDeadline -> StatusFilter.Open
        RegistrationState.Closed, RegistrationState.Archived -> StatusFilter.Closed
    }

/** Filter katalog: beberapa pilihan dalam satu kategori digabung OR, antarkategori AND. */
data class CatalogFilters(
    val degrees: Set<String> = emptySet(),
    val locations: Set<StudyLocation> = emptySet(),
    val fundings: Set<FundingType> = emptySet(),
    val statuses: Set<StatusFilter> = emptySet(),
) {
    val count: Int get() = degrees.size + locations.size + fundings.size + statuses.size
}

/** Pilihan "Jenis Pendanaan" di Figma; program nonfinansial tidak termasuk keduanya. */
val FundingFilterOptions = listOf(FundingType.Full, FundingType.Partial)

/** Pencarian teks di nama dan penyelenggara (tanpa beda huruf besar/kecil), lalu filter jenjang dan status. */
fun List<CatalogItem>.filterCatalog(query: String, filters: CatalogFilters): List<CatalogItem> {
    val q = query.trim()
    return filter { item ->
        (q.isEmpty() || item.card.title.contains(q, ignoreCase = true) || item.card.provider.contains(q, ignoreCase = true)) &&
            (filters.degrees.isEmpty() || item.degrees.any { it in filters.degrees }) &&
            (filters.locations.isEmpty() || item.location in filters.locations) &&
            (filters.fundings.isEmpty() || item.funding in filters.fundings) &&
            (filters.statuses.isEmpty() || item.statusFilter in filters.statuses)
    }
}

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
