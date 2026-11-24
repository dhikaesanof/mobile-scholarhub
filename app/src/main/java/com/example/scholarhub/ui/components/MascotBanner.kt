package com.example.scholarhub.ui.components

import androidx.compose.foundation.Canvas
import com.example.scholarhub.ui.theme.nunitoSans
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.OutlineMist
import com.example.scholarhub.ui.theme.Ocean
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.BubbleBorder
import com.example.scholarhub.ui.theme.BubbleSurface
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ScholarHubTextStyles
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.TextBubble

/** Maskot hiu bajak laut dengan crop Figma (gambar 152.89% × 122.35%, geser −29.1% / −9.79%). */
@Composable
fun Mascot(width: Dp, height: Dp, modifier: Modifier = Modifier) {
    CroppedImage(
        image = R.drawable.home_mascot,
        width = width,
        height = height,
        crop = FigmaCrop(scaleX = 1.5289f, scaleY = 1.2235f, offsetX = -0.291f, offsetY = -0.0979f),
        contentDescription = "Maskot ScholarHub",
        modifier = modifier,
    )
}

/**
 * Banner ScholarMatch varian speech bubble (Home). Kartu 162 dp, maskot 105.57 × 150.03,
 * bubble #ECECEC dengan ekor menunjuk ke maskot.
 */
@Composable
fun MascotBubbleBanner(
    message: String,
    actionLabel: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(162.dp)
            .cardSurface()
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Mascot(width = 105.57.dp, height = 150.03.dp)
            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CardShape)
                        .background(BubbleSurface)
                        .border(1.dp, BubbleBorder, CardShape)
                        .padding(15.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(bottom = 6.dp),
                        style = ScholarHubTextStyles.bubble,
                        color = TextBubble,
                    )
                    Text(
                        text = actionLabel,
                        modifier = Modifier
                            .clip(PillShape)
                            .background(Navy)
                            .clickable(onClick = onActionClick)
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        style = ScholarHubTextStyles.bubble.copy(lineHeight = 18.sp),
                        color = Color.White,
                    )
                }
                // Ekor bubble 8 × 16 berwarna border (#E6E6E6), 1 dp di dalam tepi + offset −8/32 dari Figma.
                Canvas(modifier = Modifier.offset(x = (-7).dp, y = 33.dp).size(width = 8.dp, height = 16.dp)) {
                    val path = Path().apply {
                        moveTo(size.width, 0f)
                        lineTo(0f, size.height / 2f)
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(path, BubbleBorder)
                }
            }
        }
    }
}

/**
 * Banner maskot varian info (Figma `Welcome / Mascot Insight Banner`): gradien putih → #ECF5FB → putih,
 * border #E0E9EF, radius 16, padding 17, maskot 56 × 80, label "⚓ PANDUAN BEASISWA", judul, dan deskripsi.
 * [action] menambah tombol di bawah deskripsi (varian CTA di Detail Beasiswa).
 */
@Composable
fun MascotInfoBanner(
    label: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    mascotWidth: Dp = 56.dp,
    mascotHeight: Dp = 80.dp,
    labelStyle: TextStyle = nunitoSans(11f, 16.5f, FontWeight.ExtraBold, tracking = 0.55f),
    titleStyle: TextStyle = nunitoSans(15f, 18.75f, FontWeight.ExtraBold),
    descriptionStyle: TextStyle = nunitoSans(12f, 16.5f, FontWeight.Normal),
    descriptionColor: Color = TextVariant,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .cardSurface(shape = CardShape, color = Color.Transparent)
            .background(Brush.horizontalGradient(listOf(SurfaceCard, SkySoft, SurfaceCard)))
            .border(1.dp, OutlineMist, CardShape)
            .padding(17.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Mascot(width = mascotWidth, height = mascotHeight)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(1.3.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(7.5.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_list_anchor),
                    contentDescription = null,
                    modifier = Modifier.size(width = 10.5.dp, height = 11.667.dp),
                )
                Text(text = label.uppercase(), style = labelStyle, color = Ocean)
            }
            Text(
                text = title,
                modifier = Modifier.padding(top = 0.7.dp),
                style = titleStyle,
                color = Navy,
            )
            // Figma varian katalog: Inter Regular 12 / 16.5, dipetakan ke Nunito Sans.
            Text(
                text = description,
                modifier = Modifier.padding(bottom = 0.75.dp),
                style = descriptionStyle,
                color = descriptionColor,
            )
        }
            action?.invoke()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 360)
@Composable
private fun MascotBubbleBannerPreview() {
    ScholarHubTheme {
        MascotBubbleBanner(
            message = "Biar nggak salah arah, yuk temukan beasiswa yang paling pas buat profilmu!",
            actionLabel = "Test Lagi",
            onActionClick = {},
            modifier = Modifier.padding(end = 2.dp),
        )
    }
}
