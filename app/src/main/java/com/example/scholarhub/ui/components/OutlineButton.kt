package com.example.scholarhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.SurfaceCard

/**
 * Tombol putih lebar penuh (Figma `ScholarMatch / Pilihan`): tinggi 44, radius 12, border #006398 23%, Medium 13.
 * Dipakai mis. "Lengkapi Profil", "Ubah jawaban", "Lihat detail beasiswa".
 */
@Composable
fun OutlineButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(ScholarHubRadius.md)
    Box(
        modifier.fillMaxWidth().height(44.dp).clip(shape).background(SurfaceCard)
            .border(1.dp, Ocean.copy(alpha = 0.23f), shape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = Navy)
    }
}
