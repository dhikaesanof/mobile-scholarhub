package com.example.scholarhub.feature.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.NunitoFontFamily
import com.example.scholarhub.ui.theme.ScholarHubDimens
import com.example.scholarhub.ui.theme.ShadowHeader
import androidx.compose.foundation.layout.Box

private val BadgeBg = Color(0xFFCCE5FF)
private val BadgeText = Color(0xFF001D31)

/**
 * Header Login/Register (Figma `130:5808`): logo di tengah; lonceng dari Figma sengaja tidak dipakai.
 * `badgeText` dan `onBack` dipakai profil mentor.
 */
@Composable
fun AuthHeader(
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    onBack: (() -> Unit)? = null,
    /** Tombol lonceng di kanan (Header mentor, Figma `234:11763`); null = slot kosong. */
    onNotificationClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, ambientColor = ShadowHeader, spotColor = ShadowHeader)
            .background(Base)
            .statusBarsPadding()
            .height(ScholarHubDimens.headerBarHeight)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Image(
                        painter = painterResource(R.drawable.detail_back),
                        contentDescription = "Kembali",
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            Box(
                modifier = Modifier.size(28.dp).background(Navy, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_scholarhub_logo),
                    contentDescription = null,
                    modifier = Modifier.size(width = 12.75.dp, height = 14.17.dp),
                )
            }
            Text(
                text = "ScholarHub",
                style = TextStyle(
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    lineHeight = 28.5.sp,
                    color = Navy,
                ),
            )
            if (badgeText != null) {
                Text(
                    text = badgeText.uppercase(),
                    modifier = Modifier
                        .background(BadgeBg, CircleShape)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    style = TextStyle(
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = BadgeText,
                    ),
                )
            }
        }

        // Penyeimbang slot kiri agar logo tetap di tengah; berisi lonceng bila ada.
        Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            if (onNotificationClick != null) {
                IconButton(onClick = onNotificationClick) {
                    Image(painterResource(R.drawable.ic_home_bell), "Notifikasi", Modifier.size(width = 16.dp, height = 20.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun AuthHeaderPreview() {
    AuthHeader()
}
