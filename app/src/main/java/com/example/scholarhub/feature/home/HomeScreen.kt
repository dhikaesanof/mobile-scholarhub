package com.example.scholarhub.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.feature.scholarship.components.ScholarshipCarouselCard
import com.example.scholarhub.feature.scholarship.components.ScholarshipCarouselUi
import com.example.scholarhub.ui.components.MascotBubbleBanner
import com.example.scholarhub.ui.components.MenteeBottomBar
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.ScholarHubTopBar
import com.example.scholarhub.ui.components.SectionHeader
import com.example.scholarhub.ui.components.SectionLink
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.ScholarHubSpacing
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.Sky
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.scholarhub.R
import androidx.compose.foundation.layout.size

// Figma Home Page (846:4756): kolom konten 360 dp di layar 402 dp, jadi margin 21 dp.
private val HomeHorizontalPadding = 21.dp

/** Isi beranda. Sesi dan favorit masih contoh statis; status profil dan ScholarMatch dari sesi aplikasi. */
data class HomeUiState(
    val userName: String,
    val favorites: List<ScholarshipCarouselUi>,
)

private val SampleFavorite = ScholarshipCarouselUi(
    id = "DM01",
    title = "Beasiswa Unggulan Kemendikbudristek 2026",
    degree = "S1",
    location = "Dalam Negeri",
    daysLeft = "14 Hari Lagi",
)

val SampleHomeUiState = HomeUiState(
    userName = "Rayhan",
    favorites = listOf(SampleFavorite, SampleFavorite),
)

@Composable
fun HomeScreen(
    state: HomeUiState = SampleHomeUiState,
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToSchedule: () -> Unit = {},
    /** Profil akademik sudah diisi (Edit Profil atau form ScholarMatch); jika belum, tampil kartu "Lengkapi profilmu" (Figma `209:4880`). */
    profileComplete: Boolean = false,
    onCompleteProfile: () -> Unit = {},
    /** Ada hasil ScholarMatch di sesi ini; banner berubah menjadi "Lihat Rekomendasi" (Figma `209:5286`). */
    hasRecommendation: Boolean = false,
    onOpenScholarMatch: () -> Unit = {},
    onOpenRecommendation: () -> Unit = {},
    /** Kartu sesi terdekat (Figma `239:13712`); null menampilkan "Belum ada sesi mentoring" (`215:6629`). */
    nearestSession: (@Composable () -> Unit)? = null,
    onFindMentor: () -> Unit = {},
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
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Yuk, lanjutkan persiapan beasiswanya!", style = MaterialTheme.typography.bodyMedium, color = Navy)
                    Image(painterResource(R.drawable.mascot_greeting), null, Modifier.size(width = 22.dp, height = 17.dp))
                }
            }

            if (!profileComplete) {
                Column(
                    Modifier.padding(horizontal = HomeHorizontalPadding).fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)).background(Sky).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("Lengkapi profilmu, yuk", style = MaterialTheme.typography.titleMedium, color = Navy)
                    Text(
                        "Tambahkan informasi kuliah agar rekomendasi beasiswa lebih sesuai denganmu. Skor bahasa dan prestasi bisa ditambahkan nanti.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Navy,
                    )
                    // Belum ada layar edit profil; informasi kuliah diisi di form ScholarMatch.
                    OutlineButton("Lengkapi Profil", onClick = onCompleteProfile)
                }
            }

            if (state.favorites.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(ScholarHubSpacing.sectionTitle)) {
                    SectionHeader(
                        title = "Beasiswa Populer",
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

            if (nearestSession != null) {
                HomeSection(title = "Sesi Mentoring Terdekat", titleStartPadding = 0.dp, action = {
                    SectionLink(text = "Semua Jadwal", onClick = onNavigateToSchedule)
                }) { nearestSession() }
            } else {
                HomeSection(title = "Sesi Mentoring Terdekat", titleStartPadding = 0.dp) {
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Sky).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text("Belum ada sesi mentoring", style = MaterialTheme.typography.titleSmall, color = Navy)
                        Text(
                            "Butuh bantuan menyiapkan beasiswa? Temukan mentor yang sesuai dan jadwalkan sesi pertamamu.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Navy,
                        )
                        OutlineButton("Cari Mentor", onClick = onFindMentor)
                    }
                }
            }

            HomeSection(title = "ScholarMatch") {
                MascotBubbleBanner(
                    message = if (hasRecommendation) {
                        "“Rekomendasi beasiswamu sudah siap. Yuk, lihat!”"
                    } else {
                        "Biar nggak salah arah, yuk temukan beasiswa yang paling pas buat dirimu!"
                    },
                    actionLabel = if (hasRecommendation) "Lihat Rekomendasi" else "Coba ScholarMatch",
                    onActionClick = if (hasRecommendation) onOpenRecommendation else onOpenScholarMatch,
                    // Banner Figma 358 dp, 2 dp lebih sempit dari kolom konten.
                    modifier = Modifier.padding(end = 2.dp),
                )
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
