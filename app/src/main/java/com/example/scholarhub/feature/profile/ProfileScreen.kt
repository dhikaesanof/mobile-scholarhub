package com.example.scholarhub.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.profile.components.AchievementCard
import com.example.scholarhub.feature.profile.components.AchievementUi
import com.example.scholarhub.feature.profile.components.AddPillButton
import com.example.scholarhub.feature.profile.components.LogoutButton
import com.example.scholarhub.feature.profile.components.ProfileSectionTitle
import com.example.scholarhub.feature.profile.components.StatTile
import com.example.scholarhub.feature.profile.components.StatUi
import com.example.scholarhub.ui.components.EmptyNoticeCard
import com.example.scholarhub.ui.components.HelpRow
import com.example.scholarhub.ui.components.MenteeBottomBar
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.ui.components.ProfileInfoCard
import com.example.scholarhub.ui.components.ProfileInfoSection
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.NavyStrong
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.SkyBorder
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.TextMuted

/** Prestasi tingkat nasional/internasional memakai ikon piala navy (Figma `Prestasi 1`), lainnya medali. */
private fun Achievement.toUi() = if (level == "Nasional" || level == "Internasional") {
    AchievementUi(id, level, year.toString(), title, organizer, R.drawable.ic_profile_trophy, 15.dp, 15.dp, NavyStrong)
} else {
    AchievementUi(id, level, year.toString(), title, organizer, R.drawable.ic_profile_medal, 13.333.dp, 17.5.dp, SkyBorder)
}

/**
 * Profil mentee (Figma `143:2716`, kosong `143:2995`): kartu identitas, kualifikasi akademik, Tracker & Reminder,
 * prestasi, serta Bantuan & Keamanan. [readOnly] untuk mentor yang membuka profil mentee (Figma `239:13964`).
 */
@Composable
fun ProfileScreen(
    profile: MenteeProfile = MenteeProfile(),
    onBack: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onAddAchievement: () -> Unit = {},
    onRemoveAchievement: (String) -> Unit = {},
    onHelp: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
    readOnly: Boolean = false,
    /** Kartu Tracker & Reminder (`feature/tracker/TrackerCard`); diisi pemanggil karena butuh katalog dan checklist sesi. */
    tracker: @Composable () -> Unit = {},
) {
    val stats = listOf(
        StatUi("IPK Kumulatif", profile.gpa.ifBlank { "-" }, null, TextMuted, R.drawable.ic_profile_ipk, 16.5.dp, 14.625.dp, SkySoft),
        StatUi("Skor IELTS", profile.ieltsScore.ifBlank { "-" }, null, TextMuted, R.drawable.ic_profile_lang, 16.575.dp, 15.dp, SkyBorder),
        StatUi("Skor TOEFL", profile.toeflScore.ifBlank { "-" }, null, TextMuted, R.drawable.ic_profile_lang, 16.575.dp, 15.dp, SkyBorder),
    )
    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = if (readOnly) "Profil Mentee" else "Profil", onBack = onBack) },
        bottomBar = { if (!readOnly) MenteeBottomBar(currentTab = MenteeNavTab.Profile, onTabSelect = onTabSelect) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ProfileInfoCard(
                name = profile.name,
                photo = profile.photo,
                // Contoh profil yang dilihat mentor memakai foto Rayhan.
                fallbackPhoto = if (readOnly) R.drawable.profile_avatar else null,
                sections = listOf(
                    ProfileInfoSection(
                        null,
                        listOf(
                            "Universitas" to profile.campus.orEmpty(),
                            "Program Studi" to profile.major.orEmpty(),
                            "Semester" to (profile.semester?.toString() ?: ""),
                        ),
                    ),
                ),
                actionLabel = "Edit Profil",
                onAction = onEditProfile.takeUnless { readOnly },
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileSectionTitle("Kualifikasi Akademik")
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    stats.forEach { StatTile(it, Modifier.weight(1f).fillMaxHeight()) }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileSectionTitle("Tracker & Reminder")
                Box(Modifier.padding(horizontal = 2.dp)) { tracker() }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileSectionTitle("Prestasi & Penghargaan", trailing = if (readOnly) null else ({ AddPillButton(onClick = onAddAchievement) }))
                if (profile.achievements.isEmpty()) {
                    EmptyNoticeCard("Belum ada prestasi yang ditambahkan")
                }
                profile.achievements.forEach { achievement ->
                    var menu by remember { mutableStateOf(false) }
                    Box {
                        AchievementCard(achievement.toUi(), onMoreClick = { if (!readOnly) menu = true })
                        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                            DropdownMenuItem(
                                text = { Text("Hapus prestasi", style = MaterialTheme.typography.labelLarge, color = Navy) },
                                onClick = {
                                    menu = false
                                    onRemoveAchievement(achievement.id)
                                },
                            )
                        }
                    }
                }
            }

            if (!readOnly) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileSectionTitle("Bantuan & Keamanan")
                    HelpRow(R.drawable.ic_profile_help, 17.179.dp, 15.462.dp, "Pusat Bantuan", onHelp)
                    HelpRow(R.drawable.ic_profile_password, 13.333.dp, 17.5.dp, "Ganti Password", onChangePassword)
                }
                LogoutButton(onClick = onLogout)
            }
        }
    }
}

@Preview(widthDp = 402, heightDp = 1600)
@Composable
private fun ProfileScreenPreview() {
    ScholarHubTheme {
        ProfileScreen(
            profile = MenteeProfile(
                campus = "Universitas Indonesia", major = "Teknik Informatika", semester = 6, gpa = "3,84", ieltsScore = "7.5", toeflScore = "650",
                achievements = listOf(Achievement("a", "Juara 1 Hackathon Nasional GEMASTIK 2023", "Nasional", 2023, "Puspresnas Kemendikbudristek RI")),
            ),
        )
    }
}
