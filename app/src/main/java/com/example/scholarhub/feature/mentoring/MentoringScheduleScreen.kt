package com.example.scholarhub.feature.mentoring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.scholarhub.feature.mentoring.components.SessionCard
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.WarningNotice
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SurfaceCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

const val AdminContact = "mailto:admin@scholarhub.example"

/** Waktu sekarang yang diperbarui tiap detik, untuk hitung mundur dan pergantian status sesi. */
@Composable
fun rememberNow(store: MentoringStore): LocalDateTime {
    var now by remember { mutableStateOf(store.now()) }
    LaunchedEffect(store) {
        while (true) {
            delay(1_000)
            now = store.now()
        }
    }
    return now
}

/** Kartu sesi untuk [booking] dari sudut pandang mentee (lawan sesi = mentor) atau mentor (lawan sesi = mentee). */
@Composable
fun BookingSessionCard(
    booking: Booking,
    now: LocalDateTime,
    asMentor: Boolean = false,
    modifier: Modifier = Modifier,
    showStatus: Boolean = true,
    onClick: (() -> Unit)? = null,
    onJoin: () -> Unit = {},
    onReport: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onContactAdmin: () -> Unit = {},
    mentorNote: String? = null,
) {
    val phase = if (showStatus) booking.phaseAt(now) else null
    SessionCard(
        person = if (asMentor) booking.mentee else booking.mentor,
        topic = booking.topic,
        date = booking.pillDate(),
        time = booking.timeRange(),
        phase = phase,
        modifier = modifier,
        countdown = Duration.between(now, booking.start).countdown(),
        onClick = onClick,
        onJoin = onJoin,
        onReport = onReport,
        onConfirm = onConfirm,
        onContactAdmin = onContactAdmin,
        mentorNote = mentorNote,
    )
}

/**
 * Sesi Mentoring Saya (Figma `218:4060` Mendatang, `309:17820`/`313:18937`/`316:19554` Riwayat,
 * `313:18780` konfirmasi). Hanya pesanan yang sudah dibayar yang menjadi sesi.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentoringScheduleScreen(
    store: MentoringStore,
    onBack: () -> Unit,
    onReport: (bookingId: String) -> Unit,
    initialHistory: Boolean = false,
) {
    val bookings by store.bookings.collectAsState()
    val now = rememberNow(store)
    var history by rememberSaveable { mutableStateOf(initialHistory) }
    var confirming by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val contactAdmin: () -> Unit = {
        try {
            uriHandler.openUri(AdminContact)
        } catch (_: Exception) {
            scope.launch { snackbar.showSnackbar("Aplikasi email tidak ditemukan.") }
        }
    }
    val joinRoom = { scope.launch { snackbar.showSnackbar("Tautan room belum tersedia.") } }

    val upcoming = bookings.filter { it.phaseAt(now)?.isUpcoming == true }.sortedBy { it.start }
    val past = bookings.filter { it.phaseAt(now)?.isUpcoming == false }.sortedByDescending { it.end }

    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "Sesi Mentoring Saya", onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SegmentedTabs(listOf("Mendatang", "Riwayat"), selected = if (history) 1 else 0) { history = it == 1 }
            if (!history) {
                Text(
                    if (upcoming.isEmpty()) {
                        "Belum ada sesi mendatang. Hanya pesanan yang sudah dibayar ditampilkan."
                    } else {
                        "${upcoming.size} sesi mendatang, diurutkan dari jadwal terdekat. Hanya pesanan yang sudah dibayar ditampilkan."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy,
                )
                upcoming.forEach { booking -> BookingSessionCard(booking, now, onJoin = { joinRoom() }) }
            } else {
                WarningNotice(
                    "Verifikasi Sesi Mentoring",
                    "Konfirmasi maksimal 24 jam setelah sesi mentoring berakhir atau sesi terkonfirmasi otomatis dan pembayaran masuk ke saldo mentor.",
                )
                if (past.isEmpty()) Text("Belum ada riwayat sesi.", style = MaterialTheme.typography.bodyMedium, color = Navy)
                past.forEach { booking ->
                    BookingSessionCard(
                        booking,
                        now,
                        onReport = { onReport(booking.id) },
                        onConfirm = { confirming = booking.id },
                        onContactAdmin = contactAdmin,
                    )
                }
            }
            if ((if (history) past else upcoming).isNotEmpty()) EndOfFeed("Semua sesi mentoring telah dimuat")
        }
    }

    confirming?.let { id ->
        ModalBottomSheet(
            onDismissRequest = { confirming = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            containerColor = SurfaceCard,
            dragHandle = null,
        ) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Sesi mentoring sudah selesai?", style = MaterialTheme.typography.titleMedium, color = Navy)
                Text(
                    "Pastikan sesi bersama mentor sudah terlaksana. Setelah dikonfirmasi, pembayaran akan masuk ke saldo mentor.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy,
                )
                PrimaryButton(
                    text = "Ya, Sesi Selesai",
                    onClick = {
                        store.confirm(id)
                        confirming = null
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    solidShadow = null,
                )
                OutlineButton("Batal", onClick = { confirming = null })
            }
        }
    }
}

/** Pengalih dua pilihan (Figma `Section - Sleek Role Switcher`): latar #D9EEFB, aktif navy teks putih. */
@Composable
fun SegmentedTabs(labels: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    val shape = RoundedCornerShape(ScholarHubRadius.md)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(Sky).padding(6.185.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEachIndexed { index, label ->
            val active = index == selected
            Text(
                label,
                Modifier.weight(1f).clip(shape).background(if (active) Navy else Sky)
                    .clickable(role = Role.Tab) { onSelect(index) }.padding(vertical = 10.308.dp),
                style = MaterialTheme.typography.labelLarge.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                color = if (active) SurfaceCard else Navy,
            )
        }
    }
}
