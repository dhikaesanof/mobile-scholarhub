package com.example.scholarhub.feature.scholarmatch.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextMuted
import com.example.scholarhub.ui.theme.nunitoSans

private val Shape = RoundedCornerShape(ScholarHubRadius.md)
private val FieldBorder = Ocean.copy(alpha = 0.23f)

/** Body Medium (SemiBold 13): label pertanyaan dan teks pengantar. */
internal val QuestionText = nunitoSans(13f, 19.5f, FontWeight.SemiBold)

/** Kartu putih radius 16, padding 16, jarak 13 (Figma "Informasi akademik"). */
@Composable
fun FormCard(modifier: Modifier = Modifier, gap: Int = 13, padding: Int = 16, content: @Composable () -> Unit) {
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceCard).padding(padding.dp),
        verticalArrangement = Arrangement.spacedBy(gap.dp),
    ) { content() }
}

/** Pertanyaan + isian dengan jarak 6 (Figma "Di mana kamu kuliah?"). */
@Composable
fun Question(label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = QuestionText, color = Navy)
        content()
    }
}

/** "ScholarMatch / Pilihan" dan "Pilihan aktif": tinggi 44, radius 12, Medium 13. */
@Composable
fun ChoiceButton(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .height(44.dp)
            .clip(Shape)
            .background(if (selected) Navy else SurfaceCard)
            .then(if (selected) Modifier else Modifier.border(1.dp, FieldBorder, Shape))
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = if (selected) SurfaceCard else Navy)
    }
}

/** Pilihan Ya/Tidak dua kolom dengan jarak 8. */
@Composable
fun YesNo(value: Boolean?, onChange: (Boolean) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ChoiceButton("Ya", value == true, { onChange(true) }, Modifier.weight(1f))
        ChoiceButton("Tidak", value == false, { onChange(false) }, Modifier.weight(1f))
    }
}

/** "ScholarMatch / Isian": tinggi 44, border #006398 23%, teks Medium 13; placeholder abu. */
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    error: String? = null,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            textStyle = MaterialTheme.typography.labelLarge.copy(color = Navy),
            cursorBrush = SolidColor(Navy),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth().height(44.dp).clip(Shape).background(SurfaceCard)
                .border(1.dp, if (error != null) ErrorRed else FieldBorder, Shape)
                .alpha(if (enabled) 1f else 0.5f),
            decorationBox = { inner ->
                Box(Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) Text(placeholder, style = MaterialTheme.typography.labelLarge, color = TextMuted)
                    inner()
                }
            },
        )
        error?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = ErrorRed) }
    }
}

/** Isian pilihan dengan chevron (Figma "Universitas Gadjah Mada"); membuka daftar [options]. */
@Composable
fun FormDropdown(
    value: String?,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    showChevron: Boolean = true,
) {
    var open by remember { mutableStateOf(false) }
    Box(modifier) {
        Row(
            Modifier.fillMaxWidth().height(44.dp).clip(Shape).background(SurfaceCard).border(1.dp, FieldBorder, Shape)
                .clickable(role = Role.DropdownList) { open = true }.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                value ?: placeholder,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = if (value == null) TextMuted else Navy,
                maxLines = 1,
            )
            if (showChevron) Image(painterResource(R.drawable.ic_match_chevron_down), null, Modifier.size(10.dp, 20.dp))
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.labelLarge, color = Navy) },
                    onClick = {
                        onSelect(option)
                        open = false
                    },
                )
            }
        }
    }
}

/** "ScholarMatch / Prioritas" (terpilih: latar #D9EEFB, kotak #006398): tinggi 42, kotak 17 radius 4. */
@Composable
fun CheckRow(label: String, checked: Boolean, onToggle: () -> Unit, enabled: Boolean = true) {
    val boxShape = RoundedCornerShape(4.dp)
    Row(
        Modifier.fillMaxWidth().height(42.dp).clip(Shape).background(if (checked) Sky else SurfaceCard)
            .toggleable(value = checked, enabled = enabled || checked, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(horizontal = 12.dp)
            .alpha(if (enabled || checked) 1f else 0.5f),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(17.dp).clip(boxShape).background(if (checked) Ocean else SurfaceCard)
                .border(1.dp, if (checked) Ocean else Ocean.copy(alpha = 0.52f), boxShape),
        )
        Text(label, style = QuestionText, color = Navy)
    }
}

/** "ScholarMatch / Tombol utama": navy tinggi 48; nonaktif opacity 40%. */
@Composable
fun MainButton(label: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().height(48.dp).alpha(if (enabled) 1f else 0.4f).clip(Shape).background(Navy)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, color = SurfaceCard)
    }
}
