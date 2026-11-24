package com.example.scholarhub.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.ui.theme.LinkBlue
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.Slate500

/**
 * Judul section (Figma "Heading 4": Title Medium, Navy) dengan aksi kanan opsional.
 * Sebagian besar judul di Figma punya inset kiri 6 dp; judul yang sebaris dengan link memakai 0.
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    titleStartPadding: Dp = 6.dp,
    titleColor: Color = Navy,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f).padding(start = titleStartPadding)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = titleColor)
            if (subtitle != null) {
                Text(text = subtitle, style = MaterialTheme.typography.labelLarge, color = Slate500)
            }
        }
        action?.invoke()
    }
}

/** Link teks di kanan section, misalnya "Semua Jadwal" (Label Large, #0369A1, padding 4). */
@Composable
fun SectionLink(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .widthIn(min = 94.dp)
            .clickable(onClick = onClick)
            .padding(4.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelLarge,
        color = LinkBlue,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F1F1, widthDp = 360)
@Composable
private fun SectionHeaderPreview() {
    ScholarHubTheme {
        SectionHeader(
            title = "Sesi Mentoring Terdekat",
            titleStartPadding = 0.dp,
            action = { SectionLink(text = "Semua Jadwal", onClick = {}) },
        )
    }
}
