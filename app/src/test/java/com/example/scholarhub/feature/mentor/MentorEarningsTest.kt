package com.example.scholarhub.feature.mentor

import com.example.scholarhub.feature.mentoring.ConfirmationWindow
import com.example.scholarhub.feature.mentoring.LoggedInMentorId
import com.example.scholarhub.feature.mentoring.MentoringStore
import com.example.scholarhub.feature.mentoring.SessionReport
import com.example.scholarhub.feature.mentoring.mentorPerson
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId

class MentorEarningsTest {
    private val zone = ZoneId.of("Asia/Jakarta")
    private val t0 = LocalDateTime.of(2026, 11, 23, 10, 0)
    private val store = MentoringStore(Clock.fixed(t0.atZone(zone).toInstant(), zone))
    private val start = t0.plusHours(1)
    private val end = start.plusMinutes(59)

    private fun paidBooking(mentorId: String = LoggedInMentorId) =
        store.book(mentorPerson(mentorId), "Review Esai", start, end, 50_000).also { store.markPaid(it.id) }

    private fun earnings(now: LocalDateTime) = store.bookings.value.mentorEarnings(now).sumOf { it.price }

    @Test
    fun earnings_enterBalanceOnlyAfterSessionCompletes() {
        val b = paidBooking()
        assertEquals(0, earnings(start.plusMinutes(10)))          // sedang berlangsung
        assertEquals(0, earnings(end.plusMinutes(5)))             // menunggu konfirmasi mentee
        store.confirm(b.id)
        assertEquals(50_000, earnings(end.plusMinutes(5)))        // dikonfirmasi mentee
    }

    @Test
    fun unconfirmedSession_completesAfter24Hours() {
        paidBooking()
        assertEquals(50_000, earnings(end.plus(ConfirmationWindow)))
    }

    @Test
    fun reportedOrUnpaidOrOtherMentor_doNotCount() {
        val reported = paidBooking()
        store.report(reported.id, SessionReport("Mentor tidak hadir", "x", emptyList()))
        store.book(mentorPerson(LoggedInMentorId), "Belum bayar", start, end, 50_000)
        paidBooking("ahmad-ibra")
        assertEquals(0, earnings(end.plus(ConfirmationWindow)))
    }
}
