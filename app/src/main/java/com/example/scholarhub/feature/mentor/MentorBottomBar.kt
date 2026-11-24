package com.example.scholarhub.feature.mentor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.ui.components.HomeNavItem
import com.example.scholarhub.ui.components.NavItem
import com.example.scholarhub.ui.theme.NavSurface
import com.example.scholarhub.ui.theme.ShadowSubtle

enum class MentorNavTab {
    Schedule,
    Home,
    Profile,
}

/** Bottom nav mentor (Figma `NavMentor`): gaya sama dengan nav mentee, tiga tab berjarak 58 dp di tengah. */
@Composable
fun MentorBottomBar(
    currentTab: MentorNavTab,
    onTabSelect: (MentorNavTab) -> Unit,
) {
    val navBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, ambientColor = ShadowSubtle, spotColor = ShadowSubtle)
            .background(NavSurface)
            .padding(top = 23.dp, bottom = maxOf(23.dp, navBarInset)),
        contentAlignment = Alignment.Center,
    ) {
        Row(Modifier.height(56.dp), horizontalArrangement = Arrangement.spacedBy(58.dp), verticalAlignment = Alignment.CenterVertically) {
            NavItem(Modifier.width(72.dp), "Jadwal Sesi", R.drawable.ic_nav_calendar, 18.dp, 20.dp, currentTab == MentorNavTab.Schedule) {
                onTabSelect(MentorNavTab.Schedule)
            }
            HomeNavItem(Modifier.width(64.dp), currentTab == MentorNavTab.Home) { onTabSelect(MentorNavTab.Home) }
            NavItem(Modifier.width(64.dp), "Profil", R.drawable.ic_nav_profile, 16.dp, 16.dp, currentTab == MentorNavTab.Profile) {
                onTabSelect(MentorNavTab.Profile)
            }
        }
    }
}
