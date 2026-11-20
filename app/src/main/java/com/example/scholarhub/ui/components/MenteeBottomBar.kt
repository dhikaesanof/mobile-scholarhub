package com.example.scholarhub.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.theme.NavSurface
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.ScholarHubTextStyles
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowMedium
import com.example.scholarhub.ui.theme.ShadowSubtle
import com.example.scholarhub.ui.theme.TextMuted

enum class MenteeNavTab {
    Premium,
    Mentor,
    Home,
    Scholarship,
    Profile,
}

/**
 * Bottom nav mentee (Figma `NavMentee`, varian `Home=Default/Mentor/Beasiswa/Profil/Premium`).
 * Tinggi 102 dp: slot 56 dp dengan jarak 23 dp atas-bawah. Jarak bawah ikut inset navigation bar jika lebih besar.
 * Tab Home berupa lingkaran 40 dp yang naik 12 dp; navy saat aktif, abu saat tidak.
 */
@Composable
fun MenteeBottomBar(
    currentTab: MenteeNavTab,
    onTabSelect: (MenteeNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, ambientColor = ShadowSubtle, spotColor = ShadowSubtle)
            .background(NavSurface)
            .padding(top = 23.dp, bottom = maxOf(23.dp, navBarInset)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavItem(Modifier.weight(1f), "Premium", R.drawable.ic_nav_premium, 16.dp, 21.dp, currentTab == MenteeNavTab.Premium) {
                onTabSelect(MenteeNavTab.Premium)
            }
            NavItem(Modifier.weight(1f), "Mentor", R.drawable.ic_nav_mentor, 20.dp, 18.dp, currentTab == MenteeNavTab.Mentor) {
                onTabSelect(MenteeNavTab.Mentor)
            }
            HomeNavItem(Modifier.weight(1f), currentTab == MenteeNavTab.Home) { onTabSelect(MenteeNavTab.Home) }
            NavItem(Modifier.weight(1f), "Beasiswa", R.drawable.ic_nav_scholarship, 22.dp, 18.dp, currentTab == MenteeNavTab.Scholarship) {
                onTabSelect(MenteeNavTab.Scholarship)
            }
            NavItem(Modifier.weight(1f), "Profil", R.drawable.ic_nav_mentee_profile, 16.dp, 16.dp, currentTab == MenteeNavTab.Profile) {
                onTabSelect(MenteeNavTab.Profile)
            }
        }
    }
}

@Composable
private fun NavItem(
    modifier: Modifier,
    label: String,
    @DrawableRes icon: Int,
    iconWidth: Dp,
    iconHeight: Dp,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = if (selected) Navy else TextMuted
    Column(
        modifier = modifier.height(56.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(width = iconWidth, height = iconHeight),
            colorFilter = ColorFilter.tint(color),
        )
        Text(
            text = label,
            style = if (selected) ScholarHubTextStyles.navLabel.copy(fontWeight = FontWeight.Bold) else ScholarHubTextStyles.navLabel,
            color = color,
        )
    }
}

@Composable
private fun HomeNavItem(modifier: Modifier, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) Navy else TextMuted
    Column(
        modifier = modifier.height(56.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        // Slot ikon 40 × 28; lingkaran 40 × 40 keluar 12 dp ke atas dari slot ini. wrapContentSize(unbounded)
        // mencegah lingkaran ikut dipaksa setinggi 28 dp (tanpa ini bentuknya jadi oval).
        Box(modifier = Modifier.size(width = 40.dp, height = 28.dp)) {
            Box(
                modifier = Modifier
                    .wrapContentSize(align = Alignment.TopCenter, unbounded = true)
                    .size(40.dp)
                    .offset(y = (-12).dp)
                    .shadow(elevation = 4.dp, shape = CircleShape, ambientColor = ShadowMedium, spotColor = ShadowMedium)
                    .clip(CircleShape)
                    .background(color)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_nav_home),
                    contentDescription = null,
                    modifier = Modifier.size(width = 14.67.dp, height = 16.5.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
        }
        // Label Home di Figma selalu Bold, warnanya mengikuti state.
        Text(text = "Home", style = ScholarHubTextStyles.navLabel.copy(fontWeight = FontWeight.Bold), color = color)
    }
}

@Preview(widthDp = 402)
@Composable
private fun MenteeBottomBarPreview() {
    ScholarHubTheme {
        Column {
            MenteeBottomBar(currentTab = MenteeNavTab.Home, onTabSelect = {})
            MenteeBottomBar(currentTab = MenteeNavTab.Mentor, onTabSelect = {})
        }
    }
}
