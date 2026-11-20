package com.example.scholarhub.feature.mentoring

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.mentoring.components.BookingDayUi
import com.example.scholarhub.feature.mentoring.components.DayChip
import com.example.scholarhub.feature.mentoring.components.MentorProfileCard
import com.example.scholarhub.feature.mentoring.components.MentorProfileUi
import com.example.scholarhub.feature.mentoring.components.SampleMentorProfile
import com.example.scholarhub.feature.mentoring.components.TimeSlotItem
import com.example.scholarhub.feature.mentoring.components.TimeSlotUi
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.SectionHeader
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowNavy
import com.example.scholarhub.ui.theme.TextMuted

/** Isi Detail Mentor. Masih contoh statis sampai data jadwal mentor tersedia. */
data class MentorDetailUiState(
    val profile: MentorProfileUi,
    val monthLabel: String,
    val monthShort: String,
    val days: List<BookingDayUi>,
    val slots: List<TimeSlotUi>,
    val price: String,
)

val SampleMentorDetailUiState = MentorDetailUiState(
    profile = SampleMentorProfile,
    monthLabel = "Oktober 2026",
    monthShort = "Okt",
    days = listOf(
        BookingDayUi("Sen", "Senin", 19), BookingDayUi("Sel", "Selasa", 20), BookingDayUi("Rab", "Rabu", 21),
        BookingDayUi("Kam", "Kamis", 22), BookingDayUi("Jum", "Jumat", 23), BookingDayUi("Sab", "Sabtu", 24),
        BookingDayUi("Min", "Minggu", 25),
    ),
    slots = listOf(
        TimeSlotUi("10:00 - 10:59 WIB"),
        TimeSlotUi("11:00 - 11:59 WIB"),
        TimeSlotUi("12:00 - 12:59 WIB"),
        TimeSlotUi("13:00 - 13:59 WIB", booked = true),
    ),
    price = "Rp 50.000",
)

private fun defaultSlotIndex(slots: List<TimeSlotUi>): Int =
    if (slots.getOrNull(1)?.booked == false) 1 else slots.indexOfFirst { !it.booked }

/** Ringkasan pilihan yang diteruskan ke layar pembayaran. */
data class BookingSelection(
    val mentorId: String,
    val mentorName: String,
    val mentorAffiliation: String,
    val day: BookingDayUi,
    val slot: TimeSlotUi,
    /** Format pendek untuk bar bawah, misalnya "Sel, 20 Okt • 11:00 - 11:59 WIB". */
    val summary: String,
    /** Format panjang untuk ringkasan pembayaran, misalnya "Selasa, 20 Oktober 2026 • 11:00 - 11:59 WIB". */
    val fullSchedule: String,
    val price: String,
)

/** Detail Mentor (Figma 846:4798): profil mentor, pilih tanggal + jam, lalu "Lanjut Bayar". */
@Composable
fun MentorDetailScreen(
    state: MentorDetailUiState = SampleMentorDetailUiState,
    onBack: () -> Unit = {},
    onContinueToPayment: (BookingSelection) -> Unit = {},
) {
    // Default mengikuti Figma: Sel 20 dan 11:00 terpilih.
    var dayIndex by rememberSaveable { mutableIntStateOf(1.coerceAtMost(state.days.lastIndex)) }
    var slotIndex by rememberSaveable { mutableIntStateOf(defaultSlotIndex(state.slots)) }
    val day = state.days.getOrNull(dayIndex)
    val slot = state.slots.getOrNull(slotIndex)?.takeUnless { it.booked }
    val summary = if (day != null && slot != null) "${day.dayShort}, ${day.date} ${state.monthShort} • ${slot.label}" else null

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Detail Mentor", onBack = onBack) },
        bottomBar = {
            BookingSummaryBar(
                price = state.price,
                summary = summary ?: "Pilih tanggal dan jam sesi",
                enabled = summary != null,
                onContinue = {
                    if (day != null && slot != null && summary != null) {
                        onContinueToPayment(
                            BookingSelection(
                                mentorId = state.profile.id,
                                mentorName = state.profile.name,
                                mentorAffiliation = state.profile.affiliation,
                                day = day,
                                slot = slot,
                                summary = summary,
                                fullSchedule = "${day.dayFull}, ${day.date} ${state.monthLabel} • ${slot.label}",
                                price = state.price,
                            ),
                        )
                    }
                },
            )
        },
    ) { padding ->
        // Figma: konten 369 dp di x=16, kartu profil 12 dp di bawah header, jarak ke booking 24.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 17.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            MentorProfileCard(profile = state.profile)

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SectionHeader(title = "Booking Mentor", titleStartPadding = 0.dp)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "Pilih Tanggal Sesi", style = MaterialTheme.typography.bodyLarge, color = Navy)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.ic_calendar_month),
                                contentDescription = null,
                                modifier = Modifier.size(width = 12.dp, height = 13.333.dp),
                            )
                            Text(text = state.monthLabel, style = MaterialTheme.typography.bodyLarge, color = Ocean)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        state.days.forEachIndexed { index, item ->
                            DayChip(day = item, selected = index == dayIndex, onClick = { dayIndex = index })
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    state.slots.forEachIndexed { index, item ->
                        TimeSlotItem(slot = item, selected = index == slotIndex, onClick = { slotIndex = index })
                    }
                }
            }
        }
    }
}

/**
 * Bar bawah sticky (Figma `Floating Sticky Bottom Bar CTA`): latar #F1F1F1, garis atas #E6EFF5,
 * shadow ke atas navy 8%. Tombol mengisi sisa lebar (maks 217) setelah ringkasan + jarak 61.
 */
@Composable
private fun BookingSummaryBar(price: String, summary: String, enabled: Boolean, onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 10.dp, ambientColor = ShadowNavy, spotColor = ShadowNavy)
            .background(Base),
    ) {
        HorizontalDivider(color = OutlineSky, thickness = 1.03.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 17.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(61.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = "Total Biaya Sesi", style = MaterialTheme.typography.bodyLarge, color = TextMuted)
                Text(text = price, style = MaterialTheme.typography.titleMedium, color = Navy)
                Text(text = summary, style = MaterialTheme.typography.labelLarge, color = Ocean)
            }
            PrimaryButton(
                text = "Lanjut Bayar",
                onClick = { if (enabled) onContinue() },
                modifier = Modifier.weight(1f).widthIn(max = 217.dp),
                containerColor = if (enabled) Navy else TextMuted,
                solidShadow = null,
                contentPadding = PaddingValues(8.dp),
                textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

@Preview(widthDp = 402, heightDp = 1234)
@Composable
private fun MentorDetailScreenPreview() {
    ScholarHubTheme { MentorDetailScreen() }
}
