package com.example.scholarhub.domain.scholarmatch

import com.example.scholarhub.domain.scholarship.EligibilityRule
import com.example.scholarhub.domain.scholarship.PreferenceOption
import com.example.scholarhub.domain.scholarship.RuleOperator
import com.example.scholarhub.domain.scholarship.RuleValue
import com.example.scholarhub.domain.scholarship.Scholarship
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Mencocokkan jawaban [MatchInput] dengan aturan eligibility katalog. Independen dari UI dan penyimpanan.
 *
 * - `commonRule` dievaluasi lalu setiap jalur di `tracks` dinilai terpisah (aturan umum + aturan jalur).
 * - Grup `all`: satu syarat gagal membuat grup gagal. Grup `any`: satu alternatif terpenuhi membuat grup terpenuhi.
 * - Hanya beasiswa yang pendaftarannya berjalan pada [today] (antara `opensOn` dan `closesOn`) yang dinilai.
 * - Jawaban kosong (atau field yang tidak ditanyakan form) menghasilkan keadaan belum diketahui.
 * - Jika aturan otomatis terpenuhi tetapi masih ada `manualChecks`, hasilnya **Perlu dicek** beserta ketentuannya.
 * - Skor = jumlah prioritas unik yang cocok dengan tag manfaat yang didukung data (tanpa manfaat bersyarat
 *   dan manfaat bersumber sekunder).
 */
class ScholarshipMatcher {

    fun match(
        scholarships: List<Scholarship>,
        input: MatchInput,
        preferenceOptions: List<PreferenceOption>,
        today: LocalDate,
    ): List<ScholarshipMatch> {
        val priorities = input.priorities.take(MatchInput.MAX_PRIORITIES).toSet()
        val answers = answersFor(input)
        return scholarships
            .filter { it.isOpenOn(today) }
            .map { evaluate(it, answers, priorities, preferenceOptions) }
            .sortedWith(compareBy<ScholarshipMatch> { it.status.ordinalForSort() }.thenByDescending { it.score })
    }

    internal fun evaluate(
        scholarship: Scholarship,
        answers: Map<String, FieldAnswer>,
        priorities: Set<String>,
        preferenceOptions: List<PreferenceOption>,
    ): ScholarshipMatch {
        val eligibility = scholarship.eligibility
        val common = eligibility.commonRule?.let { evaluate(it, answers) } ?: Eval.Pass
        val tracks = if (eligibility.tracks.isEmpty()) {
            listOf(toTrackResult(null, null, common, eligibility.manualChecks))
        } else {
            eligibility.tracks.map { track ->
                val trackEval = track.rule?.let { evaluate(it, answers) } ?: Eval.Pass
                toTrackResult(track.id, track.name, all(listOf(common, trackEval)), eligibility.manualChecks + track.manualChecks)
            }
        }
        val best = tracks.minBy { it.status.ordinalForSort() }

        val supportedTags = scholarship.summary.benefits
            .filter { it.evidence != "secondary" && !it.conditional }
            .flatMap { it.tags }
            .toSet()
        val matched = preferenceOptions.filter { it.id in priorities && it.id in supportedTags }
        return ScholarshipMatch(
            scholarship = scholarship,
            status = best.status,
            bestTrack = best,
            tracks = tracks,
            matchedPriorities = matched,
            score = matched.size,
            relevancePercent = if (priorities.isEmpty()) null else matched.size * 100 / priorities.size,
        )
    }

    private fun toTrackResult(id: String?, name: String?, eval: Eval, manualChecks: List<String>): TrackResult = when (eval.outcome) {
        Outcome.Pass -> if (manualChecks.isEmpty()) {
            TrackResult(id, name, MatchStatus.Eligible, emptyList(), emptyList())
        } else {
            TrackResult(id, name, MatchStatus.NeedsReview, emptyList(), manualChecks)
        }
        Outcome.Unknown -> TrackResult(id, name, MatchStatus.NeedsReview, eval.reasons, manualChecks)
        Outcome.Fail -> TrackResult(id, name, MatchStatus.NotEligible, eval.reasons, emptyList())
    }

    // ---------- Evaluasi aturan ----------

    internal enum class Outcome { Pass, Fail, Unknown }

    internal data class Eval(val outcome: Outcome, val reasons: List<String> = emptyList()) {
        companion object {
            val Pass = Eval(Outcome.Pass)
        }
    }

    internal fun evaluate(rule: EligibilityRule, answers: Map<String, FieldAnswer>): Eval = when (rule) {
        is EligibilityRule.All -> all(rule.rules.map { evaluate(it, answers) })
        is EligibilityRule.AnyOf -> any(rule.rules.map { evaluate(it, answers) })
        is EligibilityRule.Condition -> evaluate(rule, answers[rule.field])
    }

    private fun all(results: List<Eval>): Eval = when {
        results.any { it.outcome == Outcome.Fail } ->
            Eval(Outcome.Fail, results.filter { it.outcome == Outcome.Fail }.flatMap { it.reasons })
        results.any { it.outcome == Outcome.Unknown } ->
            Eval(Outcome.Unknown, results.filter { it.outcome == Outcome.Unknown }.flatMap { it.reasons })
        else -> Eval.Pass
    }

    private fun any(results: List<Eval>): Eval = when {
        results.isEmpty() || results.any { it.outcome == Outcome.Pass } -> Eval.Pass
        results.any { it.outcome == Outcome.Unknown } -> Eval(
            Outcome.Unknown,
            listOf("Salah satu dari: " + results.flatMap { it.reasons }.joinToString("; ")),
        )
        else -> Eval(Outcome.Fail, listOf("Tidak ada alternatif yang terpenuhi: " + results.flatMap { it.reasons }.joinToString("; ")))
    }

    private fun evaluate(condition: EligibilityRule.Condition, answer: FieldAnswer?): Eval {
        val label = fieldLabel(condition.field)
        val requirement = condition.reason ?: "$label ${describe(condition)}"
        return when (answer) {
            null -> Eval(Outcome.Unknown, listOf("$requirement (belum ada jawaban)"))
            is FieldAnswer.Candidates -> {
                val results = answer.values.map { satisfies(it, condition) }
                when {
                    results.all { it } -> Eval.Pass
                    results.none { it } -> Eval(Outcome.Fail, listOf(requirement))
                    else -> Eval(Outcome.Unknown, listOf("$requirement (jawabanmu berupa rentang)"))
                }
            }
        }
    }

    private fun satisfies(candidate: RuleValue, condition: EligibilityRule.Condition): Boolean {
        val values = condition.values
        return when (condition.operator) {
            RuleOperator.EQ, RuleOperator.IN -> values.any { equal(candidate, it) }
            RuleOperator.GT -> compare(candidate, values.firstOrNull())?.let { it > 0 } ?: false
            RuleOperator.GTE -> compare(candidate, values.firstOrNull())?.let { it >= 0 } ?: false
            RuleOperator.LTE -> compare(candidate, values.firstOrNull())?.let { it <= 0 } ?: false
        }
    }

    private fun equal(a: RuleValue, b: RuleValue): Boolean = when {
        a is RuleValue.Text && b is RuleValue.Text -> a.value.equals(b.value, ignoreCase = true)
        a is RuleValue.Number && b is RuleValue.Number -> a.value.compareTo(b.value) == 0
        a is RuleValue.Flag && b is RuleValue.Flag -> a.value == b.value
        else -> false
    }

    private fun compare(a: RuleValue, b: RuleValue?): Int? =
        if (a is RuleValue.Number && b is RuleValue.Number) a.value.compareTo(b.value) else null

    // ---------- Jawaban ke field aturan ----------

    internal sealed interface FieldAnswer {
        /** Satu atau beberapa nilai yang mungkin; semua harus memenuhi agar syarat dianggap terpenuhi. */
        data class Candidates(val values: List<RuleValue>) : FieldAnswer
    }

    internal fun answersFor(input: MatchInput): Map<String, FieldAnswer> = buildMap {
        input.activeStudent?.let { put("activeStudent", flag(it)) }
        input.degree?.let { put("degree", text(it)) }
        input.campus?.let { put("campus", text(it)) }
        input.campusType?.let { put("campusType", text(it)) }
        input.major?.let { put("major", text(it)) }
        input.semester?.let { put("semester", FieldAnswer.Candidates(listOf(RuleValue.Number(BigDecimal(it))))) }
        // "Belum punya IPK" membiarkan syarat IPK belum diketahui.
        input.gpa?.takeUnless { input.noGpa }?.let { put("gpa", FieldAnswer.Candidates(listOf(RuleValue.Number(it)))) }
        input.organization?.let { put("organization", flag(it)) }
        input.commitment?.let { put("commitment", flag(it)) }
        when (input.receivingScholarship) {
            false -> {
                put("otherScholarship", flag(false))
                put("otherTuitionAid", flag(false))
            }
            // Beasiswa lain yang sedang diterima bisa berupa bantuan UKT atau nonpendidikan; hanya field umum yang bisa dinilai.
            true -> put("otherScholarship", flag(true))
            null -> Unit
        }
    }

    private fun flag(value: Boolean) = FieldAnswer.Candidates(listOf(RuleValue.Flag(value)))

    private fun text(value: String) = FieldAnswer.Candidates(listOf(RuleValue.Text(value)))

    private fun describe(c: EligibilityRule.Condition): String {
        val values = c.values.map(::format)
        return when (c.operator) {
            RuleOperator.EQ -> "harus ${values.firstOrNull().orEmpty()}"
            RuleOperator.IN -> "salah satu dari ${values.joinToString(", ")}"
            RuleOperator.GT -> "lebih dari ${values.firstOrNull().orEmpty()}"
            RuleOperator.GTE -> "minimal ${values.firstOrNull().orEmpty()}"
            RuleOperator.LTE -> "maksimal ${values.firstOrNull().orEmpty()}"
        }
    }

    private fun format(value: RuleValue): String = when (value) {
        is RuleValue.Text -> value.value
        is RuleValue.Number -> value.value.stripTrailingZeros().toPlainString().replace('.', ',')
        is RuleValue.Flag -> if (value.value) "ya" else "tidak"
    }

    private fun fieldLabel(field: String): String = FieldLabels[field] ?: field

    private fun Scholarship.isOpenOn(today: LocalDate): Boolean {
        val reg = registration
        if (reg.statusAtSnapshot == "archived") return false
        if (reg.opensOn != null && today.isBefore(reg.opensOn)) return false
        return if (reg.closesOn != null) !today.isAfter(reg.closesOn) else reg.statusAtSnapshot.startsWith("open")
    }

    private fun MatchStatus.ordinalForSort() = when (this) {
        MatchStatus.Eligible -> 0
        MatchStatus.NeedsReview -> 1
        MatchStatus.NotEligible -> 2
    }

    private companion object {
        val FieldLabels = mapOf(
            "degree" to "Jenjang",
            "semester" to "Semester",
            "gpa" to "IPK",
            "campus" to "Kampus",
            "campusType" to "Tipe perguruan tinggi",
            "major" to "Jurusan",
            "cohort" to "Angkatan",
            "citizenship" to "Kewarganegaraan",
            "activeStudent" to "Status mahasiswa aktif",
            "organization" to "Pengalaman organisasi",
            "commitment" to "Komitmen kegiatan dan laporan",
            "otherTuitionAid" to "Sedang menerima bantuan UKT lain",
            "financialNeed" to "Membutuhkan bantuan finansial",
            "otherScholarship" to "Sedang menerima beasiswa lain",
            "otherFullScholarship" to "Sedang menerima beasiswa penuh lain",
            "sameObjectOtherFunding" to "Pendanaan lain untuk objek yang sama",
            "thesisStage" to "Tahap tugas akhir",
            "programAccreditation" to "Akreditasi program studi",
            "proposalApproved" to "Proposal disetujui",
            "similarityWithoutReferences" to "Similarity tanpa referensi",
            "volunteerCommitment" to "Komitmen relawan",
            "validCouponYear" to "Tahun kupon berlaku",
            "studyCountry" to "Negara studi",
            "oneYearCommitment" to "Komitmen satu tahun",
            "projectCount" to "Jumlah proyek",
            "semester3Gpa" to "IPK semester 3",
            "grade12AverageOutOf10" to "Rata-rata nilai kelas 12",
            "finalReportAverageOutOf100" to "Rata-rata rapor akhir",
            "snbtScore" to "Skor SNBT",
        )
    }
}
