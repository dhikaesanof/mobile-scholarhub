package com.example.scholarhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkySubtle
import com.example.scholarhub.ui.theme.SurfaceCard

/** Chip filter dengan angka (Figma `266:12134`, Riwayat Transaksi dan Dokumen): aktif navy + angka di latar #EDF4FA; tidak aktif putih + angka navy. */
@Composable
fun CountChip(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.shadow(1.dp, PillShape, ambientColor = ShadowSubtle, spotColor = ShadowSubtle).clip(PillShape)
            .background(if (selected) Navy else SurfaceCard).clickable(role = Role.Tab, onClick = onClick).padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = if (selected) SurfaceCard else Navy)
        Text(
            count.toString(),
            Modifier.clip(PillShape).background(if (selected) SkySubtle else Navy).padding(horizontal = 5.694.dp, vertical = 1.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Navy else Sky,
        )
    }
}
