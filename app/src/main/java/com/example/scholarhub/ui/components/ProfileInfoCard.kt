package com.example.scholarhub.ui.components

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.nunitoSans

private val Shape12 = RoundedCornerShape(12.dp)
private val RowLabel = nunitoSans(13f, 24f, FontWeight.Medium)
private val RowValue = nunitoSans(12f, 24f, FontWeight.Normal)

/** Satu kelompok baris di kartu profil; [title] null = tanpa judul (profil mentee). */
data class ProfileInfoSection(val title: String?, val rows: List<Pair<String, String>>)

/**
 * Kartu atas profil (Figma `Header Profil Mahasiswa` `287:17073`): avatar 80, nama Bold 19, subjudul #006398, bio,
 * grid label 140 dp + nilai, lalu tombol navy berikon pensil. [onAction] null = mode baca.
 */
@Composable
fun ProfileInfoCard(
    name: String,
    photo: Uri?,
    @DrawableRes fallbackPhoto: Int?,
    sections: List<ProfileInfoSection>,
    actionLabel: String,
    onAction: (() -> Unit)?,
    subtitle: String? = null,
    bio: String? = null,
) {
    Column(
        Modifier.fillMaxWidth().shadow(1.dp, Shape12).clip(Shape12).background(SurfaceCard).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ProfileAvatar(photo, fallbackPhoto, 80.dp)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(name, Modifier.padding(top = 2.dp), style = MaterialTheme.typography.titleMedium, color = Navy)
                subtitle?.let { Text(it, style = MaterialTheme.typography.labelLarge, color = Ocean) }
            }
            bio?.let { Text(it, style = MaterialTheme.typography.labelLarge, color = Navy) }
            sections.forEachIndexed { index, section ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    section.title?.let {
                        Text(it, Modifier.padding(top = if (index == 0) 2.dp else 12.dp), style = MaterialTheme.typography.titleSmall, color = Navy)
                    }
                    Column(Modifier.padding(top = if (section.title != null) 4.dp else 0.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        section.rows.forEach { (label, value) ->
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(label, Modifier.width(140.dp), style = RowLabel, color = Navy)
                                Text(value.ifBlank { "-" }, Modifier.weight(1f), style = RowValue, color = Navy)
                            }
                        }
                    }
                }
            }
        }
        if (onAction != null) {
            Row(
                Modifier.fillMaxWidth().clip(Shape12).background(Navy).clickable(role = Role.Button, onClick = onAction)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(painterResource(R.drawable.ic_profile_edit), null, Modifier.size(10.dp))
                Text(actionLabel, style = MaterialTheme.typography.labelLarge, color = SurfaceCard)
            }
        }
    }
}
