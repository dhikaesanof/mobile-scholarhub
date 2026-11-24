package com.example.scholarhub.feature.mentoring

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.mentoring.components.BookingDayUi
import com.example.scholarhub.feature.mentoring.components.DayChip
import com.example.scholarhub.feature.mentoring.components.MentorProfileCard
import com.example.scholarhub.feature.mentoring.components.MentorProfiles
import com.example.scholarhub.feature.mentoring.components.TimeSlotItem
import com.example.scholarhub.feature.mentoring.components.TimeSlotUi
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.ShadowNavy
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.Steel
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.nunitoSans
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val Indonesian = Locale.forLanguageTag("id-ID")
const val SessionPrice = 50_000
val MentoringTopics = listOf("Review Esai Beasiswa", "Simulasi Wawancara", "Strategi Persiapan Beasiswa")

/** Pesanan yang belum dibayar, diteruskan ke layar pembayaran. */
data class BookingDraft(
    val mentor: SessionPerson,
    val topic: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val price: Int,
)

/** Orang di kartu sesi untuk mentor [id] dari daftar mentor. */
fun mentorPerson(id: String): SessionPerson {
    val profile = MentorProfiles.firstOrNull { it.id == id } ?: MentorProfiles.first()
    return SessionPerson(profile.id, profile.name, profile.affiliation, profile.photo)
}

/**
 * Detail Mentor (Figma `209:5537`): profil, topik yang ingin dibahas, tanggal 7 hari ke depan, jam sesi, lalu
 * "Konfirmasi Booking". Jam yang sudah dipesan (termasuk pesananmu di [store]) tidak bisa dipilih.
 */
@Composable
fun MentorDetailScreen(
    mentorId: String,
    store: MentoringStore,
    onBack: () -> Unit,
    onConfirmBooking: (BookingDraft) -> Unit,
) {
    val profile = MentorProfiles.firstOrNull { it.id == mentorId } ?: MentorProfiles.first()
    val bookings by store.bookings.collectAsState()
    val openSlots by store.openSlots.collectAsState()
    val today = remember { store.now().toLocalDate() }
    val days = remember(today) { (1..7).map { today.plusDays(it.toLong()) } }
    var dayIndex by rememberSaveable { mutableIntStateOf(0) }
    var slotIndex by rememberSaveable { mutableIntStateOf(-1) }
    var topic by rememberSaveable { mutableStateOf(MentoringTopics.first()) }
    val day = days[dayIndex]
    val now = store.now()
    // Slot yang dibuka mentor di tanggal ini; yang sudah dipesan tampil "Terpesan".
    val free = freeSlots(profile.id, day, bookings, openSlots, now)
    val dayStarts = openSlots.filter { it.mentorId == profile.id && it.start.toLocalDate() == day && it.start.isAfter(now) }.map { it.start }.sorted()
    val slots = dayStarts.map { start ->
        TimeSlotUi("${start.format(TimeFormat)} - ${start.plusMinutes(59).format(TimeFormat)} WIB", booked = start !in free)
    }
    val chosenStart = dayStarts.getOrNull(slotIndex)?.takeIf { it in free }
    val draft = chosenStart?.let { BookingDraft(mentorPerson(profile.id), topic, it, it.plusMinutes(59), SessionPrice) }

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Detail Mentor", onBack = onBack) },
        bottomBar = {
            BookingSummaryBar(
                price = rupiah(SessionPrice),
                summary = draft?.let { "${it.start.format(ShortDay)} • ${it.start.format(TimeFormat)}–${it.end.format(TimeFormat)} WIB" }
                    ?: "Pilih tanggal dan jam sesi",
                enabled = draft != null,
                onContinue = { draft?.let(onConfirmBooking) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            MentorProfileCard(profile = profile)

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Booking Sesi Mentoring", style = MaterialTheme.typography.titleMedium, color = Navy)
                Column(Modifier.padding(bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Apa yang ingin kamu diskusikan?", style = MaterialTheme.typography.bodyLarge, color = Navy)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TopicDropdown(topic) { topic = it }
                        Text(
                            "Tulis kebutuhanmu agar mentor dapat menyiapkan sesi.",
                            style = nunitoSans(11f, 16.5f, FontWeight.Normal),
                            color = Steel,
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Pilih Sesi", style = MaterialTheme.typography.bodyLarge, color = Navy)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(painterResource(R.drawable.ic_calendar_month), null, Modifier.size(12.dp, 13.333.dp))
                            Text(day.format(MonthFormat), style = MaterialTheme.typography.bodyLarge, color = Ocean)
                        }
                    }
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        days.forEachIndexed { index, date ->
                            DayChip(day = date.toChip(), selected = index == dayIndex, onClick = {
                                dayIndex = index
                                slotIndex = -1
                            })
                        }
                    }
                }
                Column(Modifier.padding(bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (slots.isEmpty()) {
                        Text("Mentor belum membuka slot di tanggal ini.", style = MaterialTheme.typography.bodyMedium, color = Navy)
                    }
                    slots.forEachIndexed { index, slot ->
                        TimeSlotItem(slot = slot, selected = index == slotIndex, onClick = { if (!slot.booked) slotIndex = index })
                    }
                }
            }
        }
    }
}

private val TimeFormat = DateTimeFormatter.ofPattern("HH:mm")
private val ShortDay = DateTimeFormatter.ofPattern("EEE, d MMM", Indonesian)
private val MonthFormat = DateTimeFormatter.ofPattern("MMMM yyyy", Indonesian)

private fun LocalDate.toChip() = BookingDayUi(
    dayShort = dayOfWeek.getDisplayName(TextStyle.SHORT, Indonesian).take(3),
    dayFull = dayOfWeek.getDisplayName(TextStyle.FULL, Indonesian),
    date = dayOfMonth,
)

/** Pilihan topik (Figma `Profil / Kolom formulir`): latar #ECF5FB, border #006398, tinggi 44. */
@Composable
private fun TopicDropdown(value: String, onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(ScholarHubRadius.md)
    Box {
        Row(
            Modifier.fillMaxWidth().height(44.dp).clip(shape).background(SkySoft).border(1.dp, Ocean, shape)
                .clickable(role = Role.DropdownList) { open = true }.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(value, Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = Navy)
            Image(painterResource(R.drawable.ic_report_down), null, Modifier.size(20.dp))
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            MentoringTopics.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.labelLarge, color = Navy) },
                    onClick = {
                        onSelect(option)
                        open = false
                    },
                )
            }
        }
    }
}

/**
 * Bar bawah sticky (Figma `Floating Sticky Bottom Bar CTA`): latar #F1F1F1, garis atas #E6EFF5,
 * shadow ke atas navy 8%, ringkasan biaya dan jadwal, tombol "Konfirmasi Booking".
 */
@Composable
private fun BookingSummaryBar(price: String, summary: String, enabled: Boolean, onContinue: () -> Unit) {
    Column(Modifier.fillMaxWidth().shadow(elevation = 10.dp, ambientColor = ShadowNavy, spotColor = ShadowNavy).background(Base)) {
        HorizontalDivider(color = OutlineSky, thickness = 1.03.dp)
        Row(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 17.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("Total Biaya Sesi", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
                Text(price, style = MaterialTheme.typography.titleMedium, color = Navy)
                Text(summary, style = MaterialTheme.typography.labelLarge, color = Ocean)
            }
            PrimaryButton(
                text = "Konfirmasi Booking",
                onClick = { if (enabled) onContinue() },
                containerColor = if (enabled) Navy else TextMuted,
                solidShadow = null,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}
