package com.example.scholarhub.feature.notifications

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.CroppedImage
import com.example.scholarhub.ui.components.FigmaCrop
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.InkBlue
import com.example.scholarhub.ui.theme.InterSemiBold
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineMist
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.SkyBorder
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans

// Frame Riwayat Notifikasi di-scale ×0.949 secara merata; nilai Figma dipakai apa adanya.
private val Pad = 15.183.dp
private val CardShape = RoundedCornerShape(15.183.dp)
private val CardShadow = Color(0x1C000000) // Figma: 0 4 2 hitam 11%
private val ChipLabel = nunitoSans(11.388f, 15.183f, InterSemiBold, tracking = 0.2278f)
private val Meta = nunitoSans(11.388f, 15.183f, FontWeight.Normal)
private val Body = nunitoSans(13.285f, 21.589f, FontWeight.Normal)
private val Title = nunitoSans(17.081f, 23.487f, FontWeight.Bold)
private val GroupTitle = nunitoSans(15.183f, 20.877f, FontWeight.Bold, tracking = -0.3796f)

enum class NotificationCategory(val chipLabel: String, @DrawableRes val chipIcon: Int?, val iconWidth: Dp, val iconHeight: Dp) {
    Deadline("Deadline Pendaftaran", R.drawable.ic_notif_chip_deadline, 13.333.dp, 14.667.dp),
    Mentor("Bimbingan Mentor", R.drawable.ic_notif_chip_mentor, 13.333.dp, 12.dp),
    Documents("Status Berkas", R.drawable.ic_notif_chip_documents, 10.667.dp, 13.333.dp),
    Info("Info Beasiswa", R.drawable.ic_notif_chip_info, 14.667.dp, 12.dp),
    Account("Keamanan Akun", null, 0.dp, 0.dp),
}

/** Bagian isi notifikasi; [bold] ditulis SemiBold #141D21 (mis. nama mentor). */
@Immutable
data class BodyPart(val text: String, val bold: Boolean = false)

@Immutable
data class NotificationUi(
    val id: String,
    val category: NotificationCategory,
    /** Label kategori di kartu (mis. "Deadline Terdekat"), bisa berbeda dari label chip. */
    val label: String,
    val time: String,
    val title: String,
    val body: List<BodyPart>,
    val unread: Boolean,
    @DrawableRes val icon: Int? = null,
    val iconSize: Dp = 20.dp,
    /** Foto mentor menggantikan ikon untuk notifikasi bimbingan. */
    @DrawableRes val mentorPhoto: Int? = null,
)

@Immutable
data class NotificationGroup(val title: String, val date: String, val items: List<NotificationUi>)

/** Contoh statis sesuai Figma; project belum punya sumber data notifikasi. */
val SampleNotificationGroups = listOf(
    NotificationGroup(
        "Hari Ini", "20 Okt 2026",
        listOf(
            NotificationUi(
                "deadline-bu", NotificationCategory.Deadline, "Deadline Terdekat", "10 mnt lalu",
                "Pengingat Deadline: Beasiswa\nUnggulan 2026",
                listOf(BodyPart("Pendaftaran ditutup dalam 3 hari lagi (23 Oktober). Segera lengkapi berkas esai motivasimu!")),
                unread = true, icon = R.drawable.ic_notif_deadline,
            ),
            NotificationUi(
                "mentor-session", NotificationCategory.Mentor, "Bimbingan Mentor", "2 jam lalu",
                "Sesi Bimbingan Dimulai 1 Jam\nLagi",
                listOf(
                    BodyPart("Review Esai Kontribusi LPDP bersama "),
                    BodyPart("Kak Samantha Dira, S.Pd.", bold = true),
                    BodyPart(" pukul 19:30 WIB. Tautan Google Meet telah siap."),
                ),
                unread = true, mentorPhoto = R.drawable.mentor_avatar,
            ),
        ),
    ),
    NotificationGroup(
        "Kemarin", "19 Okt 2026",
        listOf(
            NotificationUi(
                "reco-talenta", NotificationCategory.Info, "Info Beasiswa", "Kemarin, 09:15",
                "Rekomendasi Beasiswa Baru\n98% Relevan",
                listOf(BodyPart("Beasiswa Prestasi Talenta Muda 2026 baru saja dibuka dan sangat selaras dengan profil akademikmu (IPK 3.84).")),
                // Figma menandai grup "1 baru" tetapi kartu ini tampil sebagai sudah dibaca; dipertahankan sebagai belum dibaca agar badge konsisten.
                unread = true, icon = R.drawable.ic_notif_info, iconSize = 22.dp,
            ),
        ),
    ),
    NotificationGroup(
        "Minggu Lalu", "Oktober 2026",
        listOf(
            NotificationUi(
                "security", NotificationCategory.Account, "Keamanan Akun", "10 Okt 2026",
                "Keamanan Akun Terjaga",
                listOf(BodyPart("Kata sandi akun ScholarHub milikmu berhasil diperbarui dari perangkat Chrome Mobile.")),
                unread = false, icon = R.drawable.ic_notif_security, iconSize = 18.dp,
            ),
        ),
    ),
)

/** Riwayat Notifikasi (Figma 846:4134): status belum dibaca, filter kategori, dan notifikasi per waktu. */
@Composable
fun NotificationsScreen(
    groups: List<NotificationGroup> = SampleNotificationGroups,
    onBack: () -> Unit = {},
    onOpen: (NotificationUi) -> Unit = {},
) {
    var filter by rememberSaveable { mutableStateOf<NotificationCategory?>(null) }
    // Status dibaca disimpan per sesi layar; belum ada penyimpanan notifikasi.
    var readIds by remember { mutableStateOf(emptySet<String>()) }
    val all = groups.map { g -> g.copy(items = g.items.map { if (it.id in readIds) it.copy(unread = false) else it }) }
    val unread = all.sumOf { g -> g.items.count { it.unread } }
    val visible = all.map { g -> g.copy(items = g.items.filter { filter == null || it.category == filter }) }.filter { it.items.isNotEmpty() }

    Scaffold(containerColor = Base, topBar = { ScholarHubDetailTopBar(title = "Riwayat Notifikasi", onBack = onBack) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            Row(
                Modifier.padding(start = Pad, end = Pad, top = Pad, bottom = 7.592.dp),
                horizontalArrangement = Arrangement.spacedBy(3.796.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(9.49.dp).clip(CircleShape).background(Ocean))
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Navy)) { append("$unread notifikasi") }
                        withStyle(SpanStyle(color = TextVariant)) { append(" belum dibaca") }
                    },
                    style = nunitoSans(11.388f, 15.183f, FontWeight.Normal, tracking = 0.2278f),
                )
            }

            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).height(38.433.dp).padding(horizontal = Pad),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NotificationChip(label = "Semua", selected = filter == null, count = unread, onClick = { filter = null })
                NotificationCategory.entries.filter { it.chipIcon != null }.forEach { cat ->
                    NotificationChip(
                        label = cat.chipLabel,
                        selected = filter == cat,
                        icon = cat.chipIcon,
                        iconWidth = cat.iconWidth,
                        iconHeight = cat.iconHeight,
                        onClick = { filter = cat },
                    )
                }
            }

            Column(
                Modifier.padding(start = Pad, end = Pad, top = Pad, bottom = 30.367.dp),
                verticalArrangement = Arrangement.spacedBy(22.775.dp),
            ) {
                visible.forEachIndexed { index, group ->
                    Column(verticalArrangement = Arrangement.spacedBy(7.592.dp)) {
                        GroupHeader(group, first = index == 0)
                        group.items.forEach { item ->
                            NotificationCard(item, onClick = {
                                readIds = readIds + item.id
                                onOpen(item)
                            })
                        }
                    }
                }
                if (visible.isEmpty()) {
                    Text("Belum ada notifikasi untuk kategori ini.", style = Body, color = TextVariant)
                }
                EndOfFeed("Semua riwayat notifikasi telah dimuat")
            }
        }
    }
}

/** Chip filter notifikasi: aktif navy + badge #5BB8FE; tidak aktif putih dengan ikon. */
@Composable
private fun NotificationChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    count: Int? = null,
    @DrawableRes icon: Int? = null,
    iconWidth: Dp = 0.dp,
    iconHeight: Dp = 0.dp,
) {
    Row(
        Modifier.shadow(1.dp, PillShape, ambientColor = ShadowSubtle, spotColor = ShadowSubtle)
            .clip(PillShape)
            .background(if (selected) Navy else SurfaceCard)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = if (selected) 15.183.dp else 13.285.dp, vertical = 7.592.dp),
        horizontalArrangement = Arrangement.spacedBy(if (count != null) 5.694.dp else 3.796.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let { Image(painterResource(it), null, Modifier.size(iconWidth, iconHeight)) }
        Text(label, style = ChipLabel, color = if (selected) SurfaceCard else TextVariant)
        if (count != null && count > 0) {
            Text(
                count.toString(),
                modifier = Modifier.clip(PillShape).background(Color(0xFF5BB8FE)).padding(horizontal = 5.694.dp, vertical = 1.898.dp),
                style = nunitoSans(9.49f, 11.862f, FontWeight.Bold, tracking = 0.2278f),
                color = InkBlue,
            )
        }
    }
}

@Composable
private fun GroupHeader(group: NotificationGroup, first: Boolean) {
    val newCount = group.items.count { it.unread }
    Row(
        Modifier.fillMaxWidth().padding(top = if (first) 3.796.dp else 7.592.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.694.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(group.title, style = GroupTitle, color = Navy)
            if (newCount > 0) {
                Text(
                    "$newCount baru",
                    modifier = Modifier.clip(PillShape).background(OutlineSky).padding(horizontal = 7.592.dp, vertical = 1.898.dp),
                    style = nunitoSans(10.439f, 20.877f, FontWeight.Bold, tracking = -0.3796f),
                    color = TextVariant,
                )
            }
        }
        Text(group.date, style = Meta, color = TextVariant.copy(alpha = 0.7f))
    }
}

/**
 * Kartu notifikasi. Belum dibaca: label kategori SemiBold #006398, judul navy, titik biru, kotak ikon #CCE5FF.
 * Sudah dibaca: label Regular abu, judul #141D21, tanpa titik, kotak ikon #E0E9EF.
 */
@Composable
private fun NotificationCard(item: NotificationUi, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .shadow(3.dp, CardShape, ambientColor = CardShadow, spotColor = CardShadow)
            .clip(CardShape)
            .background(SurfaceCard)
            .clickable(onClick = onClick)
            .padding(Pad),
        horizontalArrangement = Arrangement.spacedBy(7.592.dp),
    ) {
        if (item.mentorPhoto != null) {
            Box {
                // Figma: foto 50 × 51 radius 28 dengan cincin 2 dp #006398 @40% di luar (crop 179% / −39.5%).
                CroppedImage(
                    image = item.mentorPhoto,
                    width = 50.dp,
                    height = 51.dp,
                    crop = FigmaCrop(scaleY = 1.7902f, offsetY = -0.3951f),
                    contentDescription = "Foto mentor",
                    modifier = Modifier
                        .drawBehind {
                            val ring = 2.dp.toPx()
                            drawRoundRect(
                                color = Ocean.copy(alpha = 0.4f),
                                topLeft = Offset(-ring, -ring),
                                size = Size(size.width + ring * 2, size.height + ring * 2),
                                cornerRadius = CornerRadius(30.dp.toPx()),
                            )
                        }
                        .clip(RoundedCornerShape(28.dp))
                        .background(OutlineSky),
                )
                Box(
                    Modifier.align(Alignment.BottomEnd).offset(x = 3.55.dp, y = 3.55.dp).size(18.979.dp).clip(CircleShape).background(Ocean),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(painterResource(R.drawable.ic_notif_camera), null, Modifier.size(10.dp, 8.dp))
                }
            }
        } else {
            Box(
                Modifier.size(41.754.dp).clip(RoundedCornerShape(11.388.dp)).background(if (item.unread) SkyBorder else OutlineMist),
                contentAlignment = Alignment.Center,
            ) {
                item.icon?.let { Image(painterResource(it), null, Modifier.size(item.iconSize)) }
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.329.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.label,
                    style = if (item.unread) ChipLabel else Meta.copy(letterSpacing = ChipLabel.letterSpacing),
                    color = if (item.unread) Ocean else TextVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(5.684.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(item.time, style = Meta, color = TextVariant)
                    if (item.unread) Box(Modifier.size(7.592.dp).clip(CircleShape).background(Ocean))
                }
            }
            Text(item.title, style = Title, color = if (item.unread) Navy else TextStrong)
            Text(
                buildAnnotatedString {
                    item.body.forEach { part ->
                        if (part.bold) withStyle(SpanStyle(fontWeight = InterSemiBold, color = TextStrong)) { append(part.text) } else append(part.text)
                    }
                },
                modifier = Modifier.padding(top = 1.281.dp),
                style = Body,
                color = TextVariant,
            )
        }
    }
}

@Preview(widthDp = 402, heightDp = 1222)
@Composable
private fun NotificationsScreenPreview() {
    ScholarHubTheme { NotificationsScreen() }
}
