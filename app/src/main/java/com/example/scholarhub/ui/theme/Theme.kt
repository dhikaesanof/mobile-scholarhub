package com.example.scholarhub.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Desain Figma hanya punya mode terang, jadi tema tidak memakai dark/dynamic color
// agar warna brand tidak tergantikan wallpaper di Android 12+.
private val LightColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    primaryContainer = NavyStrong,
    onPrimaryContainer = Color.White,
    secondary = Ocean,
    onSecondary = Color.White,
    secondaryContainer = Sky,
    onSecondaryContainer = OceanDeep,
    tertiary = LinkBlue,
    onTertiary = Color.White,
    background = Base,
    onBackground = Navy,
    surface = SurfaceCard,
    onSurface = TextStrong,
    surfaceVariant = SkySoft,
    onSurfaceVariant = TextVariant,
    surfaceContainer = SurfaceCard,
    outline = Outline,
    outlineVariant = OutlineSoft,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
)

/** Radius dari Figma. */
object ScholarHubRadius {
    val xs = 4.dp // badge kecil
    val sm = 8.dp // chip kecil, item list
    val md = 12.dp // tombol, input, logo provider, foto mentor
    val lg = 16.dp // kartu
    val xl = 20.dp // chip deadline
    val avatar = 28.dp
}

/** Spacing grid 4 dp. */
object ScholarHubSpacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 6.dp
    val md = 8.dp
    val lg = 12.dp
    val xl = 16.dp
    val xxl = 20.dp
    val xxxl = 24.dp

    /** Margin horizontal konten layar (Figma 16–21; dipakai 20). */
    val screenHorizontal = 20.dp
    /** Jarak antarsection di layar. */
    val section = 16.dp
    /** Jarak judul section ke isinya. */
    val sectionTitle = 8.dp
    /** Padding dalam kartu standar. */
    val card = 16.dp
}

/** Ukuran tetap yang dipakai berulang. */
object ScholarHubDimens {
    val headerBarHeight = 64.dp
    val bottomBarHeight = 102.dp
    val iconButton = 40.dp
    val providerLogo = 48.dp
    val avatar = 50.dp
    val primaryButtonShadowOffset = 2.5.dp
}

val ScholarHubShapes = Shapes(
    extraSmall = RoundedCornerShape(ScholarHubRadius.xs),
    small = RoundedCornerShape(ScholarHubRadius.sm),
    medium = RoundedCornerShape(ScholarHubRadius.md),
    large = RoundedCornerShape(ScholarHubRadius.lg),
    extraLarge = RoundedCornerShape(ScholarHubRadius.xl),
)

val PillShape = CircleShape

@Composable
fun ScholarHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = ScholarHubShapes,
        content = content,
    )
}
