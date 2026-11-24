package com.example.scholarhub.feature.mentor

import android.net.Uri
import com.example.scholarhub.feature.mentoring.Booking
import com.example.scholarhub.feature.mentoring.ConfirmationWindow
import com.example.scholarhub.feature.mentoring.LoggedInMentorId
import com.example.scholarhub.feature.mentoring.SessionPhase
import com.example.scholarhub.feature.mentoring.phaseAt
import com.example.scholarhub.feature.mentoring.rupiah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val MentorSpecializations = listOf("Review Esai", "Simulasi Wawancara", "Review CV", "Strategi Persiapan Beasiswa")

data class BankAccount(val bank: String, val number: String, val holder: String) {
    /** "BCA • 8271****90": nomor disamarkan kecuali 4 digit awal dan 2 digit akhir. */
    val masked: String get() = "$bank • " + if (number.length > 6) number.take(4) + "****" + number.takeLast(2) else number
}

data class Withdrawal(val id: String, val amount: Int, val at: LocalDateTime, val account: BankAccount)

/** Profil mentor yang masuk; mulai kosong (Figma `323:6672`), terisi setelah Lengkapi Profil (`372:24634`). */
data class MentorProfile(
    val name: String = "Samantha Dira",
    val photo: Uri? = null,
    val scholarship: String = "",
    val about: String = "",
    val campus: String? = null,
    val major: String? = null,
    val specializations: List<String> = emptyList(),
    val instagram: String = "",
    val email: String = "samanthadira@gmail.com",
    val account: BankAccount? = null,
    val withdrawals: List<Withdrawal> = emptyList(),
) {
    val complete: Boolean get() = scholarship.isNotBlank() && about.isNotBlank() && campus != null && major != null && specializations.isNotEmpty()
}

/** Sesi milik mentor yang sudah lewat (menunggu konfirmasi, ditinjau, selesai), terbaru dulu. */
fun List<Booking>.mentorHistory(now: LocalDateTime): List<Booking> =
    filter { it.mentor.id == LoggedInMentorId && it.phaseAt(now) in HistoryPhases }.sortedByDescending { it.start }

private val HistoryPhases = setOf(SessionPhase.AwaitingConfirmation, SessionPhase.InReview, SessionPhase.Completed)

/** Penghasilan = sesi selesai; saldo = penghasilan dikurangi penarikan. */
fun List<Booking>.mentorEarnings(now: LocalDateTime): List<Booking> = mentorHistory(now).filter { it.phaseAt(now) == SessionPhase.Completed }

private val NoteTime = DateTimeFormatter.ofPattern("d MMM, HH.mm", Locale.forLanguageTag("id-ID"))

/** Catatan di kartu Riwayat Mentoring (Figma `364:7709`). */
fun Booking.mentorNote(now: LocalDateTime): String? = when (phaseAt(now)) {
    SessionPhase.AwaitingConfirmation ->
        "Menunggu konfirmasi mentee hingga ${end.plus(ConfirmationWindow).format(NoteTime)} WIB. Penghasilan belum masuk saldo."
    SessionPhase.InReview -> "Laporan sesi sedang ditinjau. Penghasilan ditahan sampai laporan diselesaikan."
    SessionPhase.Completed -> "Sesi telah dikonfirmasi mentee. Penghasilan ${rupiah(price)} sudah masuk ke saldo."
    else -> null
}

/** Profil mentor di memori untuk satu sesi masuk. shortcut: penarikan disimulasikan; sambungkan ke payout saat backend ada. */
class MentorProfileStore(initial: MentorProfile = MentorProfile()) {
    private val state = MutableStateFlow(initial)
    val profile: StateFlow<MentorProfile> = state.asStateFlow()

    fun update(transform: (MentorProfile) -> MentorProfile) = state.update(transform)

    fun withdraw(amount: Int, at: LocalDateTime) = state.update { p ->
        val account = p.account ?: return@update p
        if (amount <= 0) p else p.copy(withdrawals = listOf(Withdrawal("W${p.withdrawals.size + 1}", amount, at, account)) + p.withdrawals)
    }
}
