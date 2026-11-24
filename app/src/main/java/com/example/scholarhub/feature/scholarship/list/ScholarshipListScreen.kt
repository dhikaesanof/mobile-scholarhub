package com.example.scholarhub.feature.scholarship.list

import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.scholarship.components.ScholarshipCard
import com.example.scholarhub.feature.scholarship.components.PreviewScholarshipCard
import com.example.scholarhub.ui.components.MascotInfoBanner
import com.example.scholarhub.ui.components.MenteeBottomBar
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubTopBar
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.NavyShadow
import com.example.scholarhub.ui.theme.OutlineCool
import com.example.scholarhub.ui.theme.OutlineMist
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans

/** Filter di Figma. Skema katalog v1 belum punya lokasi studi atau status pendanaan penuh. */
enum class CatalogFilter(val label: String, @DrawableRes val icon: Int?, val iconWidth: Dp, val iconHeight: Dp) {
    All("Semua", null, 0.dp, 0.dp),
    Abroad("Luar Negeri", R.drawable.ic_list_abroad, 13.786.dp, 12.1.dp),
    Domestic("Dalam Negeri", R.drawable.ic_list_domestic, 10.667.dp, 13.333.dp),
    FullyFunded("Fully Funded", R.drawable.ic_list_funded, 12.667.dp, 12.dp),
}

@Composable
fun ScholarshipListRoute(
    viewModel: ScholarshipListViewModel,
    onOpenDetail: (String) -> Unit,
    onOpenNotifications: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    ScholarshipListScreen(
        state = state,
        onOpenDetail = onOpenDetail,
        onRetry = viewModel::load,
        onOpenNotifications = onOpenNotifications,
        onTabSelect = onTabSelect,
    )
}

/** Beasiswa Page (Figma 846:4855): pencarian, banner panduan, filter, dan katalog dari `scholarships.json`. */
@Composable
fun ScholarshipListScreen(
    state: ScholarshipListState,
    onOpenDetail: (String) -> Unit,
    onRetry: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
) {
    var filter by rememberSaveable { mutableStateOf(CatalogFilter.All) }

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubTopBar(onNotificationClick = onOpenNotifications) },
        bottomBar = { MenteeBottomBar(currentTab = MenteeNavTab.Scholarship, onTabSelect = onTabSelect) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SearchBarPlaceholder()
            MascotInfoBanner(
                label = "Panduan Beasiswa",
                title = "Siap Raih Beasiswa Impian?",
                description = "Temukan pendanaan terbaik yang sesuai\nprofil akademikmu.",
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(R.drawable.ic_filter), contentDescription = null, modifier = Modifier.size(15.dp, 10.dp))
                        Text(text = "Eksplor Beasiswa", style = MaterialTheme.typography.titleMedium, color = Navy)
                    }
                    if (state is ScholarshipListState.Ready) {
                        Text(text = "${state.activeCount} program aktif", style = MaterialTheme.typography.labelLarge, color = TextVariant)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).height(37.5.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    CatalogFilter.entries.forEach { item ->
                        CatalogFilterChip(item, selected = item == filter, onClick = { filter = item })
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
                is ScholarshipListState.Ready -> {
                    if (filter == CatalogFilter.All) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            state.items.forEach { item ->
                                ScholarshipCard(item = item.card, onDetailClick = { onOpenDetail(item.card.id) })
                            }
                        }
                    } else {
                        Text(
                            text = "Filter \"${filter.label}\" belum tersedia karena data katalog belum memuat informasi ini.",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                        )
                    }
                }
            }
        }
    }
}

/** Search bar Figma. Pencarian katalog masuk fase berikutnya, jadi bar ini belum bisa diketik. */
@Composable
private fun SearchBarPlaceholder() {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .cardSurface(shape = shape)
            .border(1.dp, OutlineCool, shape),
        contentAlignment = Alignment.CenterStart,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_list_search),
            contentDescription = null,
            modifier = Modifier.padding(start = 14.dp).size(15.dp),
        )
        Text(
            text = "Cari beasiswa impianmu, negara, jurusan...",
            modifier = Modifier.padding(start = 41.dp, end = 41.dp),
            style = nunitoSans(14f, 17f, FontWeight.Normal),
            color = TextVariant.copy(alpha = 0.7f),
            maxLines = 1,
        )
        Image(
            painter = painterResource(R.drawable.ic_list_tune),
            contentDescription = "Filter lanjutan",
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 14.dp).size(15.dp),
        )
    }
}

/**
 * Chip filter katalog. Aktif: navy, teks putih Bold 13, shadow solid 2 dp #000C26.
 * Tidak aktif: putih, border #E0E9EF, ikon 14 dp dari kiri, teks #141D21.
 */
@Composable
private fun CatalogFilterChip(filter: CatalogFilter, selected: Boolean, onClick: () -> Unit) {
    val text = nunitoSans(13f, 19.5f, FontWeight.Bold)
    if (selected) {
        Row(
            modifier = Modifier
                .padding(top = 0.75.dp)
                .drawBehind {
                    drawRoundRect(
                        color = NavyShadow,
                        topLeft = Offset(0f, 2.dp.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(size.height / 2),
                    )
                }
                .clip(PillShape)
                .background(Navy)
                .clickable(role = Role.Tab, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Figma hanya punya chip "Semua" yang aktif; ikon chip lain dibuat putih agar terbaca di atas navy.
            filter.icon?.let {
                Image(painterResource(it), null, Modifier.size(filter.iconWidth, filter.iconHeight), colorFilter = ColorFilter.tint(SurfaceCard))
            }
            Text(text = filter.label, style = text, color = SurfaceCard)
        }
    } else {
        Row(
            modifier = Modifier
                .height(33.5.dp)
                .shadow(1.dp, PillShape, ambientColor = ShadowSubtle, spotColor = ShadowSubtle)
                .clip(PillShape)
                .background(SurfaceCard)
                .border(1.dp, OutlineMist, PillShape)
                .clickable(role = Role.Tab, onClick = onClick)
                .padding(start = 14.dp, end = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            filter.icon?.let { Image(painterResource(it), null, Modifier.size(filter.iconWidth, filter.iconHeight)) }
            Text(text = filter.label, modifier = Modifier.padding(bottom = 1.5.dp), style = text, color = TextStrong)
        }
    }
}

@Preview(widthDp = 402, heightDp = 1168)
@Composable
private fun ScholarshipListScreenPreview() {
    ScholarHubTheme {
        ScholarshipListScreen(
            state = ScholarshipListState.Ready(
                items = listOf(CatalogItem(PreviewScholarshipCard.copy(relevance = null), RegistrationState.Open, 14)),
                activeCount = 1,
            ),
            onOpenDetail = {},
        )
    }
}

