package com.example.scholarhub.feature.mentoring.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.Disabled
import com.example.scholarhub.ui.theme.DisabledSoft
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowMedium
import com.example.scholarhub.ui.theme.ShadowStrong
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextMuted

private val ChipShape = RoundedCornerShape(ScholarHubRadius.md)

@Immutable
data class BookingDayUi(val dayShort: String, val dayFull: String, val date: Int)

@Immutable
data class TimeSlotUi(val label: String, val booked: Boolean = false)

/**
 * Tombol tanggal 62 dp (Figma `Button - Day`). Tidak aktif: putih, border #E6EFF5, tinggi 74.
 * Aktif: navy, tanpa border, tinggi 72, nama hari Bold #D9EEFB dan tanggal ExtraBold putih.
 */
@Composable
fun DayChip(day: BookingDayUi, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val base = modifier.width(62.dp)
    val chipModifier = if (selected) {
        base
            .shadow(elevation = 4.dp, shape = ChipShape, ambientColor = ShadowMedium, spotColor = ShadowMedium)
            .clip(ChipShape)
            .background(Navy)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 10.dp)
    } else {
        base
            .shadow(elevation = 1.dp, shape = ChipShape, ambientColor = ShadowSubtle, spotColor = ShadowSubtle)
            .clip(ChipShape)
            .background(SurfaceCard)
            .border(1.dp, OutlineSky, ChipShape)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 11.dp)
    }
    val text = MaterialTheme.typography.bodyLarge
    Column(
        modifier = chipModifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // Figma: nama hari Inter 16/24, dipetakan ke Nunito Sans.
        Text(
            text = day.dayShort,
            style = if (selected) text.copy(fontWeight = FontWeight.Bold) else text,
            color = if (selected) Sky else TextMuted,
        )
        Text(
            text = day.date.toString(),
            style = if (selected) text.copy(fontWeight = FontWeight.ExtraBold) else text,
            color = if (selected) Color.White else Navy,
        )
    }
}

/**
 * Baris jam (Figma `Button - Jam 1: Tersedia`, varian Default/Variant2/Variant3):
 * tersedia (putih + chip "Pilih Jam"), terpilih (navy + "Terpilih"), booked (abu, tidak bisa diklik).
 */
@Composable
fun TimeSlotItem(slot: TimeSlotUi, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val container: Color
    val circle: Color
    val icon: Color
    val text: Color
    val chipBg: Color
    val chipText: Color
    val chipLabel: String
    when {
        slot.booked -> {
            container = Disabled; circle = DisabledSoft; icon = TextMuted; text = TextMuted
            chipBg = DisabledSoft; chipText = TextMuted; chipLabel = "Booked"
        }
        selected -> {
            container = Navy; circle = Sky; icon = Navy; text = Sky
            chipBg = Color.Transparent; chipText = Sky; chipLabel = "Terpilih"
        }
        else -> {
            container = SurfaceCard; circle = SkySoft; icon = Ocean; text = Navy
            chipBg = Sky; chipText = Ocean; chipLabel = "Pilih Jam"
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            // Figma: drop shadow 0 2 2 hitam 25% (booked 0 2 1).
            .shadow(elevation = if (slot.booked) 1.dp else 2.dp, shape = ChipShape, ambientColor = ShadowStrong, spotColor = ShadowStrong)
            .clip(ChipShape)
            .background(container)
            .clickable(enabled = !slot.booked, role = Role.RadioButton, onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(circle), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(R.drawable.ic_slot_clock),
                    contentDescription = null,
                    modifier = Modifier.size(13.333.dp),
                    colorFilter = ColorFilter.tint(icon),
                )
            }
            Text(
                text = slot.label,
                modifier = Modifier.padding(bottom = 1.5.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = text,
            )
        }
        Text(
            text = chipLabel,
            modifier = Modifier
                .clip(PillShape)
                .background(chipBg)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelLarge,
            color = chipText,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 401)
@Composable
private fun BookingPickerPreview() {
    ScholarHubTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                DayChip(BookingDayUi("Sen", "Senin", 19), selected = false, onClick = {})
                DayChip(BookingDayUi("Sel", "Selasa", 20), selected = true, onClick = {})
            }
            TimeSlotItem(TimeSlotUi("10:00 - 10:59 WIB"), selected = false, onClick = {})
            TimeSlotItem(TimeSlotUi("11:00 - 11:59 WIB"), selected = true, onClick = {})
            TimeSlotItem(TimeSlotUi("13:00 - 13:59 WIB", booked = true), selected = false, onClick = {})
        }
    }
}
