package com.example.scholarhub.domain.scholarmatch

import com.example.scholarhub.domain.scholarship.EligibilityRule
import com.example.scholarhub.domain.scholarship.FundingType
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
        opensOn: String? = null,
        closesOn: String = "2026-11-30",
    ) = Scholarship(
        id = id, name = "Uji $id", provider = "Penyelenggara", period = "2026",
        studyCountry = "Indonesia", fundingType = FundingType.Partial,
        registration = ScholarshipRegistration(
            status, opensOn?.let(LocalDate::parse), LocalDate.parse(closesOn), null, ZoneId.of("Asia/Jakarta"), null,
        ),
        summary = ScholarshipSummary(about = "", benefits = benefits, coverage = emptyList()),
        requirements = emptyList(),
        guide = ScholarshipGuide(emptyList(), null),
        eligibility = ScholarshipEligibility(common, tracks, manualChecks),
        sources = ScholarshipSources("", emptyList(), "", null),
    )

    private val today = LocalDate.parse("2026-11-23")

    private fun single(s: Scholarship, input: MatchInput) = matcher.match(listOf(s), input, options, today).single()

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
        assertEquals(MatchStatus.Eligible, single(scholarship(common = rule), MatchInput(campusType = "PTN")).status)
        // PTS gagal, IPK belum diisi → belum diketahui, bukan gagal.
        assertEquals(MatchStatus.NeedsReview, single(scholarship(common = rule), MatchInput(campusType = "PTS")).status)
        assertEquals(
            MatchStatus.NotEligible,
            single(scholarship(common = rule), MatchInput(campusType = "PTS", gpa = BigDecimal("3.0"))).status,
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
        val result = single(s, MatchInput(degree = "S1", gpa = BigDecimal("3.2")))
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
    fun semesterBounds_andExactCampusAndMajor() {
        val s = scholarship(
            common = EligibilityRule.All(
                listOf(
                    cond("semester", RuleOperator.GTE, 3),
                    cond("semester", RuleOperator.LTE, 5),
                    cond("campus", RuleOperator.IN, "Universitas Brawijaya", "Universitas Gadjah Mada"),
                    cond("major", RuleOperator.IN, "Informatika", "Statistika"),
                ),
            ),
        )
        val ok = MatchInput(semester = 5, campus = "Universitas Gadjah Mada", major = "Statistika")
        assertEquals(MatchStatus.Eligible, single(s, ok).status)
        assertEquals(MatchStatus.NotEligible, single(s, ok.copy(semester = 6)).status)
        assertEquals(MatchStatus.NotEligible, single(s, ok.copy(campus = "Kampus lain")).status)
        assertEquals(MatchStatus.NotEligible, single(s, ok.copy(major = "Manajemen")).status)
    }

    @Test
    fun noGpa_makesGpaRuleUnknown_notFailed() {
        val s = scholarship(common = cond("gpa", RuleOperator.GTE, 3.5))
        assertEquals(MatchStatus.NeedsReview, single(s, MatchInput(gpa = BigDecimal("2.0"), noGpa = true)).status)
    }

    @Test
    fun otherScholarship_yesOnlyFailsStrictRule_tuitionOnlyRuleBecomesUnknown() {
        val strict = scholarship(common = cond("otherScholarship", RuleOperator.EQ, false))
        val tuitionOnly = scholarship(common = cond("otherTuitionAid", RuleOperator.EQ, false))
        assertEquals(MatchStatus.NotEligible, single(strict, MatchInput(receivingScholarship = true)).status)
        assertEquals(MatchStatus.NeedsReview, single(tuitionOnly, MatchInput(receivingScholarship = true)).status)
        assertEquals(MatchStatus.Eligible, single(tuitionOnly, MatchInput(receivingScholarship = false)).status)
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
                scholarship("E", closesOn = "2026-11-22"), // sudah tutup per tanggal
                scholarship("F", opensOn = "2026-11-24"), // belum dibuka
                scholarship("G", closesOn = "2026-11-23"), // hari terakhir
            ),
            MatchInput(gpa = BigDecimal("3.5"), priorities = setOf("mentoring")),
            options,
            today,
        )
        assertEquals(listOf("B", "D", "G", "A"), results.map { it.scholarship.id })
    }
}
