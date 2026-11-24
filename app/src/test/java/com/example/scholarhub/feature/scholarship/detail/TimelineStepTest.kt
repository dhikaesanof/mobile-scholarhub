package com.example.scholarhub.feature.scholarship.detail

import com.example.scholarhub.domain.scholarship.TimelineStep
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TimelineStepTest {
    private fun step(start: String, end: String? = null) =
        TimelineStep("Tahap", LocalDate.parse(start), end?.let(LocalDate::parse), null)

    // Bentuk timeline DM01: mulai, batas, seleksi administrasi (rentang), lalu pengumuman.
    private val steps = listOf(step("2026-11-01"), step("2026-11-20"), step("2026-12-01", "2026-12-07"), step("2026-12-20"))

    @Test
    fun deadlineDay_fillsDeadlineAndEarlierSteps() {
        assertEquals(1, lastReachedStep(steps, LocalDate.parse("2026-11-20")))
    }

    @Test
    fun rangeCountsFromItsStart_andBeforeFirstStepNothingIsFilled() {
        assertEquals(2, lastReachedStep(steps, LocalDate.parse("2026-12-03")))
        assertEquals(-1, lastReachedStep(steps, LocalDate.parse("2026-10-31")))
    }

    @Test
    fun stepDates_followFigmaFormat() {
        assertEquals("9 Desember 2026", stepDates(step("2026-12-09")))
        assertEquals("1 - 7 Desember 2026", stepDates(step("2026-12-01", "2026-12-07")))
        assertEquals("28 November - 2 Desember 2026", stepDates(step("2026-11-28", "2026-12-02")))
    }
}
