package com.example.scholarhub.feature.mentoring

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

class MentoringStoreTest {
    private val zone = ZoneId.of("Asia/Jakarta")
    private val t0 = LocalDateTime.of(2026, 11, 23, 10, 0)
    private val clock = Clock.fixed(t0.atZone(zone).toInstant(), zone)
    private val store = MentoringStore(clock)
    private val mentor = mentorPerson("samantha-dira")

    private fun newBooking(start: LocalDateTime = t0.plusDays(2)) = store.book(mentor, "Review Esai Beasiswa", start, start.plusMinutes(59), 50_000)

    @Test
    fun unpaidBooking_isNotASession_andFailsAfterPaymentWindow() {
        val b = newBooking()
        assertNull(b.phaseAt(t0))
        assertEquals(PaymentStatus.Pending, b.paymentStatusAt(t0.plusMinutes(14)))
        assertEquals(PaymentStatus.Failed, b.paymentStatusAt(t0.plus(PaymentWindow)))
    }

    @Test
    fun cancelledBooking_failsAtOnce_butPaidOneStaysPaid() {
        val b = newBooking()
        store.cancel(b.id)
        assertEquals(PaymentStatus.Failed, store.find(b.id)!!.paymentStatusAt(t0))
        val paid = newBooking(t0.plusDays(3))
        store.markPaid(paid.id)
        store.cancel(paid.id)
        assertEquals(PaymentStatus.Paid, store.find(paid.id)!!.payment)
    }

    @Test
    fun paidSession_phasesFollowTime() {
        val start = t0.plusDays(2)
        store.markPaid(newBooking(start).id)
        val b = store.bookings.value.last()
        assertEquals(SessionPhase.Scheduled, b.phaseAt(start.minusHours(25)))
        assertEquals(SessionPhase.StartingSoon, b.phaseAt(start.minusHours(24)))
        assertEquals(SessionPhase.Ongoing, b.phaseAt(start))
        assertEquals(SessionPhase.AwaitingConfirmation, b.phaseAt(b.end))
        // Tanpa konfirmasi, sesi selesai otomatis 24 jam setelah berakhir.
        assertEquals(SessionPhase.Completed, b.phaseAt(b.end.plus(ConfirmationWindow)))
    }

    @Test
    fun confirmOrReport_changesHistoryStatus() {
        val start = t0.minusDays(3)
        val a = newBooking(start).also { store.markPaid(it.id) }
        val r = newBooking(start).also { store.markPaid(it.id) }
        store.confirm(a.id)
        store.report(r.id, SessionReport("Mentor tidak hadir", "Tidak hadir", emptyList()))
        val after = start.plusHours(2)
        assertEquals(SessionPhase.Completed, store.find(a.id)!!.phaseAt(after))
        assertEquals(SessionPhase.InReview, store.find(r.id)!!.phaseAt(after))
        // Laporan menahan penyelesaian otomatis.
        assertEquals(SessionPhase.InReview, store.find(r.id)!!.phaseAt(after.plusDays(3)))
    }

    @Test
    fun countdownFormats() {
        assertEquals("23 : 45 : 20", Duration.ofSeconds(23 * 3600 + 45 * 60 + 20L).countdown())
        assertEquals("14:31", Duration.ofSeconds(14 * 60 + 31L).countdown(withHours = false))
        assertEquals("00:00", Duration.ofSeconds(-5).countdown(withHours = false))
    }
}

class SlotPlanTest {
    private val now = LocalDateTime.of(2026, 11, 23, 9, 0)
    private val day = java.time.LocalDate.of(2026, 11, 24)
    private val at = { h: Int -> java.time.LocalTime.of(h, 0) }

    @Test
    fun splitsRangeIntoHourSlots() {
        val plan = planSlots("m", day, at(14), at(17), now, emptyList(), emptySet()) as SlotPlan.Ok
        assertEquals(listOf(14, 15, 16), plan.starts.map { it.hour })
        assertEquals("14.00–14.59 WIB", plan.starts.first().slotLabel())
    }

    @Test
    fun invalidWhenEndNotAfterStartOrInPast() {
        assertEquals(SlotPlan.Invalid, planSlots("m", day, at(17), at(14), now, emptyList(), emptySet()))
        assertEquals(SlotPlan.Invalid, planSlots("m", now.toLocalDate(), at(8), at(10), now, emptyList(), emptySet()))
    }

    @Test
    fun clashesWithBookedSessionOrOpenSlot() {
        val store = MentoringStore()
        val booked = store.book(mentorPerson("samantha-dira"), "Review Esai Beasiswa", day.atTime(15, 0), day.atTime(15, 59), 50_000)
            .copy(payment = PaymentStatus.Paid)
        val clash = planSlots("samantha-dira", day, at(14), at(17), now, listOf(booked), emptySet()) as SlotPlan.Clash
        assertEquals("15.00–15.59 WIB sudah dibooking Rayhan Pratama. Pilih rentang lain sebelum membuka slot.", clash.message)
        val open = planSlots("m", day, at(14), at(17), now, emptyList(), setOf(OpenSlot("m", day.atTime(16, 0)))) as SlotPlan.Clash
        assertEquals("16.00–16.59 WIB sudah dibuka. Pilih rentang lain sebelum membuka slot.", open.message)
    }
}
