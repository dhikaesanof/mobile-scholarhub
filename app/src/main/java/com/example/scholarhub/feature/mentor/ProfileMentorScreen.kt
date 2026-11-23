package com.example.scholarhub.feature.mentor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.feature.auth.components.AuthHeader
import com.example.scholarhub.feature.mentoring.Booking
import com.example.scholarhub.feature.mentoring.BookingSessionCard
import com.example.scholarhub.feature.mentoring.MentoringStore
import com.example.scholarhub.feature.mentoring.rememberNow
import com.example.scholarhub.feature.mentoring.rupiah
import com.example.scholarhub.feature.profile.FormActionCard
import com.example.scholarhub.feature.profile.FormScaffold
import com.example.scholarhub.feature.profile.components.LogoutButton
import com.example.scholarhub.feature.profile.components.ProfileSectionTitle
import com.example.scholarhub.ui.components.EndOfFeed
import com.example.scholarhub.ui.components.FormPickerField
import com.example.scholarhub.ui.components.FormSectionCard
import com.example.scholarhub.ui.components.FormTextField
import com.example.scholarhub.ui.components.HelpRow
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.PhotoCard
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ProfileInfoCard
import com.example.scholarhub.ui.components.ProfileInfoSection
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.OutlineMist
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.SurfaceCard
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Shape12 = RoundedCornerShape(12.dp)
private val Muted = Color(0xFF6F7582)
private val ItemBlue = Color(0xFFD8EFFD)
private val LinkBlue = Color(0xFF0077B6)
private val Caption = nunitoSans(11f, 16.5f, FontWeight.Normal)
private val LongDate = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("id-ID"))
private val ShortDate = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))

/**
 * Profil Mentor (Figma `372:24634` lengkap, `323:6672` belum lengkap): kartu profil, aktivitas mentoring, saldo dan
 * rekening, riwayat penghasilan/penarikan, serta Bantuan & Keamanan.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMentorScreen(
    store: MentorProfileStore,
    mentoring: MentoringStore,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onSessionHistory: () -> Unit,
    onIncomeHistory: () -> Unit,
    onWithdrawalHistory: () -> Unit,
    onHelp: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit,
    onTabSelect: (MentorNavTab) -> Unit,
) {
    val profile by store.profile.collectAsState()
    val bookings by mentoring.bookings.collectAsState()
    val now = rememberNow(mentoring)
    val history = bookings.mentorHistory(now)
    val earnings = bookings.mentorEarnings(now)
    val totalFee = earnings.sumOf { it.price }
    val balance = totalFee - profile.withdrawals.sumOf { it.amount }
    var editAccount by remember { mutableStateOf(false) }
    var confirmWithdraw by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Base,
        topBar = { AuthHeader(badgeText = "MENTOR", onBack = onBack) },
        bottomBar = { MentorBottomBar(currentTab = MentorNavTab.Profile, onTabSelect = onTabSelect) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfileInfoCard(
                name = profile.name,
                photo = profile.photo,
                fallbackPhoto = R.drawable.mentor_samantha,
                subtitle = profile.scholarship.ifBlank { "Status beasiswa belum ditambahkan" },
                bio = profile.about.ifBlank { "Lengkapi profil agar mentee mengenal pengalaman dan keahlianmu." },
                sections = listOf(
                    ProfileInfoSection(
                        "Profil Mentor",
                        listOf(
                            "Universitas" to profile.campus.orEmpty(),
                            "Program Studi" to profile.major.orEmpty(),
                            "Spesialisasi" to profile.specializations.joinToString(", "),
                        ),
                    ),
                    ProfileInfoSection("Informasi Kontak", listOf("Instagram" to profile.instagram, "Email" to profile.email)),
                ),
                actionLabel = if (profile.complete) "Edit Profil" else "Lengkapi Profil",
                onAction = onEdit,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileSectionTitle("Aktivitas Mentoring")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActivityTile(R.drawable.ic_mentor_sessions_done, 17.008.dp, 13.915.dp, earnings.size.toString(), "Sesi Selesai", Modifier.weight(1f))
                    ActivityTile(R.drawable.ic_mentor_fee, 15.462.dp, 14.688.dp, rupiah(totalFee), "Total Fee Mentoring", Modifier.weight(1f))
                }
                if (history.isEmpty()) {
                    WhiteCard {
                        Text("Riwayat Mentoring", style = MaterialTheme.typography.titleSmall, color = Navy)
                        Text("Belum ada sesi mentoring", style = Caption, color = Navy)
                    }
                } else {
                    Row(
                        Modifier.fillMaxWidth().clip(Shape12).background(SurfaceCard).clickable(onClick = onSessionHistory).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Riwayat Mentoring", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = Navy)
                        Image(painterResource(R.drawable.ic_profile_row_chevron), null, Modifier.size(6.167.dp, 10.dp))
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileSectionTitle("Penghasilan")
                WhiteCard(spacing = 12.dp) {
                    Text(rupiah(balance), style = nunitoSans(23f, 38.4f, FontWeight.SemiBold), color = Navy)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(profile.account?.masked ?: "No. Rek", style = MaterialTheme.typography.bodyMedium, color = Navy)
                            Text("a.n. ${profile.account?.holder ?: "xxx xxx"}", style = MaterialTheme.typography.bodyMedium, color = Navy)
                        }
                        Text(
                            if (profile.account == null) "Tambah Rekening" else "Ubah Rekening",
                            Modifier.clip(Shape12).background(Sky).border(1.dp, Color(0x3B006398), Shape12)
                                .clickable(role = Role.Button) { editAccount = true }.padding(8.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Navy,
                        )
                    }
                    if (balance == 0) Text("Saldo akan bertambah setelah sesi mentoring selesai.", style = MaterialTheme.typography.bodyMedium, color = Muted)
                    PrimaryButton(
                        text = "Tarik Fee Sekarang",
                        onClick = { confirmWithdraw = true },
                        enabled = balance > 0 && profile.account != null,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        solidShadow = null,
                    )
                }
            }

            HistoryPreviewCard(
                title = "Riwayat Penghasilan",
                emptyTitle = "Belum ada penghasilan",
                emptyBody = "Penghasilan akan tercatat setelah sesi mentoring selesai.",
                onSeeAll = onIncomeHistory.takeIf { earnings.isNotEmpty() },
            ) { earnings.firstOrNull()?.let { IncomeItem(it) } }
            HistoryPreviewCard(
                title = "Riwayat Penarikan",
                emptyTitle = "Belum ada penarikan",
                emptyBody = "Riwayat penarikan saldo akan muncul di sini.",
                onSeeAll = onWithdrawalHistory.takeIf { profile.withdrawals.isNotEmpty() },
            ) { profile.withdrawals.firstOrNull()?.let { WithdrawalItem(it) } }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileSectionTitle("Bantuan & Keamanan")
                HelpRow(R.drawable.ic_profile_help, 17.179.dp, 15.462.dp, "Pusat Bantuan", onHelp)
                HelpRow(R.drawable.ic_profile_password, 13.333.dp, 17.5.dp, "Ganti Password", onChangePassword)
            }
            LogoutButton(onClick = onLogout, label = "Keluar dari Akun Mentor")
        }
    }

    if (editAccount) {
        var bank by remember { mutableStateOf(profile.account?.bank.orEmpty()) }
        var number by remember { mutableStateOf(profile.account?.number.orEmpty()) }
        var holder by remember { mutableStateOf(profile.account?.holder.orEmpty()) }
        MentorSheet(onDismiss = { editAccount = false }) {
            Text(if (profile.account == null) "Tambah Rekening" else "Ubah Rekening", style = MaterialTheme.typography.titleMedium, color = Navy)
            FormTextField("Nama Bank", bank, { bank = it }, "Contoh: BCA")
            FormTextField("Nomor Rekening", number, { number = it.filter(Char::isDigit).take(20) }, "Nomor rekening", keyboardType = KeyboardType.Number)
            FormTextField("Atas Nama", holder, { holder = it }, "Nama pemilik rekening")
            PrimaryButton(
                text = "Simpan Rekening",
                onClick = {
                    store.update { it.copy(account = BankAccount(bank.trim(), number, holder.trim())) }
                    editAccount = false
                },
                enabled = bank.isNotBlank() && number.length >= 6 && holder.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                solidShadow = null,
            )
            OutlineButton("Batal", onClick = { editAccount = false })
        }
    }

    if (confirmWithdraw) {
        MentorSheet(onDismiss = { confirmWithdraw = false }) {
            Text("Tarik fee sekarang?", style = MaterialTheme.typography.titleMedium, color = Navy)
            Text(
                "Saldo ${rupiah(balance)} akan dikirim ke ${profile.account?.masked} a.n. ${profile.account?.holder}.",
                style = MaterialTheme.typography.bodyMedium,
                color = Navy,
            )
            PrimaryButton(
                text = "Tarik Fee",
                onClick = {
                    store.withdraw(balance, mentoring.now())
                    confirmWithdraw = false
                    scope.launch { snackbar.showSnackbar("Penarikan berhasil diajukan.") }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                solidShadow = null,
            )
            OutlineButton("Batal", onClick = { confirmWithdraw = false })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MentorSheet(onDismiss: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        containerColor = SurfaceCard,
    ) {
        Column(Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}

@Composable
private fun WhiteCard(spacing: Dp = 8.dp, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().clip(Shape12).background(SurfaceCard).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content,
    )
}

/** Tile Aktivitas Mentoring (Figma `Statistik Ringkas Mentor`): ikon dalam lingkaran #E0E9EF 33 dp, nilai Bold 19. */
@Composable
private fun ActivityTile(@DrawableRes icon: Int, iconWidth: Dp, iconHeight: Dp, value: String, label: String, modifier: Modifier) {
    Column(
        modifier.shadow(1.dp, Shape12).clip(Shape12).background(SurfaceCard).padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(Modifier.padding(bottom = 4.dp).size(33.dp).clip(CircleShape).background(OutlineMist), contentAlignment = Alignment.Center) {
            Image(painterResource(icon), null, Modifier.size(iconWidth, iconHeight))
        }
        Text(value, style = MaterialTheme.typography.titleMedium, color = Navy)
        Text(label, style = MaterialTheme.typography.labelLarge, color = TextVariant)
    }
}

@Composable
private fun HistoryPreviewCard(title: String, emptyTitle: String, emptyBody: String, onSeeAll: (() -> Unit)?, item: @Composable () -> Unit) {
    WhiteCard(spacing = 12.dp) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, color = Navy)
            if (onSeeAll != null) {
                Text("Semua Riwayat", Modifier.clickable(role = Role.Button, onClick = onSeeAll), style = MaterialTheme.typography.bodyMedium, color = LinkBlue)
            }
        }
        if (onSeeAll == null) {
            Text(emptyTitle, style = MaterialTheme.typography.labelLarge, color = Navy)
            Text(emptyBody, style = MaterialTheme.typography.bodyMedium, color = Muted)
        } else {
            item()
        }
    }
}

/** Item Riwayat Penghasilan (Figma `Mentor · Item Riwayat Penghasilan` `329:6919`). */
@Composable
private fun IncomeItem(booking: Booking) {
    Row(Modifier.fillMaxWidth().clip(Shape12).background(ItemBlue).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(booking.mentee.name, style = MaterialTheme.typography.labelLarge, color = Navy)
            Text(booking.topic, style = Caption, color = Navy)
            Text(booking.start.format(LongDate), style = Caption, color = Navy)
            Text(booking.invoice, style = Caption, color = Muted)
        }
        Text("+${rupiah(booking.price)}", style = MaterialTheme.typography.labelLarge, color = Navy)
    }
}

/** Item Riwayat Penarikan (Figma `Mentor · Item Riwayat Penarikan` `329:6926`). */
@Composable
private fun WithdrawalItem(withdrawal: Withdrawal) {
    Row(Modifier.fillMaxWidth().clip(Shape12).background(ItemBlue).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Penarikan Berhasil", style = MaterialTheme.typography.labelLarge, color = Navy)
            Text("${withdrawal.at.format(ShortDate)} · ${withdrawal.account.bank} Transfer", style = Caption, color = Navy)
            Text(withdrawal.account.masked, style = Caption, color = Muted)
        }
        Text("-${rupiah(withdrawal.amount)}", style = MaterialTheme.typography.labelLarge, color = Navy)
    }
}

/** Kerangka daftar riwayat (Figma `329:6965`, `329:7245`): kartu ringkasan, kartu daftar, penanda akhir. */
@Composable
private fun HistoryListScreen(title: String, total: String, summary: String, endText: String, onBack: () -> Unit, items: @Composable ColumnScope.() -> Unit) {
    Scaffold(containerColor = Base, topBar = { ScholarHubDetailTopBar(title = title, onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            WhiteCard {
                Text(total, style = nunitoSans(23f, 38.4f, FontWeight.SemiBold), color = Navy)
                Text(summary, style = MaterialTheme.typography.bodyMedium, color = Muted)
            }
            WhiteCard(spacing = 12.dp, content = items)
            EndOfFeed(endText)
        }
    }
}

@Composable
fun IncomeHistoryScreen(mentoring: MentoringStore, onBack: () -> Unit) {
    val bookings by mentoring.bookings.collectAsState()
    val earnings = bookings.mentorEarnings(rememberNow(mentoring))
    HistoryListScreen(
        "Riwayat Penghasilan",
        rupiah(earnings.sumOf { it.price }),
        "Total penghasilan dari ${earnings.size} sesi mentoring yang telah selesai.",
        "Semua riwayat penghasilan telah dimuat",
        onBack,
    ) { earnings.forEach { IncomeItem(it) } }
}

@Composable
fun WithdrawalHistoryScreen(store: MentorProfileStore, mentoring: MentoringStore, onBack: () -> Unit) {
    val profile by store.profile.collectAsState()
    val bookings by mentoring.bookings.collectAsState()
    val withdrawn = profile.withdrawals.sumOf { it.amount }
    val balance = bookings.mentorEarnings(rememberNow(mentoring)).sumOf { it.price } - withdrawn
    HistoryListScreen(
        "Riwayat Penarikan",
        rupiah(withdrawn),
        "Total penarikan berhasil · ${profile.withdrawals.size} transaksi. Saldo tersedia ${rupiah(balance)}.",
        "Semua riwayat penarikan telah dimuat",
        onBack,
    ) { profile.withdrawals.forEach { WithdrawalItem(it) } }
}

/** Riwayat Mentoring mentor (Figma `364:7709`): sesi menunggu konfirmasi, dalam peninjauan, dan selesai. */
@Composable
fun MentorSessionHistoryScreen(mentoring: MentoringStore, onBack: () -> Unit, onContactAdmin: () -> Unit) {
    val bookings by mentoring.bookings.collectAsState()
    val now = rememberNow(mentoring)
    Scaffold(containerColor = Base, topBar = { ScholarHubDetailTopBar(title = "Riwayat Mentoring", onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            bookings.mentorHistory(now).forEach { booking ->
                BookingSessionCard(booking, now, asMentor = true, onContactAdmin = onContactAdmin, mentorNote = booking.mentorNote(now))
            }
            EndOfFeed("Semua riwayat mentoring telah dimuat")
        }
    }
}

/** Lengkapi Profil (Figma `323:6828`) / Edit Profil terisi (`323:6940`) mentor. */
@Composable
fun MentorEditProfileScreen(
    profile: MentorProfile,
    campusOptions: List<String>,
    majorOptions: List<String>,
    onBack: () -> Unit,
    onSave: (MentorProfile) -> Unit,
) {
    var draft by remember { mutableStateOf(profile) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> uri?.let { draft = draft.copy(photo = it) } }
    val emailError = if (draft.email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(draft.email.trim()).matches()) "Format email tidak valid." else null
    val valid = draft.name.isNotBlank() && draft.email.isNotBlank() && emailError == null

    FormScaffold(
        if (profile.complete) "Edit Profil" else "Lengkapi Profil",
        "Perbarui profil agar mentee mengenal pengalaman dan keahlian mentoringmu.",
        onBack,
    ) {
        PhotoCard(draft.photo, R.drawable.mentor_samantha) {
            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        FormSectionCard("Identitas diri") {
            FormTextField("Nama Lengkap", draft.name, { draft = draft.copy(name = it) }, "Nama lengkap", "Nama ini ditampilkan pada profil dan daftar mentor.",
                error = if (draft.name.isBlank()) "Nama wajib diisi." else null)
        }
        FormSectionCard("Profil Mentor") {
            FormTextField("Status Beasiswa", draft.scholarship, { draft = draft.copy(scholarship = it) }, "Contoh: Awardee Beasiswa Unggulan",
                "Tuliskan pengalaman beasiswa yang relevan.")
            FormTextField("Tentang Saya", draft.about, { draft = draft.copy(about = it) }, "Ceritakan pengalaman dan pendekatan mentoringmu.",
                "Perkenalkan pengalamanmu secara singkat.", minHeight = 96.dp)
        }
        FormSectionCard("Pendidikan & Keahlian") {
            FormPickerField("Perguruan Tinggi", draft.campus, "Pilih perguruan tinggi", campusOptions, { draft = draft.copy(campus = it) },
                "Gunakan kampus tempat kamu kuliah atau lulus.")
            FormPickerField("Program Studi", draft.major, "Pilih program studi", majorOptions, { draft = draft.copy(major = it) },
                "Pilih program studi sesuai latar pendidikanmu.")
            FormPickerField(
                "Spesialisasi Mentoring",
                draft.specializations.takeIf { it.isNotEmpty() }?.joinToString(", "),
                "Pilih spesialisasi mentoring",
                MentorSpecializations,
                { s -> draft = draft.copy(specializations = if (s in draft.specializations) draft.specializations - s else draft.specializations + s) },
                "Kamu dapat memilih lebih dari satu spesialisasi.",
                selected = draft.specializations.toSet(),
                multi = true,
            )
        }
        FormSectionCard("Informasi Kontak") {
            FormTextField("Instagram · opsional", draft.instagram, { draft = draft.copy(instagram = it.trim()) }, "Contoh: @namapengguna",
                "Ditampilkan sebagai kontak pada profil mentor.")
            FormTextField("Email", draft.email, { draft = draft.copy(email = it.trim()) }, "Email aktif", "Gunakan email aktif yang dapat dihubungi mentee.",
                emailError, KeyboardType.Email)
        }
        FormActionCard("Simpan perubahan", "Periksa kembali informasi sebelum menyimpan.", "Simpan Perubahan", valid, { onSave(draft.copy(name = draft.name.trim())) })
    }
}
