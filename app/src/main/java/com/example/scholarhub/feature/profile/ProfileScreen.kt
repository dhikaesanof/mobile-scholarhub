package com.example.scholarhub.feature.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.profile.components.AchievementCard
import com.example.scholarhub.feature.profile.components.AchievementUi
import com.example.scholarhub.feature.profile.components.AddPillButton
import com.example.scholarhub.feature.profile.components.ChecklistItemUi
import com.example.scholarhub.feature.profile.components.LogoutButton
import com.example.scholarhub.feature.profile.components.ProfileSectionTitle
import com.example.scholarhub.feature.profile.components.ProgressRoadmapCard
import com.example.scholarhub.feature.profile.components.SettingsRow
import com.example.scholarhub.feature.profile.components.SettingsRowUi
import com.example.scholarhub.feature.profile.components.StatTile
import com.example.scholarhub.feature.profile.components.StatUi
import com.example.scholarhub.feature.profile.components.TrackerUi
import com.example.scholarhub.ui.components.MenteeBottomBar
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.NavyStrong
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineMist
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.SkyBorder
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.InterSemiBold
import com.example.scholarhub.ui.theme.nunitoSans

/** Isi Profil mentee. Masih contoh statis sampai profil tersimpan tersedia (fase berikutnya). */
data class ProfileUiState(
    val name: String,
    val subtitle: String,
    @DrawableRes val avatar: Int,
    val stats: List<StatUi>,
    val tracker: TrackerUi,
    val achievements: List<AchievementUi>,
    val settings: List<SettingsRowUi>,
)

object ProfileSettingId {
    const val Bookmark = "bookmark"
    const val History = "history"
    const val Security = "security"
}

val SampleProfileUiState = ProfileUiState(
    name = "Rayhan Pratama",
    subtitle = "Universitas Indonesia • S1 Teknik Informatika (Sem. 6)",
    avatar = R.drawable.profile_avatar,
    stats = listOf(
        StatUi("IPK Kumulatif", "3.84", "/4.00", TextMuted, R.drawable.ic_profile_ipk, 16.5.dp, 14.625.dp, SkySoft),
        StatUi("Skor IELTS", "7.5", "Acad.", Ocean, R.drawable.ic_profile_lang, 16.575.dp, 15.dp, SkyBorder),
        StatUi("Skor TOEFL", "650", null, TextMuted, R.drawable.ic_profile_lang, 16.575.dp, 15.dp, SkyBorder, compact = true),
    ),
    tracker = TrackerUi(
        scholarshipName = "Beasiswa Unggulan 2026",
        daysLeft = "25 Hari Lagi",
        closingDate = "Tutup 30 April 2026",
        progress = 0.6728f,
        progressLabel = "66% Selesai",
        checklist = listOf(
            ChecklistItemUi("Mendapat Surat Rekomendasi dari dosen pembimbing atau akademisi institusi asal.", done = true),
            ChecklistItemUi("Esai Rencana Kontribusi & Studi", done = true),
            ChecklistItemUi("LoA Unconditional & KTP", done = false),
        ),
        nextStep = "Segera siapkan LoA dan KTP",
    ),
    // Judul memakai baris baru eksplisit seperti teks di Figma.
    achievements = listOf(
        AchievementUi("gemastik", "Nasional", "2023", "Juara 1 Hackathon Nasional\nGEMASTIK 2023", "Puspresnas Kemendikbudristek RI", R.drawable.ic_profile_trophy, 15.dp, 15.dp, NavyStrong),
        AchievementUi("ieee", "Internasional", "2024", "Best Paper International\nConference on AI & Data", "IEEE CS Chapter • Tokyo, Jepang", R.drawable.ic_profile_medal, 13.333.dp, 17.5.dp, SkyBorder),
        AchievementUi("pkm", "Nasional", "2023", "Penerima Pendanaan PKM-\nKC 2023", "Ditjen Belmawa Kemendikbudristek RI", R.drawable.ic_profile_funding, 18.833.dp, 18.833.dp, OutlineMist),
    ),
    settings = listOf(
        SettingsRowUi(ProfileSettingId.Bookmark, "Bookmark", "4 program dalam pantauan aktif", R.drawable.ic_profile_bookmark, 11.667.dp, 15.dp, badge = 4),
        SettingsRowUi(ProfileSettingId.History, "Riwayat Bimbingan & Transaksi", "6 sesi selesai • Faktur pembayaran", R.drawable.ic_profile_history, 18.dp, 20.dp),
        SettingsRowUi(ProfileSettingId.Security, "Keamanan & Kata Sandi", "2FA aktif • Sandi diubah 30 hr lalu", R.drawable.ic_profile_lock, 13.333.dp, 17.5.dp),
    ),
)

/** Profil mentee (Figma 846:4522). */
@Composable
fun ProfileScreen(
    state: ProfileUiState = SampleProfileUiState,
    onBack: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onSettingClick: (String) -> Unit = {},
    onAddAchievement: () -> Unit = {},
    onLogout: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
) {
    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Profil", onBack = onBack) },
        bottomBar = { MenteeBottomBar(currentTab = MenteeNavTab.Profile, onTabSelect = onTabSelect) },
    ) { padding ->
        // Figma: konten 362 dp di x=20, 28 dp di bawah header, jarak antarsection 27.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(27.dp),
        ) {
            ProfileHeaderCard(state, onEditProfile)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileSectionTitle("Kualifikasi Akademik")
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    state.stats.forEach { StatTile(it, Modifier.weight(1f).fillMaxHeight()) }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileSectionTitle("Tracker & Reminder")
                // Figma: kartu 358 dp, 2 dp lebih sempit di tiap sisi dari kolom konten.
                ProgressRoadmapCard(state.tracker, onScholarshipClick = {}, modifier = Modifier.padding(horizontal = 2.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ProfileSectionTitle("Prestasi & Penghargaan") { AddPillButton(onClick = onAddAchievement) }
                state.achievements.forEach { AchievementCard(it, onMoreClick = {}) }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ProfileSectionTitle("Akun & Preferensi")
                Column(
                    modifier = Modifier.fillMaxWidth().cardSurface(shape = RoundedCornerShape(12.dp)).padding(4.dp),
                ) {
                    state.settings.forEach { row -> SettingsRow(row, onClick = { onSettingClick(row.id) }) }
                }
            }

            LogoutButton(onClick = onLogout)
        }
    }
}

@Composable
private fun ProfileHeaderCard(state: ProfileUiState, onEditProfile: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().cardSurface(shape = RoundedCornerShape(12.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Image(
            painter = painterResource(state.avatar),
            contentDescription = "Foto ${state.name}",
            modifier = Modifier.size(80.dp).clip(CircleShape).background(SkySoft),
            contentScale = ContentScale.Crop,
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = state.name, style = MaterialTheme.typography.titleMedium, color = Navy)
            Text(text = state.subtitle, style = MaterialTheme.typography.labelLarge, color = TextVariant)
        }
        // Figma: tombol navy dengan shadow solid 3 dp warna sama (#001645), ikon pensil 13.5.
        PrimaryButton(
            text = "Edit Profil Lengkap",
            onClick = onEditProfile,
            modifier = Modifier.fillMaxWidth(),
            solidShadow = Navy,
            shadowOffset = 3.dp,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            textStyle = nunitoSans(14f, 20f, InterSemiBold, tracking = 0.14f),
            iconGap = 4.dp,
            leadingContent = {
                Image(painter = painterResource(R.drawable.ic_profile_edit), contentDescription = null, modifier = Modifier.size(13.5.dp))
            },
        )
    }
}

@Preview(widthDp = 402, heightDp = 2052)
@Composable
private fun ProfileScreenPreview() {
    ScholarHubTheme { ProfileScreen() }
}
