package com.example.scholarhub.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.scholarhub.R

/** Nunito Sans variable font (res/font/nunito_sans.ttf). Nama lama dipertahankan karena dipakai layar auth. */
@OptIn(ExperimentalTextApi::class)
val NunitoFontFamily = FontFamily(
    listOf(FontWeight.Normal, FontWeight.Medium, FontWeight.SemiBold, FontWeight.Bold, FontWeight.ExtraBold)
        .map { weight ->
            Font(R.font.nunito_sans, weight = weight, variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)))
        },
)

// Figma/CSS menghitung tinggi teks satu baris = line height penuh. Compose secara default memangkas
// ruang line height di atas baris pertama dan bawah baris terakhir, jadi trim dimatikan agar chip,
// tombol, dan kartu punya tinggi yang sama dengan Figma.
private val FigmaLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private fun nunito(weight: FontWeight, size: Float, lineHeight: Float, tracking: Float = 0f) = TextStyle(
    fontFamily = NunitoFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = if (tracking == 0f) TextUnit.Unspecified else tracking.sp,
    lineHeightStyle = FigmaLineHeightStyle,
)

/**
 * Bobot Nunito Sans untuk teks yang di Figma memakai Inter SemiBold. Inter SemiBold secara visual jauh lebih
 * tebal daripada Nunito Sans SemiBold, jadi dipetakan ke Bold agar ketebalannya mendekati Figma.
 */
val InterSemiBold: FontWeight = FontWeight.Bold

/**
 * Style Nunito Sans dengan ukuran bebas, untuk frame Figma yang di-scale (mis. 12.369 / 16.492) atau teks
 * Inter yang dipetakan ke Nunito Sans. Line height trim tetap dimatikan seperti style lain.
 */
fun nunitoSans(size: Float, lineHeight: Float, weight: FontWeight, tracking: Float = 0f): TextStyle =
    nunito(weight, size, lineHeight, tracking)

// Text style Figma (line height 1.5×). Slot M3 tanpa padanan di Figma memakai nilai yang teramati di frame.
val Typography = Typography(
    headlineSmall = nunito(FontWeight.Bold, 20f, 28f), // harga: "Rp 50.000", "Rp 150.000"
    titleLarge = nunito(FontWeight.Bold, 19f, 28.5f),
    titleMedium = nunito(FontWeight.Bold, 19f, 28.5f), // Figma: Title Medium
    titleSmall = nunito(FontWeight.Bold, 16f, 24f), // Figma: Title Small
    bodyLarge = nunito(FontWeight.Normal, 16f, 24f), // Figma: Body Large
    bodyMedium = nunito(FontWeight.SemiBold, 13f, 19.5f), // Figma: Body Medium
    bodySmall = nunito(FontWeight.Normal, 12f, 16f),
    labelLarge = nunito(FontWeight.Medium, 13f, 19.5f), // Figma: Label Large
    labelMedium = nunito(FontWeight.Bold, 12f, 16.5f),
    labelSmall = nunito(FontWeight.SemiBold, 11f, 16.5f), // Figma: Label Small
)

/** Style tambahan yang tidak punya slot M3. */
object ScholarHubTextStyles {
    /** Label bottom nav nonaktif (aktif: weight Bold, warna Navy). */
    val navLabel = nunito(FontWeight.ExtraBold, 10f, 14f, tracking = 0.6f)
    /** Label caps seperti "RINGKASAN SESI", "PROGRESS ROADMAP". */
    val overline = nunito(FontWeight.Bold, 12f, 16.5f, tracking = 0.55f)
    /** Teks speech bubble maskot dan tombol pill kecil. */
    val bubble = nunito(FontWeight.Bold, 12f, 16.5f)
    /** Judul kartu notifikasi / metode pembayaran (frame ×1.031 dinormalkan). */
    val cardTitle = nunito(FontWeight.Bold, 18f, 24f)
    /** Nilai statistik di StatTile Profil ("3.84"). */
    val statValue = nunito(FontWeight.ExtraBold, 20f, 28f)
}
