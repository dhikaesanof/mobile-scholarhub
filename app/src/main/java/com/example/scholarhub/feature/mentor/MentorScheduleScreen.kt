package com.example.scholarhub.feature.mentor

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.auth.components.AuthHeader
import com.example.scholarhub.feature.mentoring.BookingSessionCard
import com.example.scholarhub.feature.mentoring.LoggedInMentorId
import com.example.scholarhub.feature.mentoring.MentoringStore
import com.example.scholarhub.feature.mentoring.OpenSlot
import com.example.scholarhub.feature.mentoring.PaymentStatus
import com.example.scholarhub.feature.mentoring.SegmentedTabs
import com.example.scholarhub.feature.mentoring.SlotPlan
import com.example.scholarhub.feature.mentoring.freeSlots
import com.example.scholarhub.feature.mentoring.paymentStatusAt
import com.example.scholarhub.feature.mentoring.planSlots
import com.example.scholarhub.feature.mentoring.rememberNow
import com.example.scholarhub.feature.mentoring.slotLabel
import com.example.scholarhub.feature.payment.PageTitleSemi
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.cardSurface
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.ErrorContainer
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.ScholarHubRadius
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SkyBorder
import com.example.scholarhub.ui.theme.SkySoft
import com.example.scholarhub.ui.theme.Steel
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val Indonesian = Locale.forLanguageTag("id-ID")
private val DayLong = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Indonesian)
private val MonthTitle = DateTimeFormatter.ofPattern("MMMM yyyy", Indonesian)
private val HourLabel = DateTimeFormatter.ofPattern("HH.mm")
private val Hours = (6..22).map { LocalTime.of(it, 0) }
private val CardShape = RoundedCornerShape(ScholarHubRadius.md)
private val Caption = nunitoSans(11f, 16.5f, FontWeight.Normal)
private val SlotDot = Color(0xFF5BB8FE)

/** Sesi mentor yang sudah dibayar pada [day], dari yang paling awal. */
private fun List<com.example.scholarhub.feature.mentoring.Booking>.bookedOn(day: LocalDate, now: LocalDateTime) =
    filter { it.mentor.id == LoggedInMentorId && it.start.toLocalDate() == day && it.paymentStatusAt(now) == PaymentStatus.Paid }
        .sortedBy { it.start }

/**
 * Jadwal Sesi mentor (Figma `280:5490` Slot Tersedia, `280:5677` Sesi Dibooking, `280:5803` kosong,
 * `280:6041` tutup slot). Titik di tanggal: biru tua jika ada sesi dibooking, biru muda jika hanya ada slot.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorScheduleScreen(
    store: MentoringStore,
    onBack: () -> Unit,
    onOpenSlot: () -> Unit,
    onOpenMentee: (bookingId: String) -> Unit,
    onTabSelect: (MentorNavTab) -> Unit,
    initialDate: LocalDate? = null,
) {
    val bookings by store.bookings.collectAsState()
    val slots by store.openSlots.collectAsState()
    val now = rememberNow(store)
    val today = now.toLocalDate()
    val days = remember(today) { (-1..4).map { today.plusDays(it.toLong()) } }
    var selected by rememberSaveable { mutableStateOf(initialDate ?: today) }
    var bookedTab by rememberSaveable { mutableStateOf(false) }
    var closing by remember { mutableStateOf<OpenSlot?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val free = freeSlots(LoggedInMentorId, selected, bookings, slots, now)
    val booked = bookings.bookedOn(selected, now)

    Scaffold(
        containerColor = Base,
        topBar = { AuthHeader(badgeText = "MENTOR", onBack = onBack) },
        bottomBar = { MentorBottomBar(currentTab = MentorNavTab.Schedule, onTabSelect = onTabSelect) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Jadwal Sesi", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = Navy)
                    Text(
                        "+ Buka Slot Booking",
                        Modifier.clip(CircleShape).background(Sky).clickable(role = Role.Button, onClick = onOpenSlot)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = Ocean,
                    )
                }
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    days.forEach { day ->
                        val dot = when {
                            bookings.bookedOn(day, now).isNotEmpty() -> Ocean
                            freeSlots(LoggedInMentorId, day, bookings, slots, now).isNotEmpty() -> SlotDot
                            else -> null
                        }
                        DayTile(day, selected = day == selected, past = day.isBefore(today), dot = dot) { selected = day }
                    }
                }
            }
            SegmentedTabs(listOf("Slot Tersedia", "Sesi Dibooking"), selected = if (bookedTab) 1 else 0) { bookedTab = it == 1 }
            if (!bookedTab) {
                if (free.isEmpty()) {
                    Column(Modifier.fillMaxWidth().cardSurface(shape = CardShape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Belum ada slot tersedia", style = MaterialTheme.typography.titleMedium, color = Navy)
                        Text("Buka slot booking agar mentee dapat memilih waktu mentoring denganmu.", style = MaterialTheme.typography.bodyMedium, color = Navy)
                    }
                } else {
                    Text("${free.size} Slot Tersedia", style = MaterialTheme.typography.titleMedium, color = Navy)
                    free.forEach { start ->
                        Column(Modifier.fillMaxWidth().cardSurface(shape = CardShape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(start.slotLabel(), style = MaterialTheme.typography.titleSmall, color = Navy)
                            Text("Tersedia · Belum dipesan", style = Caption, color = Navy)
                            Text(
                                "Tutup Slot",
                                Modifier.fillMaxWidth().clip(CardShape).background(Navy)
                                    .clickable(role = Role.Button) { closing = OpenSlot(LoggedInMentorId, start) }.padding(vertical = 10.dp),
                                style = MaterialTheme.typography.labelLarge.copy(textAlign = TextAlign.Center),
                                color = Sky,
                            )
                        }
                    }
                    EndOfFeed("Semua slot tersedia telah dimuat")
                }
            } else {
                if (booked.isEmpty()) {
                    Column(Modifier.fillMaxWidth().cardSurface(shape = CardShape).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Belum ada sesi dibooking", style = MaterialTheme.typography.titleMedium, color = Navy)
                        Text("Sesi yang sudah dibayar mentee di tanggal ini akan tampil di sini.", style = MaterialTheme.typography.bodyMedium, color = Navy)
                    }
                } else {
                    Text("${booked.size} Sesi Dibooking", style = MaterialTheme.typography.titleMedium, color = Navy)
                    booked.forEach { booking ->
                        BookingSessionCard(
                            booking,
                            now,
                            asMentor = true,
                            onClick = { onOpenMentee(booking.id) },
                            onJoin = { scope.launch { snackbar.showSnackbar("Tautan room belum tersedia.") } },
                        )
                    }
                    EndOfFeed("Semua sesi mentoring telah dimuat")
                }
            }
        }
    }

    closing?.let { slot ->
        ModalBottomSheet(
            onDismissRequest = { closing = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            containerColor = SurfaceCard,
            dragHandle = null,
        ) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Tutup slot booking?", style = MaterialTheme.typography.titleMedium, color = Navy)
                Text("${slot.start.format(DayLong)}\n${slot.start.slotLabel()}", style = MaterialTheme.typography.bodyMedium, color = Navy)
                Text("Slot yang belum dipesan ini akan dihapus dari pilihan mentee.", style = MaterialTheme.typography.bodyMedium, color = Navy)
                PrimaryButton(
                    text = "Tutup Slot",
                    onClick = {
                        store.closeSlot(slot)
                        closing = null
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    solidShadow = null,
                )
                OutlineButton("Batal", onClick = { closing = null })
            }
        }
    }
}

/** Tombol tanggal (Figma `Horizontal Date Strip`): aktif navy lebih tinggi, hari lewat abu, titik status di bawah. */
@Composable
private fun DayTile(day: LocalDate, selected: Boolean, past: Boolean, dot: Color?, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.369.dp)
    Column(
        Modifier.widthIn(min = if (selected) 57.7.dp else 53.6.dp).height(if (selected) 74.2.dp else 70.1.dp)
            .then(if (selected) Modifier.clip(shape).background(Navy) else Modifier.cardSurface(shape = shape))
            .clickable(role = Role.Tab, onClick = onClick).padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            day.dayOfWeek.getDisplayName(TextStyle.SHORT, Indonesian).take(3).uppercase(),
            style = nunitoSans(10.308f, 14.431f, FontWeight.Bold, tracking = 0.4123f),
            color = if (selected) SkyBorder else TextVariant,
        )
        Text(
            day.dayOfMonth.toString(),
            Modifier.padding(top = 2.dp),
            style = nunitoSans(18.554f, 24.738f, FontWeight.Bold),
            color = when {
                selected -> SurfaceCard
                past -> TextVariant
                else -> TextStrong
            },
        )
        Box(Modifier.padding(top = 4.dp).size(6.185.dp).clip(CircleShape).background(if (selected && dot != null) SlotDot else dot ?: Color.Transparent))
    }
}

/**
 * Buka Slot Booking (Figma `278:15247`, pemilih tanggal `278:14249`, validasi `280:5865` dan `278:15406`).
 * Rentang jam dibagi per jam; pratinjau diganti pesan merah jika rentang tidak valid atau bentrok.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenSlotScreen(
    store: MentoringStore,
    onBack: () -> Unit,
    onOpened: (date: LocalDate, starts: List<LocalDateTime>) -> Unit,
) {
    val bookings by store.bookings.collectAsState()
    val slots by store.openSlots.collectAsState()
    val now = rememberNow(store)
    var date by rememberSaveable { mutableStateOf(now.toLocalDate().plusDays(1)) }
    var from by rememberSaveable { mutableStateOf(LocalTime.of(14, 0)) }
    var to by rememberSaveable { mutableStateOf(LocalTime.of(17, 0)) }
    var pickingDate by remember { mutableStateOf(false) }
    var startMenu by remember { mutableStateOf(false) }
    val plan = planSlots(LoggedInMentorId, date, from, to, now, bookings, slots)

    Scaffold(containerColor = Base, topBar = { AuthHeader(badgeText = "MENTOR", onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Buka Slot Booking", style = MaterialTheme.typography.titleMedium, color = Navy)
                Text("Pilih satu tanggal dan rentang jam. Slot akan dibagi otomatis per jam.", style = MaterialTheme.typography.bodyMedium, color = Navy)
            }
            Column(Modifier.fillMaxWidth().cardSurface(shape = CardShape).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tanggal sesi", style = MaterialTheme.typography.bodyMedium, color = Navy)
                Row(
                    Modifier.fillMaxWidth().clip(CardShape).background(SkySoft).clickable(role = Role.DropdownList) { pickingDate = true }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(date.format(DayLong), Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = Navy)
                    Image(painterResource(R.drawable.ic_report_down), null, Modifier.size(20.dp))
                }
                Text("Hanya tanggal dan waktu yang akan datang", style = Caption, color = Steel)
            }
            Row(
                Modifier.fillMaxWidth().cardSurface(shape = CardShape).padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HourField("Jam mulai (WIB)", "Pilih jam bulat", from, startMenu, { startMenu = it }, Modifier.weight(1f)) { from = it }
                var endMenu by remember { mutableStateOf(false) }
                HourField("Jam selesai (WIB)", "Batas akhir slot", to, endMenu, { endMenu = it }, Modifier.weight(1f)) { to = it }
            }
            when (plan) {
                is SlotPlan.Ok -> {
                    Text(
                        "Pastikan rentang jam tidak bertabrakan dengan slot tersedia atau sesi yang sudah dibooking.",
                        style = Caption,
                        color = Navy,
                    )
                    Column(Modifier.fillMaxWidth().cardSurface(shape = RoundedCornerShape(16.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Pratinjau · ${plan.starts.size} slot", style = MaterialTheme.typography.titleMedium, color = Navy)
                        Text(plan.starts.joinToString("\n") { it.slotLabel() }, style = MaterialTheme.typography.bodyMedium, color = Navy)
                        Text("Satu sesi mentoring berdurasi 59 menit", style = Caption, color = Navy)
                    }
                    PrimaryButton(
                        text = "Buka ${plan.starts.size} Slot",
                        onClick = {
                            store.openSlots(LoggedInMentorId, plan)
                            onOpened(date, plan.starts)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        solidShadow = null,
                    )
                }
                else -> {
                    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(ErrorContainer).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            if (plan is SlotPlan.Clash) "Rentang jam bertabrakan" else "Rentang jam tidak valid",
                            style = MaterialTheme.typography.titleMedium,
                            color = ErrorRed,
                        )
                        Text(
                            if (plan is SlotPlan.Clash) plan.message else "Pastikan jam yang dimasukkan sudah benar!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ErrorRed,
                        )
                    }
                    PrimaryButton(
                        text = "Perbaiki Rentang Jam",
                        onClick = { startMenu = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        solidShadow = null,
                    )
                }
            }
        }
    }

    if (pickingDate) {
        DatePickerSheet(selected = date, today = now.toLocalDate(), onDismiss = { pickingDate = false }) {
            date = it
            pickingDate = false
        }
    }
}

@Composable
private fun HourField(
    label: String,
    helper: String,
    value: LocalTime,
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    modifier: Modifier,
    onSelect: (LocalTime) -> Unit,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Navy)
        Box {
            Text(
                value.format(HourLabel),
                Modifier.fillMaxWidth().clip(CardShape).background(SkySoft).clickable(role = Role.DropdownList) { onOpenChange(true) }.padding(12.dp),
                style = MaterialTheme.typography.labelLarge,
                color = Navy,
            )
            DropdownMenu(expanded = open, onDismissRequest = { onOpenChange(false) }) {
                Hours.forEach { hour ->
                    DropdownMenuItem(text = { Text(hour.format(HourLabel), color = Navy) }, onClick = {
                        onSelect(hour)
                        onOpenChange(false)
                    })
                }
            }
        }
        Text(helper, style = Caption, color = Steel)
    }
}

/** "Pilih Tanggal" (Figma `281:5973`): kalender bulan ini dan bulan depan; tanggal sebelum hari ini tidak bisa dipilih. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerSheet(selected: LocalDate, today: LocalDate, onDismiss: () -> Unit, onPick: (LocalDate) -> Unit) {
    var draft by remember { mutableStateOf(selected) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = SurfaceCard,
        dragHandle = { Box(Modifier.padding(top = 16.dp).size(40.dp, 4.dp).clip(RoundedCornerShape(2.dp)).background(Steel)) },
    ) {
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Pilih Tanggal", style = PageTitleSemi, color = Navy)
            listOf(YearMonth.from(today), YearMonth.from(today).plusMonths(1)).forEach { month ->
                MonthGrid(month, draft, today) { draft = it }
            }
            Text("Tanggal sebelum hari ini tidak dapat dipilih.", style = Caption, color = Navy)
            PrimaryButton(
                text = "Pilih ${draft.dayOfMonth} ${draft.month.getDisplayName(TextStyle.FULL, Indonesian)}",
                onClick = { onPick(draft) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                solidShadow = null,
            )
        }
    }
}

@Composable
private fun MonthGrid(month: YearMonth, selected: LocalDate, today: LocalDate, onPick: (LocalDate) -> Unit) {
    val shape = RoundedCornerShape(ScholarHubRadius.md)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(month.atDay(1).format(MonthTitle), style = MaterialTheme.typography.titleMedium, color = Navy)
        Row {
            listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min").forEach {
                Text(it, Modifier.weight(1f), style = Caption.copy(textAlign = TextAlign.Center), color = Navy)
            }
        }
        val lead = month.atDay(1).dayOfWeek.value - 1
        val cells = List(lead) { null } + (1..month.lengthOfMonth()).map { month.atDay(it) }
        cells.chunked(7).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (0 until 7).forEach { i ->
                    val day = week.getOrNull(i)
                    val enabled = day != null && !day.isBefore(today)
                    val isSelected = day == selected
                    Box(
                        Modifier.weight(1f).heightIn(min = 40.dp).clip(shape)
                            .background(if (isSelected) Navy else SurfaceCard)
                            .border(1.dp, Navy, shape)
                            .then(if (enabled) Modifier.clickable(role = Role.Button) { onPick(day!!) } else Modifier)
                            .alpha(if (day == null || enabled) 1f else 0.5f),
                        contentAlignment = Alignment.Center,
                    ) {
                        day?.let {
                            Text(it.dayOfMonth.toString(), style = MaterialTheme.typography.labelLarge, color = if (isSelected) SurfaceCard else Navy)
                        }
                    }
                }
            }
        }
    }
}

/** Slot Berhasil Dibuka (Figma `280:5639`). */
@Composable
fun SlotOpenedScreen(date: LocalDate, starts: List<LocalDateTime>, onBack: () -> Unit, onSeeSlots: () -> Unit) {
    Scaffold(containerColor = Base, topBar = { AuthHeader(badgeText = "MENTOR", onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Image(
                painterResource(R.drawable.mascot_slot_opened),
                null,
                Modifier.align(Alignment.CenterHorizontally).size(width = 174.dp, height = 237.dp),
                contentScale = ContentScale.Fit,
            )
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFD7F5E5)).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("Slot berhasil dibuka", style = PageTitleSemi, color = Color(0xFF065F46))
                Text(
                    "${starts.size} slot kini dapat dipilih mentee pada halaman booking mentor.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF065F46),
                )
            }
            Column(Modifier.fillMaxWidth().cardSurface(shape = RoundedCornerShape(16.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(date.format(DayLong), style = MaterialTheme.typography.titleMedium, color = Navy)
                Text(starts.joinToString("\n") { it.slotLabel() }, style = MaterialTheme.typography.bodyMedium, color = Navy)
            }
            PrimaryButton(text = "Lihat Slot Tersedia", onClick = onSeeSlots, modifier = Modifier.fillMaxWidth().height(48.dp), solidShadow = null)
            Spacer(Modifier.height(8.dp))
        }
    }
}
