package com.example.scholarhub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.ScholarHubDimens
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowHeader

private val NotificationRing = androidx.compose.ui.graphics.Color(0xFFFBF9F8)

/**
 * Header utama mentee (Figma `Header` varian logo): tombol artikel di kiri,
 * logo + "ScholarHub" di tengah, notifikasi dan bookmark di kanan. Tinggi bar 64 dp.
 */
@Composable
fun ScholarHubTopBar(
    modifier: Modifier = Modifier,
    hasUnreadNotification: Boolean = true,
    onArticleClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Sisi kiri dan kanan sama-sama 88 dp agar logo tepat di tengah.
        Row(modifier = Modifier.width(88.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeaderIconButton(onClick = onArticleClick) {
                Image(
                    painter = painterResource(R.drawable.ic_home_article),
                    contentDescription = "Artikel",
                    modifier = Modifier.size(width = 18.dp, height = 20.dp),
                )
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape).background(Navy),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_scholarhub_logo),
                    contentDescription = null,
                    modifier = Modifier.size(width = 12.75.dp, height = 14.17.dp),
                )
            }
            Text(text = "ScholarHub", style = MaterialTheme.typography.titleMedium, color = Navy)
        }

        Row(modifier = Modifier.width(88.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeaderIconButton(onClick = onNotificationClick) {
                Image(
                    painter = painterResource(R.drawable.ic_home_bell),
                    contentDescription = "Notifikasi",
                    modifier = Modifier.size(width = 16.dp, height = 20.dp),
                )
                if (hasUnreadNotification) {
                    // Dot 8 dp berjarak 8 dp dari atas/kanan, dengan cincin 2 dp #FBF9F8.
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-6).dp, y = 6.dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(NotificationRing)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(ErrorRed),
                    )
                }
            }
            HeaderIconButton(onClick = onBookmarkClick) {
                // Aset Figma sudah berukuran penuh 40 × 40 termasuk ruang kosongnya.
                Image(
                    painter = painterResource(R.drawable.ic_home_bookmark),
                    contentDescription = "Bookmark",
                    modifier = Modifier.size(ScholarHubDimens.iconButton),
                )
            }
        }
    }
}

/**
 * Header varian detail (Figma `Header` di layar detail): tombol kembali 40 dp, judul di tengah,
 * dan slot kosong 40 dp di kanan agar judul tetap di tengah. Gap 42, padding horizontal 24.
 */
@Composable
fun ScholarHubDetailTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    /** Aksi kanan opsional (mis. bookmark di Detail Beasiswa); kosong tetap memakan 40 dp agar judul di tengah. */
    trailing: (@Composable () -> Unit)? = null,
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
        horizontalArrangement = Arrangement.spacedBy(42.dp),
    ) {
        HeaderIconButton(onClick = onBack) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = "Kembali",
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            color = Navy,
        )
        Box(modifier = Modifier.size(ScholarHubDimens.iconButton), contentAlignment = Alignment.Center) {
            trailing?.invoke()
        }
    }
}

@Composable
private fun HeaderIconButton(onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.size(ScholarHubDimens.iconButton).clip(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Preview(widthDp = 402)
@Composable
private fun ScholarHubTopBarPreview() {
    ScholarHubTheme {
        Column {
            ScholarHubTopBar()
            ScholarHubDetailTopBar(title = "Detail Mentor", onBack = {})
        }
    }
}
