package com.example.scholarhub.feature.mentoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.mentoring.components.MentorCard
import com.example.scholarhub.feature.mentoring.components.MentorUi
import com.example.scholarhub.feature.mentoring.components.MentoringSessionCard
import com.example.scholarhub.feature.mentoring.components.MentoringSessionUi
import com.example.scholarhub.feature.mentoring.components.SampleMentors
import com.example.scholarhub.ui.components.MenteeBottomBar
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.ui.components.OutlinePillButton
import com.example.scholarhub.ui.components.ScholarHubTopBar
import com.example.scholarhub.ui.components.SectionHeader
import com.example.scholarhub.ui.components.SectionLink
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.NavyInk
import com.example.scholarhub.ui.theme.ScholarHubTheme

/** Isi Mentor Page. Masih contoh statis sampai data mentor dan jadwal tersedia. */
data class MentorListUiState(
    val mySession: MentoringSessionUi?,
    val mentors: List<MentorUi>,
)

val SampleMentorListUiState = MentorListUiState(
    mySession = MentoringSessionUi(
        schedule = "Selasa, 20 Okt • 19:30 WIB",
        title = "Review Esai LPDP",
        mentorLine = "Bersama Kak Samantha Dira",
    ),
    mentors = SampleMentors,
)

/** Mentor Page mentee (Figma 846:4909): sesi saya + daftar mentor awardee. */
@Composable
fun MentorListScreen(
    state: MentorListUiState = SampleMentorListUiState,
    onMentorClick: (String) -> Unit = {},
    onSeeAllSchedule: () -> Unit = {},
    onFilterClick: () -> Unit = {},
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
        bottomBar = { MenteeBottomBar(currentTab = MenteeNavTab.Mentor, onTabSelect = onTabSelect) },
    ) { padding ->
        // Figma: konten 369 dp di x=17 (frame 401 dp mulai x=1), sisi kanan 16 dp.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 17.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            state.mySession?.let { session ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader(
                        title = "Sesi Mentoring Saya",
                        titleStartPadding = 0.dp,
                        action = { SectionLink(text = "Semua Jadwal", onClick = onSeeAllSchedule) },
                    )
                    MentoringSessionCard(session = session, joinContentColor = Color.White)
                }
            }

            Column {
                SectionHeader(
                    title = "Cari Mentor Beasiswa",
                    subtitle = "Pilih mentor awardee untuk bimbingan personal",
                    titleStartPadding = 0.dp,
                    titleColor = NavyInk,
                    modifier = Modifier.height(48.dp),
                    action = { OutlinePillButton(label = "Filter", icon = R.drawable.ic_filter, onClick = onFilterClick) },
                )
                // Figma: kartu pertama 12 dp di bawah judul, jarak antarkartu 16 dp.
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    state.mentors.forEach { mentor ->
                        MentorCard(mentor = mentor, onBookingClick = { onMentorClick(mentor.id) })
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 402, heightDp = 1145)
@Composable
private fun MentorListScreenPreview() {
    ScholarHubTheme { MentorListScreen() }
}
