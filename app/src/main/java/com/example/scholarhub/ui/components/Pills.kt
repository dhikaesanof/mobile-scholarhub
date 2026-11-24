package com.example.scholarhub.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.ErrorContainer
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.OceanDeep
import com.example.scholarhub.ui.theme.Outline
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkyBorder
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.Slate600
import com.example.scholarhub.ui.theme.SurfaceCard

private val SoftSkyBorder = SkyBorder.copy(alpha = 0.6f)

/** Tag biru dengan ikon 16 dp, misalnya "D4, S1" atau "IPK >= 3.20". */
@Composable
fun IconTag(
    label: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(PillShape)
            .background(Sky)
            .border(1.dp, SoftSkyBorder, PillShape)
            // Border CSS di Figma menambah 1 dp tiap sisi; Compose menggambar border di dalam.
            .padding(horizontal = 9.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(16.dp))
        Text(
            text = label,
            maxLines = 1,
            softWrap = false,
            style = MaterialTheme.typography.labelSmall,
            color = OceanDeep,
        )
    }
}

/** Badge skor relevansi, misalnya "90% Relevan". */
@Composable
fun RelevanceBadge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .clip(PillShape)
            .background(SkySoft)
            .border(1.dp, SoftSkyBorder, PillShape)
            // Border CSS di Figma menambah 1 dp tiap sisi; Compose menggambar border di dalam.
            .padding(horizontal = 9.dp, vertical = 5.dp),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = OceanDeep,
    )
}

/** Chip sisa waktu pendaftaran berwarna merah, misalnya "14 Hari Lagi". Lebar minimum 95 dp mengikuti Figma. */
@Composable
fun DeadlineChip(text: String, modifier: Modifier = Modifier) {
    // Figma membungkus chip dengan wadah putih ber-padding vertikal 4.
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .widthIn(min = 95.dp)
                .clip(RoundedCornerShape(ScholarHubRadius.xl))
                .background(ErrorContainer)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_home_clock_red),
                contentDescription = null,
                modifier = Modifier.size(width = 9.75.dp, height = 11.375.dp),
            )
            Text(text = text, style = MaterialTheme.typography.labelSmall, color = ErrorRed)
        }
    }
}

/** Tombol pill putih berborder, misalnya chip "Filter" di Mentor Page (border #E2E8F0, teks #475569). */
@Composable
fun OutlinePillButton(
    label: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconWidth: Dp = 15.dp,
    iconHeight: Dp = 10.dp,
) {
    Row(
        modifier = modifier
            .clip(PillShape)
            .background(SurfaceCard)
            .border(1.dp, Outline, PillShape)
            .clickable(onClick = onClick)
            // Figma: padding 4/10 + stroke 1 yang ikut dihitung layout = 5/11 di Compose.
            .padding(horizontal = 11.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(iconWidth, iconHeight))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium, lineHeight = 16.sp),
            color = Slate600,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PillsPreview() {
    ScholarHubTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            IconTag(label = "Semester 5-8", icon = R.drawable.ic_home_layers)
            RelevanceBadge(text = "90% Relevan")
            DeadlineChip(text = "14 Hari Lagi")
            OutlinePillButton(label = "Filter", icon = R.drawable.ic_filter, onClick = {})
        }
    }
}
