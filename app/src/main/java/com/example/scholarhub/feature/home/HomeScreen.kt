package com.example.scholarhub.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.mentoring.components.MentoringSessionCard
import com.example.scholarhub.feature.mentoring.components.MentoringSessionUi
import com.example.scholarhub.feature.scholarship.components.ScholarshipCard
import com.example.scholarhub.feature.scholarship.components.ScholarshipCardUi
import com.example.scholarhub.feature.scholarship.components.ScholarshipCarouselCard
import com.example.scholarhub.feature.scholarship.components.ScholarshipCarouselUi
import com.example.scholarhub.feature.scholarship.components.ScholarshipTagUi
import com.example.scholarhub.ui.components.MascotBubbleBanner
import com.example.scholarhub.ui.components.MenteeBottomBar
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.ui.components.ScholarHubTopBar
import com.example.scholarhub.ui.components.SectionHeader
import com.example.scholarhub.ui.components.SectionLink
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.ScholarHubSpacing
import com.example.scholarhub.ui.theme.ScholarHubTheme

// Figma Home Page (846:4756): kolom konten 360 dp di layar 402 dp, jadi margin 21 dp.
private val HomeHorizontalPadding = 21.dp

/** Isi beranda. Masih contoh statis sampai data sesi, favorit, dan hasil ScholarMatch tersedia. */
data class HomeUiState(
    val userName: String,
    val upcomingSession: MentoringSessionUi?,
    val favorites: List<ScholarshipCarouselUi>,
    val matchResults: List<ScholarshipCardUi>,
)

private val SampleTags = listOf(
    ScholarshipTagUi("D4, S1", R.drawable.ic_home_school),
    ScholarshipTagUi("Semester 5-8", R.drawable.ic_home_layers),
    ScholarshipTagUi("IPK >= 3.20", R.drawable.ic_home_stars),
)

private val SampleMatch = ScholarshipCardUi(
    id = "SH28",
    provider = "KEMENDIKBUDRISTEK",
    title = "Beasiswa Unggulan 2026",
    degreeInfo = "Jenjang S1 / S2 / S3 • Dalam Negeri",
    deadline = "14 Hari Lagi",
    relevance = "90% Relevan",
    tags = SampleTags,
)

private val SampleFavorite = ScholarshipCarouselUi(
    id = "SH28",
    title = "Beasiswa Unggulan Kemendikbudristek 2026",
    degree = "S1",
    location = "Dalam Negeri",
    daysLeft = "Sisa 14 Hari",
)

val SampleHomeUiState = HomeUiState(
    userName = "Arza",
    upcomingSession = MentoringSessionUi(
        schedule = "Selasa, 20 Okt • 19:30 WIB",
        title = "Review Esai LPDP",
        mentorLine = "Bersama Kak Samantha Dira",
    ),
    favorites = listOf(SampleFavorite, SampleFavorite),
    matchResults = listOf(SampleMatch, SampleMatch),
)

@Composable
fun HomeScreen(
    state: HomeUiState = SampleHomeUiState,
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToSchedule: () -> Unit = {},
    onOpenScholarMatch: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenBookmarks: () -> Unit = {},
    onOpenArticles: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
) {
    Scaffold(
        containerColor = Base,
        topBar = {
            ScholarHubTopBar(
                onArticleClick = onOpenArticles,
                onNotificationClick = onOpenNotifications,
                onBookmarkClick = onOpenBookmarks,
            )
        },
        bottomBar = { MenteeBottomBar(currentTab = MenteeNavTab.Home, onTabSelect = onTabSelect) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(ScholarHubSpacing.section),
        ) {
            Column(Modifier.padding(horizontal = HomeHorizontalPadding)) {
                Text(text = "Selamat Datang, ${state.userName}!", style = MaterialTheme.typography.titleMedium, color = Navy)
                Text(text = "Yuk, lanjutkan persiapan beasiswanya🔥", style = MaterialTheme.typography.bodyMedium, color = Navy)
            }

            state.upcomingSession?.let { session ->
                HomeSection(title = "Sesi Mentoring Terdekat", titleStartPadding = 0.dp, action = {
                    SectionLink(text = "Semua Jadwal", onClick = onNavigateToSchedule)
                }) {
                    MentoringSessionCard(session = session)
                }
            }

            if (state.favorites.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(ScholarHubSpacing.sectionTitle)) {
                    SectionHeader(
                        title = "Beasiswa Favorit",
                        modifier = Modifier.padding(horizontal = HomeHorizontalPadding).height(28.dp),
                    )
                    // Kartu kedua terpotong di tepi layar seperti di Figma.
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = HomeHorizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(state.favorites) { item ->
                            ScholarshipCarouselCard(item = item, onClick = { onNavigateToDetail(item.id) })
                        }
                    }
                }
            }

            HomeSection(title = "ScholarMatch") {
                MascotBubbleBanner(
                    message = "Biar nggak salah arah, yuk temukan beasiswa yang paling pas buat profilmu!",
                    actionLabel = "Test Lagi",
                    onActionClick = onOpenScholarMatch,
                    // Banner Figma 358 dp, 2 dp lebih sempit dari kolom konten.
                    modifier = Modifier.padding(end = 2.dp),
                )
            }

            if (state.matchResults.isNotEmpty()) {
                HomeSection(title = "Hasil ScholarMatch") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        state.matchResults.forEach { item ->
                            ScholarshipCard(item = item, onDetailClick = { onNavigateToDetail(item.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSection(
    title: String,
    titleStartPadding: androidx.compose.ui.unit.Dp = 6.dp,
    action: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = HomeHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(ScholarHubSpacing.sectionTitle),
    ) {
        SectionHeader(title = title, titleStartPadding = titleStartPadding, action = action)
        content()
    }
}

@Preview(widthDp = 402, heightDp = 1635)
@Composable
private fun HomeScreenPreview() {
    ScholarHubTheme { HomeScreen() }
}
