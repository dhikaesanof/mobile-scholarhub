package com.example.scholarhub.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

/**
 * Crop gambar ala Figma: gambar diskalakan relatif ke wadahnya ([scaleX], [scaleY]) lalu digeser
 * ([offsetX], [offsetY], pecahan dari ukuran wadah). Nilainya diambil langsung dari properti
 * `w-[..%] h-[..%] left-[..%] top-[..%]` pada `get_design_context`.
 */
@Immutable
data class FigmaCrop(
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
)

@Composable
fun CroppedImage(
    @DrawableRes image: Int,
    width: Dp,
    height: Dp,
    crop: FigmaCrop,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(width, height).clipToBounds()) {
        Image(
            painter = painterResource(image),
            contentDescription = contentDescription,
            modifier = Modifier
                .wrapContentSize(align = Alignment.TopStart, unbounded = true)
                .size(width = width * crop.scaleX, height = height * crop.scaleY)
                .offset(x = width * crop.offsetX, y = height * crop.offsetY),
            contentScale = ContentScale.FillBounds,
        )
    }
}
