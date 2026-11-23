package com.example.scholarhub.feature.scholarship.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.scholarship.components.PreviewScholarshipCard
import com.example.scholarhub.feature.scholarship.components.ScholarshipCard
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.MenteeBottomBar
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubTopBar
import com.example.scholarhub.ui.components.SearchField
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.Outline
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.SkySubtle
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans

@Composable
fun ScholarshipListRoute(
    viewModel: ScholarshipListViewModel,
    onOpenDetail: (String) -> Unit,
    onOpenNotifications: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
    onOpenTransactions: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    ScholarshipListScreen(
        state = state,
        onOpenDetail = onOpenDetail,
        onRetry = viewModel::load,
        onOpenTransactions = onOpenTransactions,
        onOpenNotifications = onOpenNotifications,
        onTabSelect = onTabSelect,
    )
}

/**
 * Beasiswa Page (Figma `229:4134` awal, `229:4307` panel filter, `229:4488` hasil filter): judul + jumlah,
 * tombol Filter, pencarian nama/penyelenggara, chip filter aktif, dan katalog dari `scholarships.json`.
 */
@Composable
fun ScholarshipListScreen(
    state: ScholarshipListState,
    onOpenDetail: (String) -> Unit,
    onRetry: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
    onOpenTransactions: () -> Unit = {},
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filters by remember { mutableStateOf(CatalogFilters()) }
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    val allItems = (state as? ScholarshipListState.Ready)?.items.orEmpty()
    val shown = allItems.filterCatalog(query, filters)

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubTopBar(onArticleClick = onOpenTransactions, onNotificationClick = onOpenNotifications) },
        bottomBar = { MenteeBottomBar(currentTab = MenteeNavTab.Scholarship, onTabSelect = onTabSelect) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = "Eksplor Beasiswa", style = MaterialTheme.typography.titleMedium, color = Navy)
                    if (state is ScholarshipListState.Ready) {
                        Text(
                            text = "Menampilkan ${shown.size} dari ${allItems.size} Beasiswa",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ocean,
                        )
                    }
                }
                FilterButton(activeCount = filters.count, onClick = { showFilterSheet = true })
            }

            SearchField(query = query, onQueryChange = { query = it }, placeholder = "Cari beasiswa impianmu, negara, jurusan...")

            if (filters.count > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    filters.degrees.sortedBy(DegreeOrder::indexOf).forEach { degree ->
                        ActiveFilterChip(degree) { filters = filters.copy(degrees = filters.degrees - degree) }
                    }
                    filters.locations.sortedBy { it.ordinal }.forEach { location ->
                        ActiveFilterChip(location.label) { filters = filters.copy(locations = filters.locations - location) }
                    }
                    filters.fundings.sortedBy { it.ordinal }.forEach { funding ->
                        ActiveFilterChip(funding.label) { filters = filters.copy(fundings = filters.fundings - funding) }
                    }
                    filters.statuses.sortedBy { it.ordinal }.forEach { status ->
                        ActiveFilterChip(status.label) { filters = filters.copy(statuses = filters.statuses - status) }
                    }
                }
            }

            when (state) {
                ScholarshipListState.Loading -> Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Navy)
                }
                ScholarshipListState.Error -> Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(text = "Katalog beasiswa gagal dimuat.", style = MaterialTheme.typography.bodyMedium, color = TextVariant)
                    PrimaryButton(text = "Coba Lagi", onClick = onRetry)
                }
                is ScholarshipListState.Ready -> if (shown.isEmpty()) {
                    Text(
                        text = "Tidak ada beasiswa yang cocok. Ubah kata kunci atau filter.",
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        shown.forEach { item ->
                            ScholarshipCard(item = item.card, onDetailClick = { onOpenDetail(item.card.id) })
                        }
                    }
                    EndOfFeed("Semua beasiswa telah dimuat")
                }
            }
        }
    }

    if (showFilterSheet) {
        ScholarshipFilterSheet(
            initial = filters,
            countFor = { allItems.filterCatalog(query, it).size },
            onApply = {
                filters = it
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false },
        )
    }
}

/** Tombol "Filter" / "Filter · N" (Figma `Filter Indicator Chip`): pill putih, border #E2E8F0. */
@Composable
internal fun FilterButton(activeCount: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(SurfaceCard)
            .border(1.dp, Outline, PillShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painter = painterResource(R.drawable.ic_list_filter_alt), contentDescription = null, modifier = Modifier.size(20.dp))
        Text(
            text = if (activeCount > 0) "Filter · $activeCount" else "Filter",
            style = MaterialTheme.typography.labelLarge,
            color = Navy,
        )
    }
}

/** Chip filter aktif "S1 ×": latar #EDF4FA, border navy, radius 12; ketuk untuk menghapus. */
@Composable
private fun ActiveFilterChip(label: String, onRemove: () -> Unit) {
    val shape = RoundedCornerShape(ScholarHubRadius.md)
    Text(
        text = "$label ×",
        modifier = Modifier
            .clip(shape)
            .background(SkySubtle)
            .border(1.dp, Navy, shape)
            .clickable(onClickLabel = "Hapus filter $label", onClick = onRemove)
            .padding(horizontal = 13.dp, vertical = 11.dp),
        style = MaterialTheme.typography.labelLarge,
        color = Navy,
    )
}

@Preview(widthDp = 402, heightDp = 1168)
@Composable
private fun ScholarshipListScreenPreview() {
    ScholarHubTheme {
        ScholarshipListScreen(
            state = ScholarshipListState.Ready(
                items = listOf(CatalogItem(PreviewScholarshipCard.copy(relevance = null), RegistrationState.Open, 14, listOf("S1"))),
            ),
            onOpenDetail = {},
        )
    }
}
