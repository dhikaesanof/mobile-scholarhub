package com.example.scholarhub.feature.mentoring

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MentorFilterTest {
    private val now = LocalDateTime.of(2026, 11, 23, 9, 0)
    private val day = LocalDate.of(2026, 11, 24)
    private val ids = listOf("samantha-dira", "sarah-aurelia", "ahmad-ibra")
    private val slots = ids.flatMap { id -> (10..12).map { h -> OpenSlot(id, day.atTime(h, 0)) } }.toSet()
    private fun match(f: MentorFilters, bookings: List<Booking> = emptyList()) = ids.filter { mentorMatches(it, f, bookings, slots, now) }

    @Test
    fun categoriesCombineOrWithinAndAcross() {
        assertEquals(ids, match(MentorFilters()))
        assertEquals(listOf("samantha-dira", "ahmad-ibra"), match(MentorFilters(scholarships = setOf("Beasiswa Unggulan", "Pertamina"))))
        assertEquals(emptyList<String>(), match(MentorFilters(scholarships = setOf("Tanoto"), campuses = setOf("ITB"))))
    }

    @Test
    fun availabilityNeedsAFullFreeSlotInsideRange() {
        // Slot 10:00–10:59 masuk rentang 10.00–11.00; tidak ada slot dibuka pukul 13:00.
        assertEquals(ids, match(MentorFilters(availability = MentorAvailability(day, LocalTime.of(10, 0), LocalTime.of(11, 0)))))
        assertEquals(emptyList<String>(), match(MentorFilters(availability = MentorAvailability(day, LocalTime.of(13, 0), LocalTime.of(14, 0)))))
        // Slot yang sudah dipesan tidak dihitung.
        val store = MentoringStore()
        val booked = store.book(mentorPerson("sarah-aurelia"), "Review Esai Beasiswa", day.atTime(10, 0), day.atTime(10, 59), 50_000)
            .copy(payment = PaymentStatus.Paid)
        assertEquals(listOf("samantha-dira", "ahmad-ibra"), match(MentorFilters(availability = MentorAvailability(day, LocalTime.of(10, 0), LocalTime.of(11, 0))), listOf(booked)))
    }
}
