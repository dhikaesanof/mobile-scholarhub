package com.example.scholarhub.feature.tracker

import com.example.scholarhub.domain.scholarship.TimelineStep
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TrackerStageTest {
    private fun d(day: String) = LocalDate.parse(day)

    // Jadwal DM01 di katalog.
    private val windows = stageWindows(
        listOf(
            TimelineStep("Mulai Pendaftaran", d("2026-11-01"), null, null),
            TimelineStep("Batas Pendaftaran", d("2026-11-20"), null, null),
            TimelineStep("Seleksi Administrasi", d("2026-12-01"), d("2026-12-07"), null),
            TimelineStep("Tes / Penilaian", d("2026-12-09"), null, null),
            TimelineStep("Wawancara", d("2026-12-12"), d("2026-12-14"), null),
            TimelineStep("Pengumuman", d("2026-12-20"), null, null),
        ),
    )

    @Test fun windowsSpanMatchedSteps() {
        assertEquals(listOf(d("2026-11-01") to d("2026-11-20"), d("2026-12-01") to d("2026-12-07"), d("2026-12-09") to d("2026-12-14")),
            windows.map { it.start to it.end })
        assertEquals("1–20 November 2026", windows[0].dateRange())
    }

    @Test fun stageFollowsToday() {
        assertEquals(TrackerStage.Registration, currentStage(windows, d("2026-11-20"))?.stage)
        // Jeda setelah pendaftaran ditutup masuk ke persiapan seleksi administrasi.
        assertEquals(TrackerStage.Administration, currentStage(windows, d("2026-11-23"))?.stage)
        assertEquals(TrackerStage.Interview, currentStage(windows, d("2026-12-08"))?.stage)
        assertEquals(TrackerStage.Interview, currentStage(windows, d("2027-01-01"))?.stage)
    }

    @Test fun statusText() {
        assertEquals("7 hari menuju penutupan", windows[0].status(d("2026-11-13")))
        assertEquals("Ditutup hari ini", windows[0].status(d("2026-11-20")))
        assertEquals("Dimulai 8 hari lagi", windows[1].status(d("2026-11-23")))
        assertEquals("Tahap telah berakhir", windows[2].status(d("2027-01-01")))
    }
}
