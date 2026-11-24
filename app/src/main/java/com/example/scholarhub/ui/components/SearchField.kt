package com.example.scholarhub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.OutlineCool
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans

/** Search bar putih radius 16, border #DBE4EA (Beasiswa Page dan Mentor Page). */
@Composable
fun SearchField(query: String, onQueryChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(16.dp)
    val focusManager = LocalFocusManager.current
    val textStyle = nunitoSans(14f, 17f, FontWeight.Normal)
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .cardSurface(shape = shape)
            .border(1.dp, OutlineCool, shape),
        singleLine = true,
        textStyle = textStyle.copy(color = Navy),
        cursorBrush = SolidColor(Navy),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                Image(
                    painter = painterResource(R.drawable.ic_list_search),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 14.dp).size(15.dp),
                )
                Box(Modifier.padding(start = 41.dp, end = 16.dp)) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle,
                            color = TextVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                        )
                    }
                    innerTextField()
                }
            }
        },
    )
}

