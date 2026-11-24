package com.example.scholarhub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.SurfaceCard

val CardShape = RoundedCornerShape(ScholarHubRadius.lg)
val ButtonShape = RoundedCornerShape(ScholarHubRadius.md)

/** Kartu putih standar Figma: radius 16, shadow `0 1 1-2` hitam 5%. */
fun Modifier.cardSurface(
    shape: Shape = CardShape,
    color: Color = SurfaceCard,
): Modifier = this
    .shadow(elevation = 1.dp, shape = shape, ambientColor = ShadowSubtle, spotColor = ShadowSubtle)
    .clip(shape)
    .background(color)
