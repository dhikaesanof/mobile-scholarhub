package com.example.scholarhub.feature.profile.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.OutlineMist
import com.example.scholarhub.ui.theme.OutlineSky
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.SkyBorder
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.InterSemiBold
import com.example.scholarhub.ui.theme.nunitoSans

private val Radius12 = RoundedCornerShape(12.dp)
private val Radius8 = RoundedCornerShape(8.dp)

// Teks Inter di frame Profil dipetakan ke Nunito Sans dengan ukuran/line height/tracking yang sama.
internal val CaptionBold = nunitoSans(10f, 14f, FontWeight.Bold, tracking = 0.4f)
private val RowTitle = nunitoSans(14f, 20f, InterSemiBold, tracking = 0.14f)
private val RowSubtitle = nunitoSans(12f, 16f, FontWeight.Normal)

/** Judul section di Profil: Title Medium dengan inset 4 dp. */
@Composable
fun ProfileSectionTitle(title: String, modifier: Modifier = Modifier, trailing: (@Composable () -> Unit)? = null) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = Navy)
        trailing?.invoke()
    }
}

// ---------- Kualifikasi Akademik ----------

@Immutable
data class StatUi(
    val label: String,
    val value: String,
    val suffix: String? = null,
    val suffixColor: Color = TextMuted,
    @DrawableRes val icon: Int,
    val iconWidth: Dp,
    val iconHeight: Dp,
    val iconBackground: Color,
    /** TOEFL di Figma memakai angka Bold 18/24 tanpa satuan. */
    val compact: Boolean = false,
)

/** Tile statistik 3 kolom (Figma `IPK Kumulatif`, `Skor IELTS`, `Target Program`). */
@Composable
fun StatTile(stat: StatUi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .cardSurface(shape = Radius12)
            .padding(8.dp),
        verticalArrangement = if (stat.compact) Arrangement.spacedBy(4.dp) else Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier.padding(bottom = 4.dp).size(28.dp).clip(Radius8).background(stat.iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Image(painter = painterResource(stat.icon), contentDescription = null, modifier = Modifier.size(stat.iconWidth, stat.iconHeight))
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = stat.label, style = CaptionBold, color = TextVariant)
            if (stat.compact) {
                Text(text = stat.value, style = nunitoSans(18f, 24f, FontWeight.Bold), color = Navy)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stat.value,
                        modifier = Modifier.alignByBaseline(),
                        style = nunitoSans(20f, 28f, FontWeight.Bold),
                        color = Navy,
                    )
                    stat.suffix?.let {
                        Text(text = it, modifier = Modifier.alignByBaseline(), style = CaptionBold, color = stat.suffixColor)
                    }
                }
            }
        }
    }
}

// ---------- Tracker & Reminder ----------

@Immutable
data class ChecklistItemUi(val text: String, val done: Boolean)

@Immutable
data class TrackerUi(
    val scholarshipName: String,
    val daysLeft: String,
    val closingDate: String,
    /** 0..1; Figma: 67.28% terisi dengan label "66% Selesai". */
    val progress: Float,
    val progressLabel: String,
    val checklist: List<ChecklistItemUi>,
    val nextStep: String,
)

/** Kartu "Progress Roadmap" (Figma `Section - Progress Roadmap Card`): border #E0E9EF, radius 16, padding 17. */
@Composable
fun ProgressRoadmapCard(tracker: TrackerUi, onScholarshipClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardSurface(shape = shape)
            .border(1.dp, OutlineMist, shape)
            .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "PROGRESS ROADMAP",
            style = nunitoSans(12f, 16.5f, FontWeight.Bold, tracking = 0.55f),
            color = TextVariant,
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val dropdownShape = RoundedCornerShape(10.759.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(dropdownShape)
                    .background(SurfaceCard)
                    .border(0.897.dp, Ocean, dropdownShape)
                    .clickable(onClick = onScholarshipClick)
                    .padding(12.897.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = tracker.scholarshipName,
                    modifier = Modifier.weight(1f),
                    style = nunitoSans(16f, 22f, FontWeight.ExtraBold),
                    color = Navy,
                )
                Image(
                    painter = painterResource(R.drawable.ic_profile_dropdown),
                    contentDescription = "Pilih beasiswa",
                    modifier = Modifier.padding(end = 0.9.dp).size(width = 10.308.dp, height = 6.356.dp),
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.ic_profile_deadline),
                        contentDescription = null,
                        modifier = Modifier.size(width = 17.dp, height = 16.dp),
                    )
                    Text(text = tracker.daysLeft, style = MaterialTheme.typography.labelSmall, color = ErrorRed)
                }
                Text(
                    text = tracker.closingDate,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = Ocean,
                    textAlign = TextAlign.End,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(PillShape).background(OutlineMist)) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(tracker.progress.coerceIn(0f, 1f))
                        .clip(PillShape)
                        .background(Ocean),
                )
            }
            Text(
                text = tracker.progressLabel,
                modifier = Modifier.fillMaxWidth(),
                style = nunitoSans(11f, 16.5f, FontWeight.Bold),
                color = Navy,
                textAlign = TextAlign.End,
            )
        }

        tracker.checklist.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Radius12)
                    .background(SkySoft)
                    .border(1.dp, OutlineSky.copy(alpha = 0.6f), Radius12)
                    .padding(13.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = painterResource(if (item.done) R.drawable.ic_profile_check_done else R.drawable.ic_profile_check_todo),
                    contentDescription = if (item.done) "Selesai" else "Belum selesai",
                    modifier = Modifier.size(width = 16.667.dp, height = 18.667.dp),
                )
                // Figma: item pertama #141D21, sisanya #001645.
                Text(
                    text = item.text,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (index == 0) TextStrong else Navy,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Radius12)
                .background(SkySoft)
                .border(1.dp, SkyBorder.copy(alpha = 0.8f), Radius12)
                .padding(13.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(Radius8).background(SurfaceCard).border(1.dp, SkyBorder, Radius8),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_profile_nextstep),
                    contentDescription = null,
                    modifier = Modifier.size(width = 13.5.dp, height = 14.4.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "LANGKAH BERIKUTNYA", style = MaterialTheme.typography.bodyMedium, color = TextVariant)
                Text(
                    text = tracker.nextStep,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ---------- Prestasi ----------

@Immutable
data class AchievementUi(
    val id: String,
    val level: String,
    val year: String,
    val title: String,
    val organizer: String,
    @DrawableRes val icon: Int,
    val iconWidth: Dp,
    val iconHeight: Dp,
    val iconBackground: Color,
)

/** Kartu prestasi (Figma `Prestasi 1..3`): ikon 40 dp, chip tingkat, tahun, judul, penyelenggara, menu ⋮. */
@Composable
fun AchievementCard(item: AchievementUi, onMoreClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().cardSurface(shape = Radius12).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(Radius12).background(item.iconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Image(painter = painterResource(item.icon), contentDescription = null, modifier = Modifier.size(item.iconWidth, item.iconHeight))
            }
            Column(verticalArrangement = Arrangement.spacedBy(1.4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.level,
                        modifier = Modifier
                            .clip(PillShape)
                            .background(SkyBorder.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Ocean,
                    )
                    Text(text = "• ${item.year}", style = CaptionBold, color = TextMuted)
                }
                Text(text = item.title, style = MaterialTheme.typography.titleSmall, color = Navy)
                Text(
                    text = item.organizer,
                    modifier = Modifier.padding(top = 0.6.dp),
                    style = RowSubtitle,
                    color = TextVariant,
                )
            }
        }
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).clickable(onClick = onMoreClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_profile_more),
                contentDescription = "Opsi prestasi",
                modifier = Modifier.size(width = 3.dp, height = 12.dp),
            )
        }
    }
}

/** Tombol "+ Tambah" kecil di judul Prestasi. */
@Composable
fun AddPillButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(SkyBorder.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painter = painterResource(R.drawable.ic_profile_plus), contentDescription = null, modifier = Modifier.size(9.333.dp))
        Text(text = "Tambah", style = MaterialTheme.typography.labelLarge, color = Ocean)
    }
}

// ---------- Akun & Preferensi ----------

@Immutable
data class SettingsRowUi(
    val id: String,
    val title: String,
    val subtitle: String,
    @DrawableRes val icon: Int,
    val iconWidth: Dp,
    val iconHeight: Dp,
    val badge: Int? = null,
)

/** Baris pengaturan 60 dp: ikon 36 dalam lingkaran #ECF5FB, judul + subjudul, badge opsional, chevron. */
@Composable
fun SettingsRow(row: SettingsRowUi, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Radius8)
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(SkySoft), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(row.icon), contentDescription = null, modifier = Modifier.size(row.iconWidth, row.iconHeight))
            }
            Column(modifier = Modifier.padding(bottom = 2.5.dp), verticalArrangement = Arrangement.spacedBy(5.5.dp)) {
                Text(text = row.title, style = RowTitle, color = Navy)
                Text(text = row.subtitle, style = RowSubtitle, color = TextVariant)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            row.badge?.let {
                Box(
                    modifier = Modifier.padding(end = 15.dp).size(24.dp).clip(CircleShape).background(OutlineMist),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = it.toString(), style = CaptionBold, color = Navy)
                }
            }
            Image(
                painter = painterResource(R.drawable.ic_profile_chevron),
                contentDescription = null,
                modifier = Modifier.size(width = 6.167.dp, height = 10.dp),
            )
        }
    }
}

/** Tombol "Keluar dari Akun" (Figma: pill #E6EFF5 tinggi 49, ikon + teks merah). */
@Composable
fun LogoutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(49.dp)
            .clip(PillShape)
            .background(OutlineSky)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(4.123.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painter = painterResource(R.drawable.ic_profile_logout), contentDescription = null, modifier = Modifier.size(15.462.dp))
        Text(
            text = "Keluar dari Akun",
            style = nunitoSans(14.431f, 20.615f, InterSemiBold, tracking = 0.1443f),
            color = ErrorRed,
        )
    }
}

