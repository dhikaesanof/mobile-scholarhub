package com.example.scholarhub.ui.theme

import androidx.compose.ui.graphics.Color

// Token warna dari Figma ScholarHub (frame mentee 846:*). Lihat docs/mentee-ui.md.
// Nama yang ditandai "Figma var" adalah variable resmi di file Figma; sisanya diambil
// dari nilai yang paling sering dipakai di node.

// Brand
val Navy = Color(0xFF001645) // Figma var: Primary
val NavyStrong = Color(0xFF0F2A66)
val NavyDeep = Color(0xFF0B1727) // Figma var: Primary/blue-900
val NavyInk = Color(0xFF051937) // judul "Cari Mentor Beasiswa"
val InkBlue = Color(0xFF001D31) // teks chip topik mentor
val NavyShadow = Color(0xFF000C26) // shadow solid di bawah tombol primary
val Ocean = Color(0xFF006398) // Figma var: Secondary
val OceanDark = Color(0xFF004B73)
val OceanDeep = Color(0xFF00476E)
val LinkBlue = Color(0xFF0369A1)
val Sky = Color(0xFFD9EEFB) // Figma var: Accents
val SkySoft = Color(0xFFECF5FB)
val SkyFaint = Color(0xFFF4FAFF)
val SkyBorder = Color(0xFFCCE5FF)
val SkySubtle = Color(0xFFEDF4FA) // Figma var: color/subtle, chip filter aktif

// Permukaan
val Base = Color(0xFFF1F1F1) // Figma var: Base
val SurfaceCard = Color(0xFFFFFFFF)
val NavSurface = Color(0xE6FBF9F8) // #FBF9F8 @ 90%
val BubbleSurface = Color(0xFFECECEC)
val BubbleBorder = Color(0xFFE6E6E6)

// Teks
val TextStrong = Color(0xFF141D21)
val TitleInk = Color(0xFF0A2540) // nama beasiswa di hero Detail Beasiswa
val TextVariant = Color(0xFF444650)
val TextMuted = Color(0xFF757681)
val TextBubble = Color(0xFF1B1C1C)
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate600 = Color(0xFF475569)
val Slate500 = Color(0xFF64748B)
val Steel = Color(0xFF506076) // Figma var: color/secondary, border opsi & handle bottom sheet

// Garis
val Outline = Color(0xFFE2E8F0)
val OutlineSoft = Color(0xFFF1F5F9)
val OutlineSky = Color(0xFFE6EFF5)
val OutlineMist = Color(0xFFE0E9EF)
val OutlineCool = Color(0xFFDBE4EA)
val DividerGray = Color(0xFFB5B5B5) // garis di kartu carousel beasiswa
val RadioOutline = Color(0xFF767676)

// Status
val ErrorRed = Color(0xFFBA1A1A)
val ErrorContainer = Color(0xFFFFE9E6)
val ErrorBadge = Color(0xFFFFB0B1) // badge "Ditutup" di Detail Beasiswa
val ErrorContainerSoft = Color(0x99FFDAD6) // #FFDAD6 @ 60%
val OnErrorContainer = Color(0xFF640005)
val Warning = Color(0xFFF59E0B)
val WarningStrong = Color(0xFFD97706)
val WarningContainer = Color(0xFFFEF3C7)
val WarningContainerSoft = Color(0xFFFFFBEB)
val OnWarning = Color(0xFFB45309)
val OnWarningStrong = Color(0xFF92400E)
val Success = Color(0xFF047857)
val SuccessContainer = Color(0xFFECFDF5)
val Disabled = Color(0xFFB8B8B8)
val DisabledSoft = Color(0xFFD9D9D9) // lingkaran & chip slot "Booked"
val DisabledScrim = Color(0x33000000) // hitam @ 20%, slot "Booked"

// Shadow (dipakai dengan Modifier.shadow / drawBehind)
val ShadowSubtle = Color(0x0D000000) // hitam 5%
val ShadowHeader = Color(0x0A000000) // hitam 4%
val ShadowMedium = Color(0x1A000000) // hitam 10%
val ShadowStrong = Color(0x40000000) // hitam 25%, kartu slot jam
val ShadowNavy = Color(0x14001645) // #001645 @ 8%, bar bawah sticky
