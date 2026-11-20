package com.example.scholarhub.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.NavyShadow
import com.example.scholarhub.ui.theme.ScholarHubDimens
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.ScholarHubTheme

/**
 * Tombol utama navy radius 12. Di Figma tombol ini punya shadow solid tanpa blur
 * di bawahnya (`0 2.5 0 #000C26`); set [solidShadow] = null untuk mematikannya.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIcon: Int? = null,
    containerColor: Color = Navy,
    contentColor: Color = Color.White,
    solidShadow: Color? = NavyShadow,
    shadowOffset: Dp = ScholarHubDimens.primaryButtonShadowOffset,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    /** Ikon kustom (ukuran tidak 16 dp); dipakai sebagai pengganti [leadingIcon]. */
    leadingContent: (@Composable () -> Unit)? = null,
    iconGap: Dp = 8.dp,
    /** Ikon setelah teks, misalnya sparkle di tombol ScholarMatch. */
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .then(
                if (solidShadow != null) {
                    Modifier.drawBehind {
                        drawRoundRect(
                            color = solidShadow,
                            topLeft = Offset(0f, shadowOffset.toPx()),
                            size = size,
                            cornerRadius = CornerRadius(ScholarHubRadius.md.toPx()),
                        )
                    }
                } else {
                    Modifier
                },
            )
            .clip(ButtonShape)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(iconGap, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingContent != null) {
            leadingContent()
        } else if (leadingIcon != null) {
            Image(
                painter = painterResource(leadingIcon),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }
        Text(text = text, style = textStyle, color = contentColor)
        trailingContent?.invoke()
    }
}

@Preview
@Composable
private fun PrimaryButtonPreview() {
    ScholarHubTheme {
        PrimaryButton(
            text = "Lihat Detail Program",
            onClick = {},
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
    }
}

