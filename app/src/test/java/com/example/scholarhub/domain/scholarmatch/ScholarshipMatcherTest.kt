package com.example.scholarhub.domain.scholarmatch

import com.example.scholarhub.domain.scholarship.EligibilityRule
import com.example.scholarhub.domain.scholarship.PreferenceOption
import com.example.scholarhub.domain.scholarship.RuleOperator
import com.example.scholarhub.domain.scholarship.RuleValue
import com.example.scholarhub.domain.scholarship.Scholarship
import com.example.scholarhub.domain.scholarship.ScholarshipBenefit
import com.example.scholarhub.domain.scholarship.ScholarshipEligibility
import com.example.scholarhub.domain.scholarship.ScholarshipGuide
import com.example.scholarhub.domain.scholarship.ScholarshipRegistration
import com.example.scholarhub.domain.scholarship.ScholarshipSources
import com.example.scholarhub.domain.scholarship.ScholarshipSummary
import com.example.scholarhub.domain.scholarship.ScholarshipTrack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZoneId

class ScholarshipMatcherTest {
    private val matcher = ScholarshipMatcher()
    private val options = listOf(
        PreferenceOption("tuition_support", "Bantuan UKT"),
        PreferenceOption("monthly_funding", "Dana bulanan"),
        PreferenceOption("mentoring", "Mentoring"),
        PreferenceOption("training", "Pelatihan"),
        PreferenceOption("career_development", "Persiapan karier"),
        PreferenceOption("internship_opportunity", "Kesempatan magang"),
    )

    private fun cond(field: String, op: RuleOperator, vararg values: Any) = EligibilityRule.Condition(
        field = field,
        operator = op,
        values = values.map {
            when (it) {
                is String -> RuleValue.Text(it)
                is Boolean -> RuleValue.Flag(it)
                else -> RuleValue.Number(BigDecimal(it.toString()))
            }
        },
        reason = null,
    )

    private fun benefit(vararg tags: String, evidence: String = "documented", conditional: Boolean = false) = ScholarshipBenefit(
        title = "Manfaat", amountIdr = null, amountKind = null, unitOrCoverage = null, duration = null,
        description = null, tags = tags.toSet(), evidence = evidence, conditional = conditional,
    )

    private fun scholarship(
        id: String = "SHX",
        common: EligibilityRule? = null,
        tracks: List<ScholarshipTrack> = emptyList(),
        manualChecks: List<String> = emptyList(),
        status: String = "open_by_schedule",
        benefits: List<ScholarshipBenefit> = emptyList(),
    ) = Scholarship(
        id = id, name = "Uji $id", provider = "Penyelenggara", period = "2026",
        registration = ScholarshipRegistration(status, null, LocalDate.parse("2026-11-30"), null, ZoneId.of("Asia/Jakarta"), null),
        summary = ScholarshipSummary(about = "", benefits = benefits, coverage = emptyList()),
        requirements = emptyList(),
        guide = ScholarshipGuide(emptyList(), null),
        eligibility = ScholarshipEligibility(common, tracks, manualChecks),
        sources = ScholarshipSources("", emptyList(), "", null),
    )

    private fun single(s: Scholarship, input: MatchInput) = matcher.match(listOf(s), input, options).single()

    @Test
    fun gpaBoundaries_gteIncludesBound_gtExcludesIt() {
        val gte = scholarship(common = cond("gpa", RuleOperator.GTE, 3.3))
        assertEquals(MatchStatus.Eligible, single(gte, MatchInput(gpa = BigDecimal("3.30"))).status)
        assertEquals(MatchStatus.NotEligible, single(gte, MatchInput(gpa = BigDecimal("3.29"))).status)
        val gt = scholarship(common = cond("gpa", RuleOperator.GT, 3))
        assertEquals(MatchStatus.NotEligible, single(gt, MatchInput(gpa = BigDecimal("3.00"))).status)
        assertEquals(MatchStatus.Eligible, single(gt, MatchInput(gpa = BigDecimal("3.01"))).status)
    }

    @Test
    fun emptyAnswer_isUnknown_andKeepsReason() {
        val result = single(scholarship(common = cond("gpa", RuleOperator.GTE, 3.25)), MatchInput())
        assertEquals(MatchStatus.NeedsReview, result.status)
        assertEquals(listOf("IPK minimal 3,25 (belum ada jawaban)"), result.bestTrack.reasons)
    }

    @Test
    fun allGroup_oneFailureFailsEvenWithUnknowns() {
        val rule = EligibilityRule.All(listOf(cond("gpa", RuleOperator.GTE, 3.5), cond("citizenship", RuleOperator.EQ, "Indonesia")))
        val result = single(scholarship(common = rule), MatchInput(gpa = BigDecimal("3.2")))
        assertEquals(MatchStatus.NotEligible, result.status)
        assertEquals(listOf("IPK minimal 3,5"), result.bestTrack.reasons)
    }

    @Test
    fun anyGroup_oneSatisfiedAlternativeIsEnough() {
        val rule = EligibilityRule.AnyOf(listOf(cond("campusType", RuleOperator.EQ, "PTN"), cond("gpa", RuleOperator.GTE, 3.9)))
        assertEquals(MatchStatus.Eligible, single(scholarship(common = rule), MatchInput(campusType = CampusTypeAnswer.PTN)).status)
        // PTS gagal, IPK belum diisi → belum diketahui, bukan gagal.
        assertEquals(MatchStatus.NeedsReview, single(scholarship(common = rule), MatchInput(campusType = CampusTypeAnswer.PTS)).status)
        assertEquals(
            MatchStatus.NotEligible,
            single(scholarship(common = rule), MatchInput(campusType = CampusTypeAnswer.PTS, gpa = BigDecimal("3.0"))).status,
        )
    }

    @Test
    fun tracksAreEvaluatedSeparately_bestTrackWins() {
        val s = scholarship(
            common = cond("degree", RuleOperator.IN, "S1", "D4"),
            tracks = listOf(
                ScholarshipTrack("excellence", "Excellence", cond("gpa", RuleOperator.GTE, 3.5), emptyList()),
                ScholarshipTrack("elevate", "Elevate", cond("gpa", RuleOperator.GTE, 3.0), listOf("Cek SKTM")),
            ),
        )
        val result = single(s, MatchInput(degree = DegreeAnswer.Bachelor, gpa = BigDecimal("3.2")))
        assertEquals(listOf(MatchStatus.NotEligible, MatchStatus.NeedsReview), result.tracks.map { it.status })
        assertEquals("elevate", result.bestTrack.trackId)
        assertEquals(MatchStatus.NeedsReview, result.status)
        assertEquals(listOf("Cek SKTM"), result.bestTrack.manualChecks)
    }

    @Test
    fun passingAutomaticRulesWithManualChecks_needsReview() {
        val s = scholarship(common = cond("gpa", RuleOperator.GTE, 3.0), manualChecks = listOf("Periksa SKTM"))
        val result = single(s, MatchInput(gpa = BigDecimal("3.5")))
        assertEquals(MatchStatus.NeedsReview, result.status)
        assertTrue(result.bestTrack.reasons.isEmpty())
        assertEquals(listOf("Periksa SKTM"), result.bestTrack.manualChecks)
        assertEquals(MatchStatus.Eligible, single(scholarship(common = cond("gpa", RuleOperator.GTE, 3.0)), MatchInput(gpa = BigDecimal("3.5"))).status)
    }

    @Test
    fun rangeAnswers_mixedResultIsUnknown() {
        val d4Only = scholarship(common = cond("degree", RuleOperator.EQ, "D4"))
        assertEquals(MatchStatus.NeedsReview, single(d4Only, MatchInput(degree = DegreeAnswer.Vocational)).status)
        val sem = scholarship(common = EligibilityRule.All(listOf(cond("semester", RuleOperator.GTE, 1), cond("semester", RuleOperator.LTE, 5))))
        assertEquals(MatchStatus.Eligible, single(sem, MatchInput(semester = SemesterAnswer.Sem3To4)).status)
        assertEquals(MatchStatus.NeedsReview, single(sem, MatchInput(semester = SemesterAnswer.Sem5To6)).status)
        assertEquals(MatchStatus.NotEligible, single(sem, MatchInput(semester = SemesterAnswer.Sem7Plus)).status)
    }

    @Test
    fun campusFreeText_matchesCodeInParentheses_otherwiseUnknown() {
        val s = scholarship(common = cond("campus", RuleOperator.IN, "UI", "ITB"))
        assertEquals(MatchStatus.Eligible, single(s, MatchInput(campusName = "Universitas Indonesia (UI)")).status)
        assertEquals(MatchStatus.NeedsReview, single(s, MatchInput(campusName = "Universitas Gadjah Mada")).status)
    }

    @Test
    fun score_countsUniqueSupportedPriorities_andIgnoresConditionalOrSecondary() {
        val s = scholarship(
            benefits = listOf(
                benefit("tuition_support", "mentoring"),
                benefit("mentoring"),
                benefit("monthly_funding", conditional = true),
                benefit("training", evidence = "secondary"),
            ),
        )
        val result = single(s, MatchInput(priorities = setOf("tuition_support", "mentoring", "monthly_funding", "training")))
        assertEquals(listOf("tuition_support", "mentoring"), result.matchedPriorities.map { it.id })
        assertEquals(2, result.score)
        assertEquals(50, result.relevancePercent)
        assertNull(single(s, MatchInput()).relevancePercent)
    }

    @Test
    fun priorities_areLimitedToFive() {
        val s = scholarship(benefits = listOf(benefit(*options.map { it.id }.toTypedArray())))
        val result = single(s, MatchInput(priorities = options.map { it.id }.toSet()))
        assertEquals(5, result.score)
    }

    @Test
    fun onlyOpenRegistrations_areRecommended_sortedByStatusThenScore() {
        val results = matcher.match(
            listOf(
                scholarship("A", common = cond("gpa", RuleOperator.GTE, 3.9)),
                scholarship("B", benefits = listOf(benefit("mentoring"))),
                scholarship("C", status = "archived"),
                scholarship("D"),
            ),
            MatchInput(gpa = BigDecimal("3.5"), priorities = setOf("mentoring")),
            options,
        )
        assertEquals(listOf("B", "D", "A"), results.map { it.scholarship.id })
    }
}
