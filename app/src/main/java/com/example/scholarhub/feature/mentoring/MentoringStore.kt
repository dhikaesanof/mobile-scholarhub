package com.example.scholarhub.feature.mentoring

import androidx.annotation.DrawableRes
import com.example.scholarhub.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Orang di sisi lain sesi: mentor (dilihat mentee) atau mentee (dilihat mentor). */
data class SessionPerson(
    val id: String,
    val name: String,
    val affiliation: String,
    @DrawableRes val photo: Int,
)

enum class PaymentStatus { Pending, Paid, Failed }

data class SessionReport(val type: String, val story: String, val attachments: List<String>)

/** Satu pemesanan sesi mentoring beserta pembayarannya. */
data class Booking(
    val id: String,
    val invoice: String,
    val mentor: SessionPerson,
    val mentee: SessionPerson,
    val topic: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val price: Int,
    val createdAt: LocalDateTime,
    val payment: PaymentStatus,
    val paidAt: LocalDateTime? = null,
    val confirmed: Boolean = false,
    val report: SessionReport? = null,
) {
    /** Batas bayar QRIS (Figma "Sisa 14:31"). */
    val payBefore: LocalDateTime get() = createdAt.plus(PaymentWindow)
}

/** Status kartu sesi (Figma `Card Mentoring Session`), dihitung dari waktu sekarang. */
enum class SessionPhase { Ongoing, StartingSoon, Scheduled, AwaitingConfirmation, InReview, Completed }

val PaymentWindow: Duration = Duration.ofMinutes(15)

/** Sesi selesai otomatis jika tidak dikonfirmasi dalam 24 jam setelah berakhir (Figma "Verifikasi Sesi Mentoring"). */
val ConfirmationWindow: Duration = Duration.ofHours(24)

/** Pembayaran yang belum dibayar sampai batasnya dianggap gagal. */
fun Booking.paymentStatusAt(now: LocalDateTime): PaymentStatus =
    if (payment == PaymentStatus.Pending && !now.isBefore(payBefore)) PaymentStatus.Failed else payment

/** Null jika belum dibayar; hanya pesanan yang sudah dibayar menjadi sesi. */
fun Booking.phaseAt(now: LocalDateTime): SessionPhase? = when {
    paymentStatusAt(now) != PaymentStatus.Paid -> null
    now.isBefore(start) -> if (Duration.between(now, start) <= Duration.ofHours(24)) SessionPhase.StartingSoon else SessionPhase.Scheduled
    now.isBefore(end) -> SessionPhase.Ongoing
    report != null -> SessionPhase.InReview
    confirmed || !now.isBefore(end.plus(ConfirmationWindow)) -> SessionPhase.Completed
    else -> SessionPhase.AwaitingConfirmation
}

val SessionPhase.isUpcoming: Boolean
    get() = this == SessionPhase.Ongoing || this == SessionPhase.StartingSoon || this == SessionPhase.Scheduled

private val Indonesian = Locale.forLanguageTag("id-ID")
private val PillDate = DateTimeFormatter.ofPattern("EEEE, d MMM yyyy", Indonesian)
private val ShortDate = DateTimeFormatter.ofPattern("EEE, d MMM", Indonesian)
private val LongDate = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Indonesian)
private val InvoiceDate = DateTimeFormatter.ofPattern("d MMM yyyy", Indonesian)
private val Time = DateTimeFormatter.ofPattern("HH:mm")

/** "Kamis, 22 Okt 2026" */
fun Booking.pillDate(): String = start.format(PillDate)

/** "16:00 - 17:00 WIB" */
fun Booking.timeRange(): String = "${start.format(Time)} - ${end.format(Time)} WIB"

/** "Sel, 24 Nov • 11:00–11:59 WIB" */
fun Booking.shortSchedule(): String = "${start.format(ShortDate)} • ${start.format(Time)}–${end.format(Time)} WIB"

/** "Selasa, 24 November 2026 • 11:00–11:59 WIB" */
fun Booking.longSchedule(): String = longSchedule(start, end)

fun BookingDraft.longSchedule(): String = longSchedule(start, end)

private fun longSchedule(start: LocalDateTime, end: LocalDateTime) =
    "${start.format(LongDate)} • ${start.format(Time)}–${end.format(Time)} WIB"

/** "24 Nov 2026 11:00 - 11:59 WIB" (invoice) */
fun Booking.invoiceSchedule(): String = "${start.format(InvoiceDate)} ${start.format(Time)} - ${end.format(Time)} WIB"

/** "23 Nov 2026 10:00 WIB" */
fun LocalDateTime.invoiceTime(): String = "${format(InvoiceDate)} ${format(Time)} WIB"

/** "Rp 50.000" */
fun rupiah(amount: Int): String = "Rp " + java.text.NumberFormat.getNumberInstance(Indonesian).format(amount)

/** "23 : 45 : 20" untuk hitung mundur, "14:31" untuk sisa waktu bayar. */
fun Duration.countdown(withHours: Boolean = true): String {
    val s = seconds.coerceAtLeast(0)
    return if (withHours) "%02d : %02d : %02d".format(s / 3600, s % 3600 / 60, s % 60) else "%02d:%02d".format(s / 60, s % 60)
}

val SampleMentee = SessionPerson("rayhan", "Rayhan Pratama", "Universitas Indonesia", R.drawable.profile_avatar)

/** Mentor yang masuk di sisi mentor (Figma "Selamat Datang, Samantha!"); sesi mentor = pesanan dengan mentor ini. */
const val LoggedInMentorId = "samantha-dira"

/**
 * Pemesanan sesi di memori untuk satu sesi aplikasi. Mulai kosong seperti kondisi awal Figma (Home Mentor `381:25256`,
 * Jadwal Sesi `280:5803`): pesanan baru muncul setelah mentee memesan slot yang dibuka mentor.
 * shortcut: belum ada backend pembayaran; "Periksa Status Pembayaran" langsung menandai lunas. Ganti saat API tersedia.
 */
class MentoringStore(private val clock: Clock = Clock.systemDefaultZone()) {
    fun now(): LocalDateTime = LocalDateTime.now(clock)

    private val state = MutableStateFlow(emptyList<Booking>())
    val bookings: StateFlow<List<Booking>> = state.asStateFlow()

    private val slotState = MutableStateFlow(seedSlots(now().toLocalDate()))

    /** Slot booking yang dibuka mentor (Figma "Buka Slot Booking"); mentee hanya bisa memesan slot ini. */
    val openSlots: StateFlow<Set<OpenSlot>> = slotState.asStateFlow()

    /** Membuka slot dari [plan] yang sudah divalidasi [planSlots]. */
    fun openSlots(mentorId: String, plan: SlotPlan.Ok) {
        slotState.update { it + plan.starts.map { start -> OpenSlot(mentorId, start) } }
    }

    fun closeSlot(slot: OpenSlot) {
        slotState.update { it - slot }
    }

    fun find(id: String): Booking? = state.value.firstOrNull { it.id == id }

    fun book(mentor: SessionPerson, topic: String, start: LocalDateTime, end: LocalDateTime, price: Int, mentee: SessionPerson = SampleMentee): Booking {
        val created = now()
        val number = state.value.size + 90
        val booking = Booking(
            id = "B$number",
            invoice = "INV/SH/${created.format(DateTimeFormatter.BASIC_ISO_DATE)}/%03d".format(number),
            mentor = mentor,
            mentee = mentee,
            topic = topic,
            start = start,
            end = end,
            price = price,
            createdAt = created,
            payment = PaymentStatus.Pending,
        )
        state.update { it + booking }
        return booking
    }

    fun markPaid(id: String) = edit(id) { it.copy(payment = PaymentStatus.Paid, paidAt = now()) }

    /** Mentee membatalkan sebelum bayar: pesanan gagal dan slotnya langsung bisa dipesan lagi. */
    fun cancel(id: String) = edit(id) { if (it.payment == PaymentStatus.Pending) it.copy(payment = PaymentStatus.Failed) else it }

    fun confirm(id: String) = edit(id) { it.copy(confirmed = true) }

    fun report(id: String, report: SessionReport) = edit(id) { it.copy(report = report) }

    private fun edit(id: String, transform: (Booking) -> Booking) {
        state.update { list -> list.map { if (it.id == id) transform(it) else it } }
    }

    /**
     * Mentor contoh tanpa akun (Sarah, Ahmad) membuka 10:00–13:59 untuk 10 hari ke depan agar mentee tetap bisa memesan.
     * Mentor yang masuk ([LoggedInMentorId]) mulai tanpa slot dan membukanya sendiri lewat "Buka Slot Booking".
     */
    private fun seedSlots(today: java.time.LocalDate): Set<OpenSlot> =
        listOf("sarah-aurelia", "ahmad-ibra").flatMap { mentor ->
            (0L..10L).flatMap { d -> (10..13).map { h -> OpenSlot(mentor, today.plusDays(d).atTime(h, 0)) } }
        }.toSet()

}

/** Slot satu jam (59 menit sesi) yang dibuka mentor. */
data class OpenSlot(val mentorId: String, val start: LocalDateTime)

/** Hasil validasi "Buka Slot Booking" (Figma `278:15247`, `280:5865`, `278:15406`). */
sealed interface SlotPlan {
    data class Ok(val starts: List<LocalDateTime>) : SlotPlan
    data object Invalid : SlotPlan
    data class Clash(val message: String) : SlotPlan
}

private val SlotTime = DateTimeFormatter.ofPattern("HH.mm")

/** "14.00–14.59 WIB" */
fun LocalDateTime.slotLabel(): String = "${format(SlotTime)}–${plusMinutes(59).format(SlotTime)} WIB"

/**
 * Memecah [from]–[to] di [date] menjadi slot per jam. Tidak valid jika jam selesai tidak setelah jam mulai atau
 * jam mulai sudah lewat; bentrok jika ada sesi yang sudah dibooking atau slot yang sudah dibuka di rentang itu.
 */
fun planSlots(
    mentorId: String,
    date: java.time.LocalDate,
    from: java.time.LocalTime,
    to: java.time.LocalTime,
    now: LocalDateTime,
    bookings: List<Booking>,
    slots: Set<OpenSlot>,
): SlotPlan {
    if (!to.isAfter(from) || date.atTime(from).isBefore(now)) return SlotPlan.Invalid
    val starts = generateSequence(date.atTime(from)) { it.plusHours(1) }.takeWhile { !it.plusHours(1).isAfter(date.atTime(to)) }.toList()
    if (starts.isEmpty()) return SlotPlan.Invalid
    bookings.firstOrNull { b -> b.mentor.id == mentorId && b.start in starts && b.paymentStatusAt(now) != PaymentStatus.Failed }?.let {
        return SlotPlan.Clash("${it.start.slotLabel()} sudah dibooking ${it.mentee.name}. Pilih rentang lain sebelum membuka slot.")
    }
    starts.firstOrNull { OpenSlot(mentorId, it) in slots }?.let {
        return SlotPlan.Clash("${it.slotLabel()} sudah dibuka. Pilih rentang lain sebelum membuka slot.")
    }
    return SlotPlan.Ok(starts)
}

/** Slot terbuka milik [mentorId] di [day] yang belum lewat dan belum dipesan (pesanan gagal bayar tidak dihitung). */
fun freeSlots(mentorId: String, day: java.time.LocalDate, bookings: List<Booking>, slots: Set<OpenSlot>, now: LocalDateTime): List<LocalDateTime> =
    slots.filter { it.mentorId == mentorId && it.start.toLocalDate() == day && it.start.isAfter(now) }
        .map { it.start }
        .filter { start -> bookings.none { b -> b.mentor.id == mentorId && b.start == start && b.paymentStatusAt(now) != PaymentStatus.Failed } }
        .sorted()
