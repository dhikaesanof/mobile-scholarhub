package com.example.scholarhub.feature.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.NunitoFontFamily

// Warna dari frame Figma "Home Page" (node 1:1849).
private val BackgroundColor = Color(0xFFF1F1F1)
private val Navy = Color(0xFF001645)
private val Blue = Color(0xFF006398)
private val LinkBlue = Color(0xFF0369A1)
private val DeepBlue = Color(0xFF00476E)
private val Accent = Color(0xFFD9EEFB)
private val LightBlue = Color(0xFFECF5FB)
private val BorderBlue = Color(0xFFCCE5FF)
private val BorderBlueSoft = Color(0x99CCE5FF)
private val MutedText = Color(0xFF444650)
private val NavMuted = Color(0xFF757681)
private val AlertRed = Color(0xFFBA1A1A)
private val AlertRedBg = Color(0xFFFFE9E6)
private val BubbleBg = Color(0xFFECECEC)
private val BubbleBorder = Color(0xFFE6E6E6)
private val NavBg = Color(0xE6FBF9F8)
private val SoftShadow = Color(0x0D000000)

private val CardShape = RoundedCornerShape(16.dp)
private val ButtonShape = RoundedCornerShape(12.dp)
private val PillShape = RoundedCornerShape(9999.dp)

private fun nunito(
    size: Float,
    weight: FontWeight,
    color: Color,
    lineHeight: Float = size * 1.5f,
    letterSpacing: Float = 0f,
) = TextStyle(
    fontFamily = NunitoFontFamily,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
    color = color,
)

private fun Modifier.card(shape: Shape = CardShape): Modifier = this
    .shadow(elevation = 1.dp, shape = shape, ambientColor = SoftShadow, spotColor = SoftShadow)
    .clip(shape)
    .background(Color.White)

enum class MenteeNavTab {
    Premium,
    Mentor,
    Home,
    Scholarship,
    Profile,
}

@Composable
fun HomeScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToSchedule: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
) {
    Scaffold(
        containerColor = BackgroundColor,
        topBar = { HomeHeader() },
        bottomBar = {
            MenteeBottomBar(
                currentTab = MenteeNavTab.Home,
                onTabSelect = onTabSelect,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GreetingSection(Modifier.padding(horizontal = 21.dp))
            UpcomingSessionSection(
                modifier = Modifier.padding(horizontal = 21.dp),
                onSeeAllSchedule = onNavigateToSchedule,
            )
            FavoriteScholarshipsSection(onItemClick = onNavigateToDetail)
            ScholarMatchBanner(Modifier.padding(horizontal = 21.dp))
            ScholarMatchResultsSection(
                modifier = Modifier.padding(horizontal = 21.dp),
                onDetailClick = onNavigateToDetail,
            )
        }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, ambientColor = Color(0x0A000000), spotColor = Color(0x0A000000))
            .background(BackgroundColor)
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.width(88.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HeaderIconButton(onClick = { }) {
                Image(
                    painter = painterResource(R.drawable.ic_home_article),
                    contentDescription = "Artikel",
                    modifier = Modifier.size(width = 18.dp, height = 20.dp),
                )
            }
        }

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Navy),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_scholarhub_logo),
                    contentDescription = null,
                    modifier = Modifier.size(width = 12.75.dp, height = 14.17.dp),
                )
            }
            Text(text = "ScholarHub", style = nunito(19f, FontWeight.Bold, Navy))
        }

        Row(
            modifier = Modifier.width(88.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HeaderIconButton(onClick = { }) {
                Image(
                    painter = painterResource(R.drawable.ic_home_bell),
                    contentDescription = "Notifikasi",
                    modifier = Modifier.size(width = 16.dp, height = 20.dp),
                )
                // Badge 8dp dengan cincin 2dp #FBF9F8, berjarak 8dp dari kanan dan atas.
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-6).dp, y = 6.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFBF9F8))
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(AlertRed),
                )
            }
            HeaderIconButton(onClick = { }) {
                // Aset tombol bookmark Figma berukuran penuh 40x40.
                Image(
                    painter = painterResource(R.drawable.detail_bookmark),
                    contentDescription = "Bookmark",
                    modifier = Modifier.size(40.dp),
                )
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    onClick: () -> Unit,
    content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Composable
private fun GreetingSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Selamat Datang, Arza!", style = nunito(19f, FontWeight.Bold, Navy))
        Text(
            text = "Yuk, lanjutkan persiapan beasiswanya🔥",
            style = nunito(13f, FontWeight.SemiBold, Navy),
        )
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    startPadding: Dp = 6.dp,
) {
    Text(
        text = text,
        modifier = modifier.padding(start = startPadding),
        style = nunito(19f, FontWeight.Bold, Navy),
    )
}

@Composable
private fun UpcomingSessionSection(
    modifier: Modifier = Modifier,
    onSeeAllSchedule: () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionTitle(text = "Sesi Mentoring Terdekat", startPadding = 0.dp)
            Text(
                text = "Semua Jadwal",
                modifier = Modifier
                    .width(94.dp)
                    .clickable(onClick = onSeeAllSchedule)
                    .padding(4.dp),
                textAlign = TextAlign.Center,
                style = nunito(13f, FontWeight.Medium, LinkBlue),
            )
        }
        MentoringSessionCard()
    }
}

@Composable
private fun MentoringSessionCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .card()
            .padding(16.63.dp),
        verticalArrangement = Arrangement.spacedBy(16.63.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MentorAvatar()
            Column(
                modifier = Modifier.padding(top = 3.12.dp),
                verticalArrangement = Arrangement.spacedBy(2.08.dp),
            ) {
                Row(
                    modifier = Modifier
                        .border(1.dp, Blue, PillShape)
                        .padding(horizontal = 8.32.dp, vertical = 2.08.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.16.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_home_session_clock),
                        contentDescription = null,
                        modifier = Modifier.size(width = 10.17.dp, height = 10.63.dp),
                    )
                    Text(
                        text = "Selasa, 20 Okt • 19:30 WIB",
                        style = nunito(11f, FontWeight.SemiBold, Blue),
                    )
                }
                Text(
                    text = "Review Esai LPDP",
                    modifier = Modifier.padding(top = 2.08.dp),
                    style = nunito(16f, FontWeight.Bold, Navy),
                )
                Text(
                    text = "Bersama Kak Samantha Dira",
                    style = nunito(13f, FontWeight.SemiBold, MutedText),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            SessionButton(
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp)
                    .background(Accent, ButtonShape)
                    .border(1.dp, Color(0xFFE2E8F0), ButtonShape),
                iconRes = R.drawable.ic_home_reschedule,
                iconWidth = 14.dp,
                iconHeight = 14.dp,
                label = "Reschedule",
                labelColor = Blue,
                onClick = { },
            )
            SessionButton(
                modifier = Modifier
                    .width(160.dp)
                    .height(33.dp)
                    .background(Navy, ButtonShape),
                iconRes = R.drawable.ic_home_camera,
                iconWidth = 15.25.dp,
                iconHeight = 12.75.dp,
                label = "Masuk Room",
                labelColor = LightBlue,
                onClick = { },
            )
        }
    }
}

@Composable
private fun MentorAvatar() {
    // Figma: wadah 44.75x46.75 memotong foto 50dp yang digeser ke atas.
    Box(
        modifier = Modifier
            .size(width = 44.75.dp, height = 46.75.dp)
            .clip(RoundedCornerShape(50))
            .background(LightBlue),
    ) {
        Image(
            painter = painterResource(R.drawable.mentor_avatar),
            contentDescription = "Avatar Mentor",
            modifier = Modifier
                .requiredSize(width = 50.dp, height = 91.3.dp)
                .offset(x = 2.63.dp),
            contentScale = ContentScale.FillBounds,
        )
    }
}

@Composable
private fun SessionButton(
    modifier: Modifier,
    @DrawableRes iconRes: Int,
    iconWidth: Dp,
    iconHeight: Dp,
    label: String,
    labelColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(ButtonShape)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(width = iconWidth, height = iconHeight),
        )
        Spacer(Modifier.width(8.dp))
        Text(text = label, style = nunito(11f, FontWeight.SemiBold, labelColor))
    }
}

@Composable
private fun FavoriteScholarshipsSection(onItemClick: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(
            text = "Beasiswa Favorit",
            modifier = Modifier
                .padding(horizontal = 21.dp)
                .height(28.dp),
        )
        // Kartu menerus sampai tepi layar seperti di Figma.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 21.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FavoriteScholarshipCard(
                title = "Beasiswa Unggulan Kemendikbudristek 2026",
                degree = "S1",
                location = "Dalam Negeri",
                daysLeft = "Sisa 14 Hari",
                onClick = { onItemClick("SH28") },
            )
            FavoriteScholarshipCard(
                title = "Beasiswa Unggulan Kemendikbudristek 2026",
                degree = "S1",
                location = "Dalam Negeri",
                daysLeft = "Sisa 14 Hari",
                onClick = { onItemClick("SH28") },
            )
        }
    }
}

@Composable
private fun FavoriteScholarshipCard(
    title: String,
    degree: String,
    location: String,
    daysLeft: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .size(width = 255.dp, height = 166.dp)
            .clip(CardShape)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(start = 21.dp, end = 16.dp, top = 19.dp, bottom = 27.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProviderLogo()
            Box(
                modifier = Modifier
                    .size(width = 30.dp, height = 21.dp)
                    .clip(RoundedCornerShape(7.76.dp))
                    .background(Accent),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = degree, style = nunito(11f, FontWeight.SemiBold, Color(0xFF011645)))
            }
        }

        Text(
            text = title,
            style = nunito(16f, FontWeight.Bold, Navy),
            minLines = 2,
            maxLines = 2,
        )

        HorizontalDivider(color = Color(0xFFB5B5B5), thickness = 0.5.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(74.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = location,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                style = nunito(11f, FontWeight.SemiBold, Navy),
            )
            Text(
                text = daysLeft,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                style = nunito(11f, FontWeight.SemiBold, Navy),
            )
        }
    }
}

@Composable
private fun ProviderLogo() {
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(ButtonShape)
            .background(LightBlue)
            .border(1.dp, BorderBlue, ButtonShape)
            .padding(9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.logo_tutwuri),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )
    }
}

@Composable
private fun ScholarMatchBanner(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(text = "ScholarMatch")

        Box(
            modifier = Modifier
                .padding(end = 2.dp)
                .fillMaxWidth()
                .height(162.dp)
                .card()
                .padding(horizontal = 20.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(modifier = Modifier.size(width = 105.57.dp, height = 150.03.dp)) {
                    Image(
                        painter = painterResource(R.drawable.auth_mascot),
                        contentDescription = "Maskot ScholarHub",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-3).dp)
                            .size(width = 95.dp, height = 129.dp),
                        contentScale = ContentScale.Fit,
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CardShape)
                            .background(BubbleBg)
                            .border(1.dp, BubbleBorder, CardShape)
                            .padding(15.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Biar nggak salah arah, yuk temukan beasiswa yang paling pas buat profilmu!",
                            modifier = Modifier.padding(bottom = 6.dp),
                            style = nunito(12f, FontWeight.Bold, Color(0xFF1B1C1C), lineHeight = 16.5f),
                        )
                        Box(
                            modifier = Modifier
                                .clip(PillShape)
                                .background(Navy)
                                .clickable { }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "Test Lagi",
                                style = nunito(12f, FontWeight.Bold, Color.White, lineHeight = 18f),
                            )
                        }
                    }
                    // Ekor balon teks yang menunjuk ke maskot.
                    Canvas(
                        modifier = Modifier
                            .offset(x = (-7).dp, y = 33.dp)
                            .size(width = 8.dp, height = 16.dp),
                    ) {
                        val path = Path().apply {
                            moveTo(size.width, 0f)
                            lineTo(0f, size.height / 2f)
                            lineTo(size.width, size.height)
                            close()
                        }
                        drawPath(path, BubbleBg)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScholarMatchResultsSection(
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(text = "Hasil ScholarMatch")
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ScholarshipMatchCard(
                provider = "KEMENDIKBUDRISTEK",
                title = "Beasiswa Unggulan 2026",
                degreeInfo = "Jenjang S1 / S2 / S3 • Dalam Negeri",
                relevance = "90% Relevan",
                deadline = "14 Hari Lagi",
                tags = listOf("D4, S1", "Semester 5-8", "IPK >= 3.20"),
                onDetailClick = { onDetailClick("SH28") },
            )
            ScholarshipMatchCard(
                provider = "KEMENDIKBUDRISTEK",
                title = "Beasiswa Unggulan 2026",
                degreeInfo = "Jenjang S1 / S2 / S3 • Dalam Negeri",
                relevance = "90% Relevan",
                deadline = "14 Hari Lagi",
                tags = listOf("D4, S1", "Semester 5-8", "IPK >= 3.20"),
                onDetailClick = { onDetailClick("SH28") },
            )
        }
    }
}

private val TagIcons = listOf(
    R.drawable.ic_home_school,
    R.drawable.ic_home_layers,
    R.drawable.ic_home_stars,
)

@Composable
private fun ScholarshipMatchCard(
    provider: String,
    title: String,
    degreeInfo: String,
    relevance: String,
    deadline: String,
    tags: List<String>,
    onDetailClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .card()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                ProviderLogo()
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(LightBlue)
                        .border(1.dp, BorderBlueSoft, PillShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(text = relevance, style = nunito(11f, FontWeight.Bold, DeepBlue))
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(25.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(text = provider, style = nunito(11f, FontWeight.SemiBold, Blue))
                }
                Text(text = title, style = nunito(16f, FontWeight.Bold, Navy))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = degreeInfo, style = nunito(11f, FontWeight.SemiBold, MutedText))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(59.dp))
                            .background(Color.White)
                            .padding(vertical = 4.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .width(95.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(AlertRedBg)
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_home_clock_red),
                                contentDescription = null,
                                modifier = Modifier.size(width = 9.75.dp, height = 11.38.dp),
                            )
                            Text(text = deadline, style = nunito(11f, FontWeight.SemiBold, AlertRed))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tags.forEachIndexed { index, tag ->
                Row(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(Accent)
                        .border(1.dp, BorderBlueSoft, PillShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Image(
                        painter = painterResource(TagIcons[index % TagIcons.size]),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = tag,
                        maxLines = 1,
                        softWrap = false,
                        style = nunito(11f, FontWeight.SemiBold, DeepBlue),
                    )
                }
            }
        }

        // Tombol dengan bayangan solid 2.5dp (#000C26) di bawahnya.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = Color(0xFF000C26),
                        topLeft = Offset(0f, 2.5.dp.toPx()),
                        size = size,
                        cornerRadius = CornerRadius(12.dp.toPx()),
                    )
                }
                .clip(ButtonShape)
                .background(Navy)
                .clickable(onClick = onDetailClick)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Lihat Detail Program", style = nunito(13f, FontWeight.Medium, Color.White))
        }
    }
}

@Composable
fun MenteeBottomBar(
    currentTab: MenteeNavTab,
    onTabSelect: (MenteeNavTab) -> Unit,
) {
    // Figma NavMentee: tinggi 102dp, item 56dp dengan jarak 23dp di atas dan bawah.
    // Jarak bawah memakai inset navigation bar Android jika lebih besar dari 23dp.
    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, ambientColor = SoftShadow, spotColor = SoftShadow)
            .background(NavBg)
            .padding(top = 23.dp, bottom = maxOf(23.dp, navBarInset)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MenteeNavItem(
                modifier = Modifier.weight(1f),
                label = "Premium",
                iconRes = R.drawable.detail_premium,
                iconWidth = 16.dp,
                iconHeight = 21.dp,
                isSelected = currentTab == MenteeNavTab.Premium,
                onClick = { onTabSelect(MenteeNavTab.Premium) },
            )
            MenteeNavItem(
                modifier = Modifier.weight(1f),
                label = "Mentor",
                iconRes = R.drawable.detail_mentor,
                iconWidth = 20.dp,
                iconHeight = 18.dp,
                isSelected = currentTab == MenteeNavTab.Mentor,
                onClick = { onTabSelect(MenteeNavTab.Mentor) },
            )
            MenteeHomeNavItem(
                modifier = Modifier.weight(1f),
                isSelected = currentTab == MenteeNavTab.Home,
                onClick = { onTabSelect(MenteeNavTab.Home) },
            )
            MenteeNavItem(
                modifier = Modifier.weight(1f),
                label = "Beasiswa",
                iconRes = R.drawable.detail_scholarship,
                iconWidth = 22.dp,
                iconHeight = 18.dp,
                isSelected = currentTab == MenteeNavTab.Scholarship,
                onClick = { onTabSelect(MenteeNavTab.Scholarship) },
            )
            MenteeNavItem(
                modifier = Modifier.weight(1f),
                label = "Profil",
                iconRes = R.drawable.detail_profile,
                iconWidth = 16.dp,
                iconHeight = 16.dp,
                isSelected = currentTab == MenteeNavTab.Profile,
                onClick = { onTabSelect(MenteeNavTab.Profile) },
            )
        }
    }
}

@Composable
private fun MenteeNavItem(
    modifier: Modifier,
    label: String,
    @DrawableRes iconRes: Int,
    iconWidth: Dp,
    iconHeight: Dp,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val color = if (isSelected) Navy else NavMuted
    Column(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(width = iconWidth, height = iconHeight),
            colorFilter = ColorFilter.tint(color),
        )
        Text(
            text = label,
            style = nunito(
                10f,
                if (isSelected) FontWeight.Bold else FontWeight.ExtraBold,
                color,
                lineHeight = 14f,
                letterSpacing = 0.6f,
            ),
        )
    }
}

@Composable
private fun MenteeHomeNavItem(
    modifier: Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        // Lingkaran 40dp menonjol 12dp di atas slot ikon setinggi 28dp.
        Box(modifier = Modifier.size(width = 40.dp, height = 28.dp)) {
            Box(
                modifier = Modifier
                    .offset(y = (-12).dp)
                    .size(40.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(if (isSelected) Navy else NavMuted)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.detail_home),
                    contentDescription = null,
                    modifier = Modifier.size(width = 14.67.dp, height = 16.5.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
        }
        Text(
            text = "Home",
            style = nunito(
                10f,
                FontWeight.Bold,
                if (isSelected) Navy else NavMuted,
                lineHeight = 14f,
                letterSpacing = 0.6f,
            ),
        )
    }
}

@Preview(widthDp = 402, heightDp = 1635)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
