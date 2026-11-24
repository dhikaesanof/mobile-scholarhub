package com.example.scholarhub.ui.components

import android.graphics.ImageDecoder
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.Steel
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.nunitoSans
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val Shape12 = RoundedCornerShape(12.dp)
private val FieldText = nunitoSans(13f, 19.5f, FontWeight.Medium)
private val Helper = nunitoSans(11f, 16.5f, FontWeight.Normal)

/** Kartu bagian form (Figma `Rincian prestasi`, `Identitas diri`): putih radius 12, padding 16, judul Bold 19. */
@Composable
fun FormSectionCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier.fillMaxWidth().clip(Shape12).background(SurfaceCard).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = Navy)
        content()
    }
}

/** Kerangka kolom (Figma `Profil / Kolom formulir` `213:3157`): label SemiBold 13, isian #ECF5FB berborder #D9EEFB, keterangan 11 #506076. */
@Composable
private fun FieldFrame(label: String, helper: String?, error: String?, field: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Navy)
        field()
        (error ?: helper)?.let { Text(it, style = Helper, color = if (error != null) ErrorRed else Steel) }
    }
}

private fun Modifier.fieldBox(error: Boolean) =
    fillMaxWidth().clip(Shape12).background(SkySoft).border(1.dp, if (error) ErrorRed else Sky, Shape12).padding(12.dp)

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    helper: String? = null,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false,
    minHeight: Dp = 0.dp,
) {
    FieldFrame(label, helper, error) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fieldBox(error != null).heightIn(min = minHeight),
            textStyle = FieldText.copy(color = Navy),
            singleLine = minHeight == 0.dp,
            cursorBrush = SolidColor(Navy),
            keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboardType),
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) Text(placeholder, style = FieldText, color = Navy)
                    inner()
                }
            },
        )
    }
}

/** Kolom pilihan dengan daftar tarik-turun; [multi] membiarkan daftar terbuka agar beberapa pilihan bisa dicentang. */
@Composable
fun FormPickerField(
    label: String,
    value: String?,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    helper: String? = null,
    showChevron: Boolean = false,
    selected: Set<String> = emptySet(),
    multi: Boolean = false,
) {
    var open by remember { mutableStateOf(false) }
    FieldFrame(label, helper, null) {
        Box {
            Row(
                Modifier.fieldBox(false).clickable(role = Role.DropdownList) { open = true },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(value ?: placeholder, Modifier.weight(1f), style = FieldText, color = Navy)
                if (showChevron) Image(painterResource(R.drawable.ic_tracker_chevron), null, Modifier.size(20.dp))
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }, containerColor = SurfaceCard) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(if (option in selected) "✓  $option" else option, style = FieldText, color = Navy) },
                        onClick = {
                            if (!multi) open = false
                            onSelect(option)
                        },
                    )
                }
            }
        }
    }
}

/** Kartu foto (Figma Edit Profil): avatar 80 dp di kiri, tombol navy "Ganti Foto" di kanan. */
@Composable
fun PhotoCard(photo: Uri?, @DrawableRes fallback: Int?, onChangePhoto: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(Shape12).background(SurfaceCard).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileAvatar(photo, fallback, 80.dp)
        PrimaryButton(
            text = "Ganti Foto",
            onClick = onChangePhoto,
            solidShadow = null,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            textStyle = MaterialTheme.typography.labelSmall,
        )
    }
}

/** Avatar bulat: foto pilihan pengguna, gambar bawaan, atau siluet (Figma profil kosong). */
@Composable
fun ProfileAvatar(photo: Uri?, @DrawableRes fallback: Int?, size: Dp) {
    val bitmap = rememberUriBitmap(photo)
    Box(
        Modifier.size(size).shadow(1.dp, CircleShape).clip(CircleShape).background(SkySoft),
        contentAlignment = Alignment.Center,
    ) {
        when {
            bitmap != null -> Image(bitmap, "Foto profil", Modifier.size(size), contentScale = ContentScale.Crop)
            fallback != null -> Image(painterResource(fallback), "Foto profil", Modifier.size(size), contentScale = ContentScale.Crop)
            else -> Image(painterResource(R.drawable.ic_profile_placeholder), "Belum ada foto", Modifier.size(size * 0.62f))
        }
    }
}

/** Memuat foto dari galeri (Photo Picker) di luar main thread; null selama memuat atau jika gagal. */
@Composable
fun rememberUriBitmap(uri: Uri?): ImageBitmap? {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(null, uri) {
        value = uri?.let {
            withContext(Dispatchers.IO) {
                runCatching {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it)) { decoder, info, _ ->
                        // Foto profil cukup 320 px agar tidak memakan memori.
                        val scale = 320f / maxOf(info.size.width, info.size.height)
                        if (scale < 1f) decoder.setTargetSize((info.size.width * scale).toInt(), (info.size.height * scale).toInt())
                    }.asImageBitmap()
                }.getOrNull()
            }
        }
    }
    return bitmap
}

/** Baris bantuan (Figma `Button - Keamanan Akun`): kartu putih, ikon dalam lingkaran #ECF5FB 30 dp, chevron. */
@Composable
fun HelpRow(@DrawableRes icon: Int, iconWidth: Dp, iconHeight: Dp, label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().shadow(1.dp, Shape12).clip(Shape12).background(SurfaceCard).clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(30.dp).clip(CircleShape).background(SkySoft), contentAlignment = Alignment.Center) {
            Image(painterResource(icon), null, Modifier.size(iconWidth, iconHeight))
        }
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = Navy)
        Image(painterResource(R.drawable.ic_profile_row_chevron), null, Modifier.size(6.167.dp, 10.dp))
    }
}

/** Kartu kosong satu baris (Figma "Belum ada prestasi yang ditambahkan"). */
@Composable
fun EmptyNoticeCard(text: String) {
    Box(Modifier.fillMaxWidth().clip(Shape12).background(SurfaceCard).padding(16.dp), contentAlignment = Alignment.Center) {
        Text(text, style = Helper, color = Navy)
    }
}

