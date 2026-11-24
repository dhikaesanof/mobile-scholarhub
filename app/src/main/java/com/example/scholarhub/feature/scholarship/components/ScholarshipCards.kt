package com.example.scholarhub.feature.scholarship.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.components.ButtonShape
import com.example.scholarhub.ui.components.CardShape
import com.example.scholarhub.ui.components.DeadlineChip
import com.example.scholarhub.ui.components.IconTag
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.RelevanceBadge
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.DividerGray
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkyBorder
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextVariant

@Immutable
data class ScholarshipTagUi(val label: String, @DrawableRes val icon: Int)

@Immutable
data class ScholarshipCardUi(
    val id: String,
    val provider: String,
    val title: String,
    val degreeInfo: String,
    /** Label sisa waktu ("14 Hari Lagi", "Ditutup", "Arsip"); null jika data tidak punya tenggat. */
    val deadline: String?,
    val tags: List<ScholarshipTagUi>,
    /** Hanya diisi jika kartu tampil sebagai hasil ScholarMatch. */
    val relevance: String? = null,
    /** Logo penyelenggara; null menampilkan inisial karena katalog belum punya data logo. */
    @DrawableRes val logo: Int? = R.drawable.logo_tutwuri,
)

@Immutable
data class ScholarshipCarouselUi(
    val id: String,
    val title: String,
    val degree: String,
    val location: String,
    val daysLeft: String,
    @DrawableRes val logo: Int = R.drawable.logo_tutwuri_carousel,
)

/**
 * Logo penyelenggara dalam kotak #ECF5FB berborder #CCE5FF, radius 12, padding 9, gambar 32 dp.
 * Di kartu daftar tingginya 48 dan lebarnya mengikuti isi; di carousel ukurannya 48 × 48.
 */
@Composable
fun ProviderLogo(
    @DrawableRes logo: Int?,
    modifier: Modifier = Modifier,
    square: Boolean = false,
    fallbackName: String = "",
) {
    Box(
        modifier = modifier
            .then(if (square) Modifier.size(48.dp) else Modifier.height(48.dp))
            .clip(ButtonShape)
            .background(SkySoft)
            .border(1.dp, SkyBorder, ButtonShape)
            .padding(9.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (logo != null) {
            Image(
                painter = painterResource(logo),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = providerInitials(fallbackName),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Ocean,
                )
            }
        }
    }
}

/** Dua huruf pertama dari kata penyelenggara, misalnya "BAZNAS RI" → "BR". */
internal fun providerInitials(name: String): String =
    name.split(' ', '/', '-').filter { it.firstOrNull()?.isLetter() == true }.take(2)
        .joinToString("") { it.first().uppercase() }.ifEmpty { "?" }

/** Kartu beasiswa (Figma `Scholarships Card`) untuk Home, katalog, dan hasil ScholarMatch. */
@Composable
fun ScholarshipCard(
    item: ScholarshipCardUi,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier,
    /** Konten tambahan sebelum tombol, misalnya status dan alasan hasil ScholarMatch. */
    extraContent: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .cardSurface()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                ProviderLogo(logo = item.logo, fallbackName = item.provider)
                item.relevance?.let { RelevanceBadge(text = it) }
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.fillMaxWidth().height(25.dp), contentAlignment = Alignment.CenterStart) {
                    Text(text = item.provider, style = MaterialTheme.typography.labelSmall, color = Ocean)
                }
                Text(text = item.title, style = MaterialTheme.typography.titleSmall, color = Navy)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = item.degreeInfo, style = MaterialTheme.typography.labelSmall, color = TextVariant)
                    item.deadline?.let { DeadlineChip(text = it) }
                }
            }
        }

        // Figma memotong tag yang tidak muat (overflow clip); scroll dimatikan agar tetap statis.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState(), enabled = false)
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item.tags.forEach { IconTag(label = it.label, icon = it.icon) }
        }

        extraContent?.invoke()

        PrimaryButton(
            text = "Lihat Detail Program",
            onClick = onDetailClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Kartu kecil 255 × 166 untuk carousel "Beasiswa Favorit" (Figma `Scholarships Carousel`). */
@Composable
fun ScholarshipCarouselCard(
    item: ScholarshipCarouselUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .size(width = 255.dp, height = 166.dp)
            .clip(CardShape)
            .background(SurfaceCard)
            .clickable(onClick = onClick)
            // Isi 218 dp rata kanan di Figma, sehingga sisi kiri 21 dp dan kanan 16 dp.
            // Padding bawah Figma (27) tidak dipakai: isi kartu (137 dp) memang melewati area padding
            // dan Figma tidak memotongnya, sedangkan Compose akan menyusutkan baris terakhir.
            .padding(start = 21.dp, end = 16.dp, top = 19.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ProviderLogo(logo = item.logo, square = true, fallbackName = item.title)
            Box(
                modifier = Modifier
                    .size(width = 30.dp, height = 21.dp)
                    .clip(RoundedCornerShape(7.76.dp))
                    .background(Sky),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = item.degree, style = MaterialTheme.typography.labelSmall, color = Navy)
            }
        }
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleSmall,
            color = Navy,
            minLines = 2,
            maxLines = 2,
        )
        HorizontalDivider(color = DividerGray, thickness = 0.5.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(74.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf(item.location, item.daysLeft).forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false,
                    style = MaterialTheme.typography.labelSmall,
                    color = Navy,
                )
            }
        }
    }
}

internal val PreviewScholarshipCard = ScholarshipCardUi(
    id = "SH28",
    provider = "KEMENDIKBUDRISTEK",
    title = "Beasiswa Unggulan 2026",
    degreeInfo = "Jenjang S1 / S2 / S3 • Dalam Negeri",
    deadline = "14 Hari Lagi",
    relevance = "90% Relevan",
    tags = listOf(
        ScholarshipTagUi("D4, S1", R.drawable.ic_home_school),
        ScholarshipTagUi("Semester 5-8", R.drawable.ic_home_layers),
        ScholarshipTagUi("IPK >= 3.20", R.drawable.ic_home_stars),
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 392)
@Composable
private fun ScholarshipCardsPreview() {
    ScholarHubTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ScholarshipCarouselCard(
                item = ScholarshipCarouselUi("SH28", "Beasiswa Unggulan Kemendikbudristek 2026", "S1", "Dalam Negeri", "Sisa 14 Hari"),
                onClick = {},
            )
            ScholarshipCard(item = PreviewScholarshipCard, onDetailClick = {})
        }
    }
}
