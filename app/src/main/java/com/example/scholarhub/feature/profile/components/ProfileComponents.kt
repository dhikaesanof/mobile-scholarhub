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

/** Tombol "Keluar dari Akun" (Figma: pill #E6EFF5 tinggi 49, ikon + teks merah). */
@Composable
fun LogoutButton(onClick: () -> Unit, modifier: Modifier = Modifier, label: String = "Keluar dari Akun") {
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
            text = label,
            style = nunitoSans(14.431f, 20.615f, InterSemiBold, tracking = 0.1443f),
            color = ErrorRed,
        )
    }
}

