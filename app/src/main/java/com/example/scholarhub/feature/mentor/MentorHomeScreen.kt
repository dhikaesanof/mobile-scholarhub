package com.example.scholarhub.feature.mentor

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.auth.components.AuthHeader
import com.example.scholarhub.feature.mentoring.Booking
import com.example.scholarhub.feature.mentoring.BookingSessionCard
import com.example.scholarhub.feature.mentoring.LoggedInMentorId
import com.example.scholarhub.feature.mentoring.MentoringStore
import com.example.scholarhub.feature.mentoring.SessionPhase
import com.example.scholarhub.feature.mentoring.isUpcoming
import com.example.scholarhub.feature.mentoring.phaseAt
import com.example.scholarhub.feature.mentoring.rememberNow
import com.example.scholarhub.feature.mentoring.rupiah
import com.example.scholarhub.feature.payment.PageTitleSemi
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.SectionLink
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.TextVariant
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.scholarhub.ui.theme.SurfaceCard
import androidx.compose.ui.graphics.Color
import com.example.scholarhub.ui.components.PrimaryButton
import androidx.compose.foundation.layout.height

private val LongDate = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("id-ID"))

/** Sesi milik mentor yang masuk yang sudah dibayar dan belum berakhir, dari yang terdekat. */
fun List<Booking>.upcomingForMentor(now: LocalDateTime) =
    filter { it.mentor.id == LoggedInMentorId && it.phaseAt(now)?.isUpcoming == true }.sortedBy { it.start }

/**
 * Home Mentor (Figma `234:11763`): ringkasan hari ini (sesi aktif, saldo penghasilan) dan sesi terdekat.
 * Saldo = jumlah sesi selesai milik mentor; pencairan saldo ada di Profil mentor.
 */
@Composable
fun MentorHomeScreen(
    store: MentoringStore,
    onBack: () -> Unit,
    onSeeAll: () -> Unit,
    onOpenMentee: (bookingId: String) -> Unit,
    onTabSelect: (MentorNavTab) -> Unit,
    onOpenSlot: () -> Unit = {},
    onOpenNotifications: () -> Unit = {},
) {
    val bookings by store.bookings.collectAsState()
    val now = rememberNow(store)
    val upcoming = bookings.upcomingForMentor(now)
    val balance = bookings.filter { it.mentor.id == LoggedInMentorId && it.phaseAt(now) == SessionPhase.Completed }.sumOf { it.price }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Base,
        topBar = { AuthHeader(badgeText = "MENTOR", onBack = onBack, onNotificationClick = onOpenNotifications) },
        bottomBar = { MentorBottomBar(currentTab = MentorNavTab.Home, onTabSelect = onTabSelect) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 16.492.dp, end = 16.492.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Selamat Datang, Samantha!", style = MaterialTheme.typography.titleMedium, color = Navy)
                    Image(painterResource(R.drawable.mascot_greeting), null, Modifier.size(width = 22.dp, height = 17.dp))
                }
                Text("Yuk, bantu mentee selangkah lebih dekat dengan beasiswa impiannya!", style = MaterialTheme.typography.bodyMedium, color = Navy)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ringkasan Hari Ini", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = Navy)
                    Image(painterResource(R.drawable.ic_calendar_month), null, Modifier.size(12.dp, 15.333.dp))
                    Text(" ${now.format(LongDate)}", style = MaterialTheme.typography.bodyLarge, color = Ocean)
                }
                StatCard("SESI", upcoming.size.toString(), "Sesi Terjadwal", R.drawable.ic_mentor_stat_session)
                StatCard("SALDO PENGHASILAN", rupiah(balance), null, R.drawable.ic_mentor_stat_wallet)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sesi Terdekat", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = Navy)
                    SectionLink(text = "Lihat Semua", onClick = onSeeAll)
                }
                val nearest = upcoming.firstOrNull()
                if (nearest == null) {
                    // Figma `Home · Belum Ada Sesi` (381:25256).
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceCard).padding(horizontal = 16.dp, vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Image(painterResource(R.drawable.ic_mentor_empty_calendar), null, Modifier.size(40.dp))
                        Text("Belum ada sesi mentoring terjadwal", Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleSmall, color = Navy)
                        Text(
                            "Buka slot booking agar mentee dapat memilih jadwal mentoring bersamamu.",
                            Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6F7582),
                        )
                        PrimaryButton(text = "Buka Slot Booking", onClick = onOpenSlot, modifier = Modifier.fillMaxWidth().height(48.dp), solidShadow = null)
                    }
                } else {
                    BookingSessionCard(
                        nearest,
                        now,
                        asMentor = true,
                        onClick = { onOpenMentee(nearest.id) },
                        onJoin = { scope.launch { snackbar.showSnackbar("Tautan room belum tersedia.") } },
                    )
                }
            }
        }
    }
}

/** Kartu ringkasan (Figma `Sesi Hari Ini` / `SALDO PENGHASILAN`): label SemiBold 11, nilai SemiBold 23, ikon dalam lingkaran #D9EEFB. */
@Composable
private fun StatCard(label: String, value: String, caption: String?, @DrawableRes icon: Int) {
    Column(Modifier.fillMaxWidth().cardSurface(shape = RoundedCornerShape(12.369.dp)).padding(12.369.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = TextVariant)
            Box(Modifier.size(24.dp).clip(CircleShape).background(Sky), contentAlignment = Alignment.Center) {
                Image(painterResource(icon), null, Modifier.size(width = 11.5.dp, height = 10.8.dp))
            }
        }
        Text(value, style = PageTitleSemi, color = Navy)
        caption?.let { Text(it, style = MaterialTheme.typography.labelSmall, color = Navy) }
    }
}

/**
 * Semua Jadwal mentor (Figma `234:12419`, "Sesi Dibooking"): sesi mentee yang sudah dibayar, dari yang terdekat.
 * Mengetuk kartu membuka Profil Mentee.
 */
@Composable
fun MentorBookedSessionsScreen(
    store: MentoringStore,
    onBack: () -> Unit,
    onOpenMentee: (bookingId: String) -> Unit,
) {
    val bookings by store.bookings.collectAsState()
    val now = rememberNow(store)
    val upcoming = bookings.upcomingForMentor(now)
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Semua Jadwal", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Sesi Dibooking", style = PageTitleSemi, color = Navy)
            Text(
                if (upcoming.isEmpty()) "Belum ada sesi mendatang." else "${upcoming.size} sesi mendatang, diurutkan dari jadwal terdekat.",
                style = MaterialTheme.typography.bodyMedium,
                color = Navy,
            )
            upcoming.forEach { booking ->
                BookingSessionCard(
                    booking,
                    now,
                    asMentor = true,
                    onClick = { onOpenMentee(booking.id) },
                    onJoin = { scope.launch { snackbar.showSnackbar("Tautan room belum tersedia.") } },
                )
            }
            if (upcoming.isNotEmpty()) EndOfFeed("Semua sesi mentoring telah dimuat")
        }
    }
}
