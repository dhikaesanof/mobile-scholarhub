package com.example.scholarhub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.ErrorContainer
import com.example.scholarhub.ui.theme.ErrorRed

/**
 * Pemberitahuan merah (Figma `Warning Mentoring`, "Lengkapi informasi profilmu", "Periksa Kembali Jadwal Sesi"):
 * latar #FFE9E6 radius 16, ikon `error_outline` 24, judul Bold 16 dan isi SemiBold 13 #BA1A1A.
 */
@Composable
fun WarningNotice(title: String, body: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(ErrorContainer).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.ic_match_error), null, Modifier.size(24.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, color = ErrorRed)
        }
        Text(body, style = MaterialTheme.typography.bodyMedium, color = ErrorRed)
    }
}
