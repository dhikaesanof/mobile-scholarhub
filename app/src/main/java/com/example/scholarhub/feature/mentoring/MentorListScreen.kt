package com.example.scholarhub.feature.mentoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scholarhub.feature.mentoring.components.MentorCard
import com.example.scholarhub.feature.mentoring.components.SampleMentors
import com.example.scholarhub.feature.scholarship.list.FilterButton
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.MenteeBottomBar
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.ui.components.ScholarHubTopBar
import com.example.scholarhub.ui.components.SearchField
import com.example.scholarhub.ui.components.SectionHeader
import com.example.scholarhub.ui.components.SectionLink
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import kotlinx.coroutines.launch

/**
 * Mentor Page mentee (Figma `209:5594` tanpa sesi, `215:7452` dengan sesi): sesi terdekat, lalu cari mentor.
 * Pencarian mencocokkan nama dan afiliasi mentor.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MentorListScreen(
    store: MentoringStore,
    onMentorClick: (String) -> Unit = {},
    onSeeAllSchedule: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
    onOpenBookmarks: () -> Unit = {},
    onOpenArticles: () -> Unit = {},
    onTabSelect: (MenteeNavTab) -> Unit = {},
) {
    val bookings by store.bookings.collectAsState()
    val openSlots by store.openSlots.collectAsState()
    val now = rememberNow(store)
    var query by rememberSaveable { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val nearest = bookings.filter { it.phaseAt(now)?.isUpcoming == true }.minByOrNull { it.start }
    var filters by remember { mutableStateOf(MentorFilters()) }
    var showFilter by rememberSaveable { mutableStateOf(false) }
    val q = query.trim()
    fun visible(f: MentorFilters) = SampleMentors.filter {
        (q.isEmpty() || it.name.contains(q, true) || it.affiliation.contains(q, true)) && mentorMatches(it.id, f, bookings, openSlots, now)
    }
    val mentors = visible(filters)

    Scaffold(
        containerColor = Base,
        topBar = {
            ScholarHubTopBar(onArticleClick = onOpenArticles, onNotificationClick = onOpenNotifications, onBookmarkClick = onOpenBookmarks)
        },
        bottomBar = { MenteeBottomBar(currentTab = MenteeNavTab.Mentor, onTabSelect = onTabSelect) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(
                    title = "Sesi Mentoring Saya",
                    titleStartPadding = 0.dp,
                    action = { SectionLink(text = "Semua Jadwal", onClick = onSeeAllSchedule) },
                )
                if (nearest == null) {
                    Text("Belum ada sesi terjadwal. Pilih mentor di bawah untuk memulai.", style = MaterialTheme.typography.bodyMedium, color = Navy)
                } else {
                    BookingSessionCard(nearest, now, onJoin = { scope.launch { snackbar.showSnackbar("Tautan room belum tersedia.") } })
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Cari Mentor Beasiswa", style = MaterialTheme.typography.titleMedium, color = Navy)
                        Text("${mentors.size} dari ${SampleMentors.size} Mentor", style = MaterialTheme.typography.labelSmall, color = Ocean)
                    }
                    FilterButton(activeCount = filters.count, onClick = { showFilter = true })
                }
                SearchField(query = query, onQueryChange = { query = it }, placeholder = "Cari Mentor yang cocok denganmu...")
                if (filters.count > 0) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filters.fields.forEach { v -> MentorActiveChip(v) { filters = filters.copy(fields = filters.fields - v) } }
                        filters.scholarships.forEach { v -> MentorActiveChip(v) { filters = filters.copy(scholarships = filters.scholarships - v) } }
                        filters.campuses.forEach { v -> MentorActiveChip(v) { filters = filters.copy(campuses = filters.campuses - v) } }
                        filters.availability?.let { a -> MentorActiveChip(a.label()) { filters = filters.copy(availability = null) } }
                    }
                }
                if (mentors.isEmpty()) {
                    Text("Tidak ada mentor yang cocok. Ubah kata kunci atau filter.", style = MaterialTheme.typography.bodyMedium, color = Navy)
                } else {
                    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        mentors.forEach { mentor -> MentorCard(mentor = mentor, onBookingClick = { onMentorClick(mentor.id) }) }
                    }
                    EndOfFeed("Semua mentor telah dimuat")
                }
            }
        }
    }
    if (showFilter) {
        MentorFilterSheet(
            initial = filters,
            today = now.toLocalDate(),
            countFor = { visible(it).size },
            onApply = {
                filters = it
                showFilter = false
            },
            onDismiss = { showFilter = false },
        )
    }
}
