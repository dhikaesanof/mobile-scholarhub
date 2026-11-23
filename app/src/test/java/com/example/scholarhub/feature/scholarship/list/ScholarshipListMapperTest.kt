package com.example.scholarhub.feature.scholarship.list

import com.example.scholarhub.domain.scholarship.EligibilityRule
import com.example.scholarhub.domain.scholarship.FundingType
import com.example.scholarhub.domain.scholarship.RuleOperator
import com.example.scholarhub.domain.scholarship.RuleValue
import com.example.scholarhub.domain.scholarship.Scholarship
import com.example.scholarhub.domain.scholarship.ScholarshipEligibility
import com.example.scholarhub.domain.scholarship.ScholarshipGuide
import com.example.scholarhub.domain.scholarship.ScholarshipRegistration
import com.example.scholarhub.domain.scholarship.ScholarshipSources
import com.example.scholarhub.domain.scholarship.ScholarshipSummary
import com.example.scholarhub.domain.scholarship.ScholarshipTrack
import com.example.scholarhub.feature.scholarship.components.providerInitials
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ScholarshipListMapperTest {
    private val jakarta = ZoneId.of("Asia/Jakarta")
    private fun at(date: String, time: String = "10:00") =
        ZonedDateTime.of(LocalDate.parse(date), LocalTime.parse(time), jakarta).toInstant()

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

    private fun scholarship(
        name: String = "Beasiswa Uji",
        provider: String = "BAZNAS RI",
        status: String = "open_by_schedule",
        opensOn: String? = null,
        closesOn: String? = "2026-11-30",
        closesAt: String? = null,
        common: EligibilityRule? = null,
        tracks: List<ScholarshipTrack> = emptyList(),
    ) = Scholarship(
        id = "SHX",
        name = name,
        provider = provider,
        period = "2026/2027",
        studyCountry = "Indonesia",
        fundingType = FundingType.Full,
        registration = ScholarshipRegistration(
            statusAtSnapshot = status,
            opensOn = opensOn?.let(LocalDate::parse),
            closesOn = closesOn?.let(LocalDate::parse),
            closesAtLocal = closesAt?.let(LocalTime::parse),
            timeZone = jakarta,
            applicationUrl = null,
        ),
        summary = ScholarshipSummary(about = "", benefits = emptyList(), coverage = emptyList()),
        requirements = emptyList(),
        guide = ScholarshipGuide(sections = emptyList(), officialGuideUrl = null),
        eligibility = ScholarshipEligibility(commonRule = common, tracks = tracks, manualChecks = emptyList()),
        sources = ScholarshipSources(primaryUrl = "", supportingUrls = emptyList(), sourceType = "", verificationNote = null),
    )

    @Test
    fun openScholarship_countsDaysInRegistrationZone() {
        val item = scholarship(closesOn = "2026-11-30").toCatalogItem(at("2026-11-20"))
        assertEquals(RegistrationState.Open, item.state)
        assertEquals(10L, item.daysLeft)
        assertEquals("10 Hari Lagi", item.card.deadline)
    }

    @Test
    fun closingDay_isLastDayUntilClosingTime() {
        assertEquals("Hari Terakhir", scholarship(closesOn = "2026-11-20").toCatalogItem(at("2026-11-20", "23:00")).card.deadline)
        // Dengan jam tutup 16:00, setelah jam itu pendaftaran sudah tutup.
        val afterClose = scholarship(closesOn = "2026-11-20", closesAt = "16:00").toCatalogItem(at("2026-11-20", "17:00"))
        assertEquals(RegistrationState.Closed, afterClose.state)
        assertEquals("Ditutup", afterClose.card.deadline)
    }

    @Test
    fun pastDeadline_isClosed_andArchiveFollowsData() {
        assertEquals("Ditutup", scholarship(closesOn = "2026-11-14").toCatalogItem(at("2026-11-20")).card.deadline)
        val archived = scholarship(status = "archived", closesOn = null).toCatalogItem(at("2026-11-20"))
        assertEquals(RegistrationState.Archived, archived.state)
        assertEquals("Arsip", archived.card.deadline)
        assertNull(scholarship(closesOn = null).toCatalogItem(at("2026-11-20")).card.deadline)
    }

    @Test
    fun tags_summarizeDegreeSemesterAndLowestGpaAcrossTracks() {
        val item = scholarship(
            common = EligibilityRule.All(
                listOf(
                    cond("degree", RuleOperator.IN, "S1", "D4"),
                    cond("semester", RuleOperator.GTE, 1),
                    cond("semester", RuleOperator.LTE, 5),
                ),
            ),
            tracks = listOf(
                ScholarshipTrack("a", "A", cond("gpa", RuleOperator.GTE, 3.5), emptyList()),
                ScholarshipTrack("b", "B", cond("gpa", RuleOperator.GTE, 3.3), emptyList()),
            ),
        ).toCatalogItem(at("2026-11-20"))
        assertEquals(listOf("Fully Funded", "Semester 1-5", "IPK >= 3.30"), item.card.tags.map { it.label })
        assertEquals("Jenjang D4 / S1 • Dalam Negeri", item.card.degreeInfo)
        assertNull(item.card.relevance)
    }

    @Test
    fun tags_exactSemestersAndStrictGpa() {
        val item = scholarship(
            common = EligibilityRule.All(listOf(cond("semester", RuleOperator.IN, 4, 6), cond("gpa", RuleOperator.GT, 3))),
        ).toCatalogItem(at("2026-11-20"))
        assertEquals(listOf("Fully Funded", "Semester 4, 6", "IPK > 3.00"), item.card.tags.map { it.label })
        assertEquals("Dalam Negeri", item.card.degreeInfo)
    }

    @Test
    fun catalog_sortsOpenByDeadlineThenClosedThenArchive() {
        val now = at("2026-11-20")
        val items = listOf(
            scholarship(status = "archived", closesOn = null).toCatalogItem(now),
            scholarship(closesOn = "2026-11-14").toCatalogItem(now),
            scholarship(closesOn = "2026-11-30").toCatalogItem(now),
            scholarship(closesOn = "2026-11-23").toCatalogItem(now),
        ).sortedForCatalog()
        assertEquals(listOf("3 Hari Lagi", "10 Hari Lagi", "Ditutup", "Arsip"), items.map { it.card.deadline })
    }

    @Test
    fun notYetOpen_isUpcoming() {
        val item = scholarship(opensOn = "2026-11-25", closesOn = "2026-12-10").toCatalogItem(at("2026-11-20"))
        assertEquals(RegistrationState.Upcoming, item.state)
        assertEquals("Segera Dibuka", item.card.deadline)
        assertEquals(StatusFilter.Upcoming, item.statusFilter)
        // Pada hari buka statusnya sudah dibuka.
        assertEquals(RegistrationState.Open, scholarship(opensOn = "2026-11-20").toCatalogItem(at("2026-11-20")).state)
    }

    @Test
    fun filterCatalog_searchesNameAndProvider_andCombinesCategories() {
        val now = at("2026-11-20")
        val s1 = cond("degree", RuleOperator.IN, "S1")
        val d4 = cond("degree", RuleOperator.EQ, "D4")
        val items = listOf(
            scholarship(name = "Hana Bank", provider = "KEB Hana", common = s1).toCatalogItem(now),
            scholarship(name = "TELADAN", provider = "Tanoto Foundation", common = d4).toCatalogItem(now),
            scholarship(name = "Arsip S1", status = "archived", closesOn = null, common = s1).toCatalogItem(now),
            scholarship(name = "Tanpa Jenjang").toCatalogItem(now),
        )
        fun names(q: String = "", f: CatalogFilters = CatalogFilters()) = items.filterCatalog(q, f).map { it.card.title }

        assertEquals(4, names().size)
        assertEquals(listOf("Hana Bank"), names("  hana "))
        assertEquals(listOf("TELADAN"), names("tanoto")) // penyelenggara
        assertEquals(listOf("Hana Bank", "Arsip S1"), names(f = CatalogFilters(degrees = setOf("S1"))))
        // OR dalam satu kategori; item tanpa data jenjang tidak lolos filter jenjang.
        assertEquals(listOf("Hana Bank", "TELADAN", "Arsip S1"), names(f = CatalogFilters(degrees = setOf("S1", "D4"))))
        // AND antarkategori: arsip dihitung ditutup.
        assertEquals(listOf("Arsip S1"), names(f = CatalogFilters(degrees = setOf("S1"), statuses = setOf(StatusFilter.Closed))))
        assertEquals(listOf("Hana Bank"), names("bank", CatalogFilters(degrees = setOf("S1"), statuses = setOf(StatusFilter.Open))))
        assertEquals(2, CatalogFilters(degrees = setOf("S1"), statuses = setOf(StatusFilter.Open)).count)
        assertEquals(4, names(f = CatalogFilters(locations = setOf(StudyLocation.Domestic), fundings = setOf(FundingType.Full))).size)
        assertEquals(0, names(f = CatalogFilters(locations = setOf(StudyLocation.Abroad))).size)
        assertEquals(0, names(f = CatalogFilters(fundings = setOf(FundingType.Partial))).size)
    }

    @Test
    fun providerInitials_usesFirstTwoWords() {
        assertEquals("BR", providerInitials("BAZNAS RI"))
        assertEquals("KH", providerInitials("KEB Hana Bank / PT Bank KEB Hana Indonesia"))
        assertEquals("?", providerInitials(""))
    }
}
