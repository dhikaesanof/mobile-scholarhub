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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.geometry.Size
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
import com.example.scholarhub.ui.components.CroppedImage
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.FigmaCrop
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.InterSemiBold
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkySubtle
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans

private val CardShadow = Color(0x1C000000) // Figma: 0 4 2 hitam 11%
private val ReadIconBox = Color(0xFFD0D0D0)
private val Link = Color(0xFF0077A8)
private val BodyGray = Color(0xFF50545C)
private val LabelSemi = nunitoSans(11f, 16.5f, InterSemiBold)
private val BodySemi = nunitoSans(13f, 19.5f, InterSemiBold)
private val Caption = nunitoSans(11f, 16.5f, FontWeight.Normal)

/** Kategori filter. Mentee: Figma `255:7523`; mentor: `348:7222`. */
enum class NotificationCategory(val chipLabel: String) {
    Deadline("Deadline Pendaftaran"),
    Mentoring("Mentoring"),
    Info("Informasi Beasiswa"),
    Account("Keamanan"),
    NewBooking("Booking Baru"),
    SessionReminder("Pengingat Sesi"),
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
    /** Kartu versi baru (Figma `348:7194`): radius 12, padding 16, label Regular #0077A8. */
    val compact: Boolean = false,
)

/** [headline] true: judul "Hari Ini · 1 baru" (Figma `348:7207`); false: judul + pill "2 baru". */
@Immutable
data class NotificationGroup(val title: String, val date: String, val items: List<NotificationUi>, val headline: Boolean = false)

private fun body(text: String) = listOf(BodyPart(text))

/** Contoh statis mentee sesuai Figma `255:7523`; project belum punya sumber data notifikasi. */
val SampleNotificationGroups = listOf(
    NotificationGroup(
        "Hari Ini", "22 Okt 2026",
        listOf(
            NotificationUi(
                "confirm-session", NotificationCategory.Mentoring, "Mentoring", "Baru saja", "Konfirmasi Sesi Mentoringmu",
                body("Sesimu bersama Samantha Dira telah berakhir. Konfirmasi atau laporkan kendala sebelum 23 Okt, 17.00 WIB."),
                unread = true, icon = R.drawable.ic_notif_booking, iconSize = 24.dp, compact = true,
            ),
            NotificationUi(
                "tracker-reminder", NotificationCategory.Deadline, "Tracker & Reminder", "08.00", "Pengingat Pendaftaran",
                body("Pendaftaran Beasiswa Cakrawala 2026 ditutup 30 November. Formulir dan berkasmu belum dikirim. Yuk, selesaikan langkah berikutnya."),
                unread = true, icon = R.drawable.ic_notif_booking, iconSize = 24.dp, compact = true,
            ),
        ),
        headline = true,
    ),
    NotificationGroup(
        "20 Oktober", "20 Okt 2026",
        listOf(
            NotificationUi(
                "deadline-bu", NotificationCategory.Deadline, "Deadline Terdekat", "10 mnt lalu",
                "Pengingat Deadline: Beasiswa\nUnggulan 2026",
                body("Pendaftaran ditutup dalam 3 hari lagi (23 Oktober). Segera lengkapi berkas yang dibutuhkan!"),
                unread = true, icon = R.drawable.ic_notif_deadline,
            ),
            NotificationUi(
                "mentor-session", NotificationCategory.Mentoring, "Bimbingan Mentor", "2 Jam lalu",
                "Sesi Bimbingan Dimulai 1 Jam\nLagi",
                body("Review Esai Kontribusi LPDP bersama Kak Samantha Dira, S.Pd. pukul 19:30 WIB. Tautan Google Meet telah siap."),
                unread = true, mentorPhoto = R.drawable.mentor_avatar,
            ),
        ),
    ),
    NotificationGroup(
        "19 Oktober", "19 Okt 2026",
        listOf(
            NotificationUi(
                "reco-talenta", NotificationCategory.Info, "Info Beasiswa", "Kemarin, 09:15",
                "Rekomendasi Beasiswa Baru\n98% Relevan",
                body("Beasiswa Prestasi Talenta Muda 2026 baru saja dibuka dan sangat selaras dengan profil akademikmu (IPK 3.84)."),
                unread = true, icon = R.drawable.ic_notif_info, iconSize = 22.dp,
            ),
        ),
    ),
    NotificationGroup(
        "Minggu Lalu", "Oktober 2026",
        listOf(
            NotificationUi(
                "security", NotificationCategory.Account, "Keamanan Akun", "10 Okt 2026", "Keamanan Akun Terjaga",
                body("Kata sandi akun ScholarHub milikmu berhasil diperbarui."),
                unread = false, icon = R.drawable.ic_notif_security, iconSize = 18.dp,
            ),
        ),
    ),
)

/** Contoh statis mentor sesuai Figma `348:7222`. */
val SampleMentorNotificationGroups = listOf(
    NotificationGroup(
        "Hari Ini", "22 Okt 2026",
        listOf(
            NotificationUi(
                "mentor-soon", NotificationCategory.SessionReminder, "Pengingat Sesi", "Baru saja", "Sesi Mentoring Dimulai 30 Menit Lagi",
                body("Sesi Review Esai Beasiswa bersama Rayhan Pratama dimulai pukul 16.00 WIB. Siapkan materi dan buka room mentoring."),
                unread = true, icon = R.drawable.ic_notif_clock, iconSize = 24.dp, compact = true,
            ),
            NotificationUi(
                "mentor-booking", NotificationCategory.NewBooking, "Booking Baru", "15 mnt lalu", "Ada Booking Mentoring Baru",
                body("Rayhan Pratama telah memesan Review Esai Beasiswa untuk 23 Okt, pukul 16.00 WIB. Pembayaran berhasil."),
                unread = true, icon = R.drawable.ic_notif_booking, iconSize = 24.dp, compact = true,
            ),
        ),
        headline = true,
    ),
)

val MenteeNotificationCategories = listOf(NotificationCategory.Deadline, NotificationCategory.Mentoring, NotificationCategory.Info, NotificationCategory.Account)
val MentorNotificationCategories = listOf(NotificationCategory.NewBooking, NotificationCategory.SessionReminder)

/**
 * Riwayat Notifikasi (Figma mentee `255:7523`, mentor `348:7222`): status belum dibaca, filter kategori, dan notifikasi per waktu.
 * Menu ⋮ membuka sheet "Tandai semua dibaca" (`273:5574`). [readIds] disimpan pemanggil agar bertahan selama sesi masuk.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    groups: List<NotificationGroup> = SampleNotificationGroups,
    categories: List<NotificationCategory> = MenteeNotificationCategories,
    readIds: Set<String> = emptySet(),
    onRead: (Set<String>) -> Unit = {},
    onBack: () -> Unit = {},
    onOpen: (NotificationUi) -> Unit = {},
) {
    var filter by rememberSaveable { mutableStateOf<NotificationCategory?>(null) }
    var confirmReadAll by rememberSaveable { mutableStateOf(false) }
    val all = groups.map { g -> g.copy(items = g.items.map { if (it.id in readIds) it.copy(unread = false) else it }) }
    val unread = all.sumOf { g -> g.items.count { it.unread } }
    val visible = all.map { g -> g.copy(items = g.items.filter { filter == null || it.category == filter }) }.filter { it.items.isNotEmpty() }

    Scaffold(
        containerColor = Base,
        topBar = {
            ScholarHubDetailTopBar(
                title = "Riwayat Notifikasi",
                onBack = onBack,
                trailing = {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).clickable(role = Role.Button) { confirmReadAll = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(painterResource(R.drawable.ic_notif_more), "Opsi notifikasi", Modifier.size(width = 5.dp, height = 19.dp))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        ) {
            Row(
                Modifier.padding(top = 15.183.dp, bottom = 7.592.dp),
                horizontalArrangement = Arrangement.spacedBy(3.796.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(9.49.dp).clip(CircleShape).background(Ocean))
                Text("$unread notifikasi belum dibaca", style = nunitoSans(11f, 16.5f, FontWeight.Bold), color = Navy)
            }

            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 15.183.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NotificationChip(label = "Semua", selected = filter == null, count = unread, onClick = { filter = null })
                categories.forEach { cat -> NotificationChip(label = cat.chipLabel, selected = filter == cat, onClick = { filter = cat }) }
            }

            Column(Modifier.padding(bottom = 8.dp), verticalArrangement = Arrangement.spacedBy(22.775.dp)) {
                visible.forEach { group ->
                    Column(verticalArrangement = Arrangement.spacedBy(if (group.headline) 12.dp else 7.592.dp)) {
                        GroupHeader(group)
                        group.items.forEach { item ->
                            NotificationCard(item, onClick = {
                                onRead(readIds + item.id)
                                onOpen(item)
                            })
                        }
                    }
                }
                if (visible.isEmpty()) {
                    Text("Belum ada notifikasi untuk kategori ini.", style = BodySemi, color = TextVariant)
                }
                EndOfFeed("Semua riwayat notifikasi telah dimuat")
            }
        }
    }

    if (confirmReadAll) {
        ModalBottomSheet(
            onDismissRequest = { confirmReadAll = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            containerColor = SurfaceCard,
        ) {
            Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("Tandai semua dibaca", style = MaterialTheme.typography.titleMedium, color = Navy)
                Text(
                    "Semua notifikasi akan ditandai sudah dibaca. Kamu tetap dapat melihatnya di riwayat notifikasi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy,
                )
                PrimaryButton(
                    text = "Tandai Semua Dibaca",
                    onClick = {
                        onRead(readIds + groups.flatMap { g -> g.items.map { it.id } })
                        confirmReadAll = false
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    solidShadow = null,
                )
                OutlineButton("Batal", onClick = { confirmReadAll = false })
            }
        }
    }
}

/** Chip filter (Figma `255:7533`): aktif navy + angka di latar #EDF4FA; tidak aktif putih, teks #444650. */
@Composable
private fun NotificationChip(label: String, selected: Boolean, onClick: () -> Unit, count: Int? = null) {
    Row(
        Modifier.shadow(1.dp, PillShape, ambientColor = ShadowSubtle, spotColor = ShadowSubtle)
            .clip(PillShape)
            .background(if (selected) Navy else SurfaceCard)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = if (selected) 15.183.dp else 13.285.dp, vertical = 7.592.dp),
        horizontalArrangement = Arrangement.spacedBy(5.694.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = if (selected) SurfaceCard else TextVariant)
        if (count != null && count > 0) {
            Text(
                count.toString(),
                modifier = Modifier.clip(PillShape).background(SkySubtle).padding(horizontal = 5.694.dp),
                style = MaterialTheme.typography.labelLarge,
                color = Navy,
            )
        }
    }
}

@Composable
private fun GroupHeader(group: NotificationGroup) {
    val newCount = group.items.count { it.unread }
    Row(
        Modifier.fillMaxWidth().padding(top = if (group.headline) 0.dp else 3.796.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (group.headline) {
            val suffix = if (newCount > 0) " · $newCount baru" else ""
            Text(group.title + suffix, style = MaterialTheme.typography.titleMedium, color = Navy)
            Text(group.date, style = Caption, color = Color(0xFF84858D))
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(5.694.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(group.title, style = MaterialTheme.typography.titleMedium, color = Navy)
                if (newCount > 0) {
                    Text(
                        "$newCount baru",
                        modifier = Modifier.clip(PillShape).background(Sky).padding(horizontal = 7.592.dp, vertical = 1.898.dp),
                        style = LabelSemi,
                        color = TextVariant,
                    )
                }
            }
            Text(group.date, style = nunitoSans(11.388f, 15.183f, FontWeight.Normal), color = TextVariant.copy(alpha = 0.7f))
        }
    }
}

/**
 * Kartu notifikasi. Belum dibaca: latar #D9EEFB, kotak ikon putih, titik biru. Sudah dibaca: latar putih, kotak ikon #D0D0D0,
 * label dan judul abu. Varian [NotificationUi.compact] mengikuti kartu baru Figma `348:7194`.
 */
@Composable
private fun NotificationCard(item: NotificationUi, onClick: () -> Unit) {
    val shape = RoundedCornerShape(if (item.compact) 12.dp else 15.183.dp)
    Row(
        Modifier.fillMaxWidth()
            .shadow(3.dp, shape, ambientColor = CardShadow, spotColor = CardShadow)
            .clip(shape)
            .background(if (item.unread) Sky else SurfaceCard)
            .clickable(onClick = onClick)
            .padding(if (item.compact) 16.dp else 15.183.dp),
        horizontalArrangement = Arrangement.spacedBy(if (item.compact) 12.dp else 7.592.dp),
    ) {
        if (item.mentorPhoto != null) {
            Box {
                // Figma: foto 42 dp radius 28 dengan cincin 2 dp #006398 @40% di luar (crop 179% / −39.5%).
                CroppedImage(
                    image = item.mentorPhoto,
                    width = 42.dp,
                    height = 42.dp,
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
                    Modifier.align(Alignment.BottomEnd).offset(x = 2.43.dp, y = 0.43.dp).size(16.43.dp).clip(CircleShape).background(Ocean),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(painterResource(R.drawable.ic_notif_camera), null, Modifier.size(9.57.dp, 7.656.dp))
                }
            }
        } else {
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(if (item.compact) 12.dp else 11.388.dp)).background(if (item.unread) SurfaceCard else ReadIconBox),
                contentAlignment = Alignment.Center,
            ) {
                item.icon?.let { Image(painterResource(it), null, Modifier.size(item.iconSize)) }
            }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(if (item.compact) 6.dp else 1.329.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.label,
                    Modifier.weight(1f),
                    style = if (item.compact) Caption else LabelSemi,
                    color = when {
                        !item.unread -> TextVariant
                        item.compact -> Link
                        else -> Ocean
                    },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(if (item.compact) 6.dp else 5.684.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(item.time, style = if (item.compact) Caption else LabelSemi, color = if (item.compact) BodyGray else TextVariant)
                    if (item.unread) Box(Modifier.size(if (item.compact) 6.dp else 7.592.dp).clip(CircleShape).background(Ocean))
                }
            }
            Text(item.title, style = MaterialTheme.typography.titleSmall, color = if (item.unread) Navy else TextStrong)
            Text(
                buildAnnotatedString {
                    item.body.forEach { part ->
                        if (part.bold) withStyle(SpanStyle(color = TextStrong)) { append(part.text) } else append(part.text)
                    }
                },
                modifier = Modifier.padding(top = if (item.compact) 0.dp else 1.281.dp),
                style = BodySemi,
                color = if (item.compact) BodyGray else TextVariant,
            )
        }
    }
}

@Preview(widthDp = 402, heightDp = 1300)
@Composable
private fun NotificationsScreenPreview() {
    ScholarHubTheme { NotificationsScreen() }
}

@Preview(widthDp = 402, heightDp = 800)
@Composable
private fun MentorNotificationsPreview() {
    ScholarHubTheme { NotificationsScreen(groups = SampleMentorNotificationGroups, categories = MentorNotificationCategories) }
}
