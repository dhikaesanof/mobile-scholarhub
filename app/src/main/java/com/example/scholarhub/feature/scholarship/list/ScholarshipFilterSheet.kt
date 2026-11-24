package com.example.scholarhub.feature.scholarship.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.Steel
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.nunitoSans

private val OptionShape = RoundedCornerShape(ScholarHubRadius.md)

/**
 * Bottom sheet "Filter Beasiswa" (Figma `229:4605`). Pilihan baru berlaku setelah "Tampilkan N Beasiswa";
 * [countFor] menghitung hasil untuk pilihan yang sedang disusun.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScholarshipFilterSheet(
    initial: CatalogFilters,
    countFor: (CatalogFilters) -> Int,
    onApply: (CatalogFilters) -> Unit,
    onDismiss: () -> Unit,
) {
    var draft by remember { mutableStateOf(initial) }
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
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Filter Beasiswa", style = MaterialTheme.typography.titleMedium, color = Navy)
                Text("Pilih satu atau beberapa opsi per kategori.", style = nunitoSans(11f, 16.5f, FontWeight.Normal), color = Navy)
            }
            FilterGroup("Jenjang Pendidikan") {
                DegreeOrder.forEach { degree ->
                    FilterOption(degree, selected = degree in draft.degrees) {
                        draft = draft.copy(degrees = draft.degrees.toggle(degree))
                    }
                }
            }
            FilterGroup("Lokasi Studi") {
                StudyLocation.entries.forEach { location ->
                    FilterOption(location.label, selected = location in draft.locations) {
                        draft = draft.copy(locations = draft.locations.toggle(location))
                    }
                }
            }
            FilterGroup("Jenis Pendanaan") {
                FundingFilterOptions.forEach { funding ->
                    FilterOption(funding.label, selected = funding in draft.fundings) {
                        draft = draft.copy(fundings = draft.fundings.toggle(funding))
                    }
                }
            }
            FilterGroup("Status") {
                StatusFilter.entries.forEach { status ->
                    FilterOption(status.label, selected = status in draft.statuses) {
                        draft = draft.copy(statuses = draft.statuses.toggle(status))
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(OptionShape)
                    .background(SurfaceCard)
                    .border(1.dp, Ocean.copy(alpha = 0.23f), OptionShape)
                    .clickable { draft = CatalogFilters() },
                contentAlignment = Alignment.Center,
            ) {
                Text("Reset Filter", style = MaterialTheme.typography.labelLarge, color = Navy)
            }
            PrimaryButton(
                text = "Tampilkan ${countFor(draft)} Beasiswa",
                onClick = { onApply(draft) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                solidShadow = null,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FilterGroup(title: String, options: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = Navy)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { options() }
    }
}

/** Opsi filter: putih berborder #506076, aktif navy dengan teks #D9EEFB. */
@Composable
internal fun FilterOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .clip(OptionShape)
            .background(if (selected) Navy else SurfaceCard)
            .border(1.dp, if (selected) Navy else Steel, OptionShape)
            .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onClick() })
            // Figma: padding 12/10 + border 1 yang ikut dihitung layout.
            .padding(horizontal = 13.dp, vertical = 11.dp),
        style = MaterialTheme.typography.labelLarge,
        color = if (selected) Sky else Navy,
    )
}

internal fun <T> Set<T>.toggle(value: T): Set<T> = if (value in this) this - value else this + value
