package com.example.scholarhub.feature.mentoring

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scholarhub.feature.scholarship.list.FilterGroup
import com.example.scholarhub.feature.scholarship.list.FilterOption
import com.example.scholarhub.feature.scholarship.list.toggle
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.SkySubtle
import com.example.scholarhub.ui.theme.Steel
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.nunitoSans
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Atribut filter per mentor (Figma `220:9401`), diturunkan dari profil mentor. Masih contoh statis. */
data class MentorFacets(val fields: Set<String>, val scholarship: String, val campus: String)

val MentorFields = listOf("Review Esai", "Persiapan Wawancara")
val MentorScholarships = listOf("Beasiswa Unggulan", "Tanoto", "Pertamina")
val MentorCampuses = listOf("Universitas Brawijaya", "Universitas Indonesia", "ITB")

val MentorFacetsById = mapOf(
    "samantha-dira" to MentorFacets(MentorFields.toSet(), "Beasiswa Unggulan", "Universitas Brawijaya"),
    "sarah-aurelia" to MentorFacets(MentorFields.toSet(), "Tanoto", "Universitas Indonesia"),
    "ahmad-ibra" to MentorFacets(MentorFields.toSet(), "Pertamina", "ITB"),
)

data class MentorAvailability(val date: LocalDate, val from: LocalTime, val to: LocalTime)

data class MentorFilters(
    val fields: Set<String> = emptySet(),
    val scholarships: Set<String> = emptySet(),
    val campuses: Set<String> = emptySet(),
    val availability: MentorAvailability? = null,
) {
    val count: Int get() = fields.size + scholarships.size + campuses.size + if (availability != null) 1 else 0
}

/**
 * Pilihan dalam satu kategori digabung OR, antarkategori AND. Ketersediaan terpenuhi jika ada satu slot sesi penuh
 * (mulai sampai selesai) di dalam rentang jam pada tanggal itu.
 */
fun mentorMatches(mentorId: String, filters: MentorFilters, bookings: List<Booking>, slots: Set<OpenSlot>, now: LocalDateTime): Boolean {
    val facets = MentorFacetsById[mentorId] ?: return filters.count == 0
    val availability = filters.availability
    return (filters.fields.isEmpty() || facets.fields.any { it in filters.fields }) &&
        (filters.scholarships.isEmpty() || facets.scholarship in filters.scholarships) &&
        (filters.campuses.isEmpty() || facets.campus in filters.campuses) &&
        (
            availability == null || freeSlots(mentorId, availability.date, bookings, slots, now).any { start ->
                !start.toLocalTime().isBefore(availability.from) && !start.toLocalTime().plusMinutes(59).isAfter(availability.to)
            }
            )
}

private val Indonesian = Locale.forLanguageTag("id-ID")
private val DateLong = DateTimeFormatter.ofPattern("d MMMM yyyy", Indonesian)
private val DateShort = DateTimeFormatter.ofPattern("d MMM yyyy", Indonesian)
private val Hour = DateTimeFormatter.ofPattern("HH.mm")
private val Hours = (8..20).map { LocalTime.of(it, 0) }

/** "23 Nov 2026 · 14.00–16.00 WIB" untuk chip filter aktif. */
fun MentorAvailability.label(): String = "${date.format(DateShort)} · ${from.format(Hour)}–${to.format(Hour)} WIB"

/** Bottom sheet "Filter Mentor" (Figma `220:9401`). Pilihan baru berlaku setelah "Tampilkan N Mentor". */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorFilterSheet(
    initial: MentorFilters,
    today: LocalDate,
    countFor: (MentorFilters) -> Int,
    onApply: (MentorFilters) -> Unit,
    onDismiss: () -> Unit,
) {
    var draft by remember { mutableStateOf(initial) }
    val days = remember(today) { (1..7).map { today.plusDays(it.toLong()) } }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = SurfaceCard,
        dragHandle = {
            Box(Modifier.padding(top = 16.dp).size(width = 40.dp, height = 4.dp).clip(RoundedCornerShape(2.dp)).background(Steel))
        },
    ) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("Filter Mentor", style = MaterialTheme.typography.titleMedium, color = Navy)
                Text("Pilih satu atau beberapa opsi per kategori.", style = nunitoSans(11f, 16.5f, FontWeight.Normal), color = Navy)
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Ketersediaan Mentor", style = MaterialTheme.typography.titleSmall, color = Navy)
                val availability = draft.availability
                PickerField(
                    label = "Tanggal sesi",
                    helper = "Pilih tanggal yang kamu inginkan",
                    value = availability?.date?.format(DateLong) ?: "Semua tanggal",
                    options = listOf("Semua tanggal") + days.map { it.format(DateLong) },
                    onSelect = { index ->
                        draft = draft.copy(
                            availability = if (index == 0) null else MentorAvailability(days[index - 1], availability?.from ?: LocalTime.of(10, 0), availability?.to ?: LocalTime.of(14, 0)),
                        )
                    },
                )
                if (availability != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PickerField("Mulai (WIB)", "Jam paling awal", availability.from.format(Hour), Hours.map { it.format(Hour) }, Modifier.weight(1f)) { i ->
                            draft = draft.copy(availability = availability.copy(from = Hours[i], to = maxOf(availability.to, Hours[i].plusHours(1))))
                        }
                        PickerField("Selesai (WIB)", "Jam paling akhir", availability.to.format(Hour), Hours.map { it.format(Hour) }, Modifier.weight(1f)) { i ->
                            draft = draft.copy(availability = availability.copy(to = Hours[i], from = minOf(availability.from, Hours[i].minusHours(1))))
                        }
                    }
                }
                Text(
                    "Menampilkan mentor dengan slot sesi penuh di dalam rentang waktu pilihanmu.",
                    style = nunitoSans(11f, 16.5f, FontWeight.Normal),
                    color = Navy,
                )
            }
            FilterGroup("Bidang bimbingan") {
                MentorFields.forEach { FilterOption(it, it in draft.fields) { draft = draft.copy(fields = draft.fields.toggle(it)) } }
            }
            FilterGroup("Beasiswa yang diterima mentor") {
                MentorScholarships.forEach { FilterOption(it, it in draft.scholarships) { draft = draft.copy(scholarships = draft.scholarships.toggle(it)) } }
            }
            FilterGroup("Asal kampus mentor") {
                MentorCampuses.forEach { FilterOption(it, it in draft.campuses) { draft = draft.copy(campuses = draft.campuses.toggle(it)) } }
            }
            OutlineButton("Reset Filter", onClick = { draft = MentorFilters() })
            PrimaryButton(
                text = "Tampilkan ${countFor(draft)} Mentor",
                onClick = { onApply(draft) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                solidShadow = null,
            )
        }
    }
}

/** Isian pilihan (Figma `Profil / Kolom formulir`): label SemiBold 13, kotak #ECF5FB radius 12, keterangan 11. */
@Composable
private fun PickerField(
    label: String,
    helper: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (Int) -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Navy)
        Box {
            Text(
                value,
                Modifier.fillMaxWidth().clip(RoundedCornerShape(ScholarHubRadius.md)).background(SkySoft)
                    .clickable(role = Role.DropdownList) { open = true }.padding(12.dp),
                style = MaterialTheme.typography.labelLarge,
                color = Navy,
            )
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(text = { Text(option, color = Navy) }, onClick = {
                        onSelect(index)
                        open = false
                    })
                }
            }
        }
        Text(helper, style = nunitoSans(11f, 16.5f, FontWeight.Normal), color = Steel)
    }
}

/** Chip filter aktif kecil (Figma `220:9470`): latar #EDF4FA, border navy, radius 12, SemiBold 11. */
@Composable
fun MentorActiveChip(label: String, onRemove: () -> Unit) {
    val shape = RoundedCornerShape(ScholarHubRadius.md)
    Text(
        "$label ×",
        Modifier.clip(shape).background(SkySubtle).border(1.dp, Navy, shape)
            .clickable(onClickLabel = "Hapus filter $label", onClick = onRemove)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall,
        color = Navy,
    )
}
