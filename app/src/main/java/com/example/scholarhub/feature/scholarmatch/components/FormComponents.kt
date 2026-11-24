package com.example.scholarhub.feature.scholarmatch.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.NavyStrong
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.Outline
import com.example.scholarhub.ui.theme.OutlineSoft
import com.example.scholarhub.ui.theme.RadioOutline
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.SkyFaint
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans

// Frame ScholarMatch di-scale ×0.897 secara merata (termasuk font); nilai Figma dipakai apa adanya.
// Teks Inter dipetakan ke Nunito Sans dengan ukuran yang sama (Inter SemiBold → Bold, lihat Type.kt).
private val Radius = RoundedCornerShape(10.759.dp)
internal val Small = nunitoSans(10.759f, 14.346f, FontWeight.Medium)
internal val SmallBold = nunitoSans(10.759f, 14.346f, FontWeight.Bold)
internal val Caption = nunitoSans(9.863f, 14.794f, FontWeight.Normal)
private val ThinBorder = 0.897.dp
private val ThickBorder = 1.793.dp

/** Kartu bagian form (Figma `Section - BAGIAN A..D`): huruf dalam lingkaran navy, judul, deskripsi, garis bawah. */
@Composable
fun FormSectionCard(letter: String, title: String, description: String, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(14.346.dp)
    Column(
        Modifier.fillMaxWidth()
            .shadow(1.dp, shape, ambientColor = ShadowSubtle, spotColor = ShadowSubtle)
            .clip(shape)
            .background(SurfaceCard)
            .border(ThinBorder, Outline, shape)
            .padding(18.829.dp),
        verticalArrangement = Arrangement.spacedBy(14.346.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(7.173.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(21.519.dp).clip(CircleShape).background(NavyStrong), contentAlignment = Alignment.Center) {
                    Text(letter, style = nunitoSans(10.759f, 14.346f, FontWeight.Bold), color = SurfaceCard)
                }
                Text(title, style = nunitoSans(14.346f, 21.519f, FontWeight.Bold), color = Navy)
            }
            Text(description, style = nunitoSans(10.759f, 14.346f, FontWeight.Normal), color = TextVariant)
            Box(Modifier.padding(top = 3.656.dp).fillMaxWidth().height(ThinBorder).background(Outline))
        }
        content()
    }
}

/** Satu pertanyaan bernomor. Pertanyaan selain yang pertama punya garis atas #F1F5F9 dan jarak 8. */
@Composable
fun Question(number: Int, label: String, first: Boolean = false, trailing: (@Composable () -> Unit)? = null, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.173.dp)) {
        if (!first) Box(Modifier.fillMaxWidth().height(ThinBorder).background(OutlineSoft))
        Row(
            Modifier.fillMaxWidth().padding(top = if (first) 0.dp else 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.38.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(17.932.dp).clip(CircleShape).background(SkySoft), contentAlignment = Alignment.Center) {
                    Text(number.toString(), style = nunitoSans(9.863f, 14.346f, FontWeight.Bold), color = Ocean)
                }
                Text(label, style = SmallBold, color = Navy)
            }
            trailing?.invoke()
        }
        content()
    }
}

/** Tombol pilihan tunggal (Figma `Button`). Terpilih: latar #ECF5FB, border 1.79 #006398, teks Bold + "✓". */
@Composable
fun OptionButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, minHeight: Dp = 35.86.dp) {
    Box(
        modifier
            .heightIn(min = minHeight)
            .clip(Radius)
            .background(if (selected) SkySoft else SurfaceCard)
            .border(if (selected) ThickBorder else ThinBorder, if (selected) Ocean else Outline, Radius)
            .selectable(selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (selected) "$label ✓" else label,
            style = if (selected) SmallBold else Small,
            color = if (selected) Ocean else TextStrong,
            textAlign = TextAlign.Center,
        )
    }
}

/** Baris radio (Figma pertanyaan 7): radio 14.35 di kanan. */
@Composable
fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .clip(Radius)
            .background(if (selected) SkySoft else SkyFaint)
            .border(if (selected) ThickBorder else ThinBorder, if (selected) Ocean else Outline, Radius)
            .selectable(selected, role = Role.RadioButton, onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, modifier = Modifier.weight(1f), style = if (selected) SmallBold else Small, color = if (selected) Ocean else TextStrong)
        Box(
            Modifier.size(14.346.dp).clip(CircleShape).background(SurfaceCard)
                .border(ThinBorder, if (selected) Navy else RadioOutline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) Box(Modifier.size(8.608.dp).clip(CircleShape).background(Navy))
        }
    }
}

/** Kartu checkbox prioritas (Figma pertanyaan 13). [enabled] false saat batas 5 prioritas sudah tercapai. */
@Composable
fun CheckboxCard(title: String, checked: Boolean, enabled: Boolean, onToggle: () -> Unit, description: String? = null) {
    Row(
        Modifier.fillMaxWidth()
            .alpha(if (enabled || checked) 1f else 0.5f)
            .clip(Radius)
            .background(if (checked) SkySoft else SkyFaint)
            .border(if (checked) ThickBorder else ThinBorder, if (checked) Ocean else Outline, Radius)
            .toggleable(checked, enabled = enabled || checked, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.759.dp),
    ) {
        Box(
            Modifier.padding(top = 1.793.dp).size(14.346.dp).clip(RoundedCornerShape(2.242.dp))
                .background(if (checked) Navy else SurfaceCard)
                .then(if (checked) Modifier else Modifier.border(ThinBorder, RadioOutline, RoundedCornerShape(2.242.dp))),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) Text("✓", style = nunitoSans(9f, 12f, FontWeight.Bold), color = SurfaceCard)
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = SmallBold, color = if (checked) Ocean else Navy)
            description?.let { Text(it, modifier = Modifier.padding(top = 1.793.dp), style = Caption, color = TextVariant) }
        }
    }
}

/** Input teks (Figma `Input`): latar #F4FAFF, border #E2E8F0, tinggi 39.45, ikon kiri dan/atau akhiran kanan. */
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    @DrawableRes leadingIcon: Int? = null,
    leadingWidth: Dp = 16.dp,
    leadingHeight: Dp = 16.dp,
    suffix: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    textStyle: TextStyle = nunitoSans(10.759f, 14.346f, FontWeight.Bold),
    error: String? = null,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            Modifier.fillMaxWidth().height(39.451.dp).clip(Radius).background(SkyFaint)
                .border(ThinBorder, if (error != null) ErrorRed else Outline, Radius),
            contentAlignment = Alignment.CenterStart,
        ) {
            leadingIcon?.let {
                Image(painterResource(it), null, Modifier.padding(start = 12.55.dp).size(leadingWidth, leadingHeight))
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = textStyle.copy(color = Navy),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                modifier = Modifier.fillMaxWidth().padding(start = if (leadingIcon != null) 36.762.dp else 13.45.dp, end = 80.dp),
                decorationBox = { inner ->
                    if (value.isEmpty()) Text(placeholder, style = textStyle.copy(fontWeight = FontWeight.Medium), color = TextVariant.copy(alpha = 0.7f))
                    inner()
                },
            )
            suffix?.let {
                Text(it, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.55.dp), style = SmallBold, color = TextVariant)
            }
        }
        error?.let { Text(it, style = Caption, color = ErrorRed) }
    }
}

