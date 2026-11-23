package com.example.scholarhub.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import com.example.scholarhub.data.scholarship.LocalScholarshipRepository
import com.example.scholarhub.feature.auth.AuthRole
import com.example.scholarhub.feature.auth.LoginScreen
import com.example.scholarhub.feature.auth.RegisterScreen
import com.example.scholarhub.feature.documents.DocumentsScreen
import com.example.scholarhub.feature.home.HomeScreen
import com.example.scholarhub.feature.mentor.ProfileMentorScreen
import com.example.scholarhub.feature.payment.PaymentScreen
import com.example.scholarhub.feature.profile.ProfileScreen
import com.example.scholarhub.feature.payment.PaymentResultScreen
import com.example.scholarhub.feature.payment.PaymentOrder
import com.example.scholarhub.feature.payment.SessionPaymentResultScreen
import com.example.scholarhub.feature.payment.toPaymentOrder
import com.example.scholarhub.feature.documents.DocumentCatalog
import com.example.scholarhub.feature.documents.DocumentFilter
import com.example.scholarhub.feature.documents.DocumentPaymentScreen
import com.example.scholarhub.feature.documents.DocumentPreviewScreen
import com.example.scholarhub.feature.documents.DocumentStore
import com.example.scholarhub.feature.tracker.TrackerCard
import com.example.scholarhub.feature.tracker.TrackerGuideScreen
import com.example.scholarhub.feature.tracker.TrackerStore
import com.example.scholarhub.feature.tracker.trackableScholarships
import com.example.scholarhub.feature.tracker.trackedStage
import com.example.scholarhub.domain.scholarship.Scholarship
import java.time.LocalDate
import com.example.scholarhub.feature.mentoring.PaymentStatus
import com.example.scholarhub.feature.mentoring.invoiceTime
import com.example.scholarhub.feature.mentoring.paymentStatusAt
import com.example.scholarhub.feature.mentoring.rupiah
import com.example.scholarhub.feature.payment.QrisPaymentScreen
import com.example.scholarhub.feature.mentor.MentorBookedSessionsScreen
import com.example.scholarhub.feature.mentor.MentorHomeScreen
import com.example.scholarhub.feature.mentor.MentorNavTab
import com.example.scholarhub.feature.mentor.MentorScheduleScreen
import com.example.scholarhub.feature.mentor.OpenSlotScreen
import com.example.scholarhub.feature.mentor.SlotOpenedScreen
import com.example.scholarhub.feature.mentoring.BookingDraft
import com.example.scholarhub.feature.mentoring.BookingSessionCard
import com.example.scholarhub.feature.mentoring.MentoringReportScreen
import com.example.scholarhub.feature.mentoring.MentoringScheduleScreen
import com.example.scholarhub.feature.mentoring.MentoringStore
import com.example.scholarhub.feature.mentoring.isUpcoming
import com.example.scholarhub.feature.mentoring.phaseAt
import com.example.scholarhub.feature.mentoring.rememberNow
import com.example.scholarhub.feature.transactions.TransactionHistoryScreen
import com.example.scholarhub.feature.mentoring.MentorDetailScreen
import com.example.scholarhub.feature.mentoring.MentorListScreen
import com.example.scholarhub.feature.notifications.NotificationsScreen
import com.example.scholarhub.feature.scholarship.detail.ScholarshipDetailScreen
import com.example.scholarhub.feature.scholarmatch.ScholarMatchFormRoute
import com.example.scholarhub.feature.scholarmatch.ScholarMatchResultRoute
import com.example.scholarhub.feature.scholarmatch.ScholarMatchResultState
import com.example.scholarhub.feature.scholarmatch.ScholarMatchViewModel
import com.example.scholarhub.feature.scholarship.detail.ScholarshipDetailViewModel
import com.example.scholarhub.feature.scholarship.list.ScholarshipListRoute
import com.example.scholarhub.feature.scholarship.list.ScholarshipListViewModel
import com.example.scholarhub.ui.components.MenteeNavTab
import com.example.scholarhub.feature.notifications.SampleMentorNotificationGroups
import com.example.scholarhub.feature.notifications.SampleNotificationGroups
import com.example.scholarhub.feature.notifications.MentorNotificationCategories
import com.example.scholarhub.feature.notifications.MenteeNotificationCategories
import androidx.compose.ui.platform.LocalUriHandler
import com.example.scholarhub.feature.mentoring.AdminContact
import com.example.scholarhub.feature.profile.ProfileStore
import com.example.scholarhub.feature.profile.SampleMenteeProfile
import com.example.scholarhub.feature.profile.EditProfileScreen
import com.example.scholarhub.feature.profile.AddAchievementScreen
import com.example.scholarhub.feature.profile.ChangePasswordScreen
import com.example.scholarhub.feature.mentor.MentorProfileStore
import com.example.scholarhub.feature.mentor.MentorEditProfileScreen
import com.example.scholarhub.feature.mentor.MentorSessionHistoryScreen
import com.example.scholarhub.feature.mentor.IncomeHistoryScreen
import com.example.scholarhub.feature.mentor.WithdrawalHistoryScreen

sealed interface AppDestination {
    data object Login : AppDestination
    data class Register(val initialRole: AuthRole = AuthRole.Mahasiswa) : AppDestination
    data object Home : AppDestination
    data object MentorList : AppDestination
    data class MentorDetail(val mentorId: String) : AppDestination
    data class Payment(val draft: BookingDraft) : AppDestination
    data class Qris(val bookingId: String, val backTo: AppDestination) : AppDestination
    data class PaymentResult(val bookingId: String, val paid: Boolean, val backTo: AppDestination) : AppDestination
    data class MentoringSchedule(val history: Boolean = false, val backTo: AppDestination = Home) : AppDestination
    data class MentoringReport(val bookingId: String) : AppDestination
    data class Documents(val filter: DocumentFilter = DocumentFilter.All) : AppDestination
    data class DocumentPayment(val documentId: String) : AppDestination
    data class DocumentQris(val purchaseId: String, val backTo: AppDestination) : AppDestination
    data class DocumentResult(val purchaseId: String, val paid: Boolean, val backTo: AppDestination) : AppDestination
    data class DocumentPreview(val documentId: String, val backTo: AppDestination) : AppDestination
    data object MentorHome : AppDestination
    data object MentorSessions : AppDestination
    data class MentorSchedule(val date: java.time.LocalDate? = null) : AppDestination
    data object OpenSlot : AppDestination
    data class SlotOpened(val date: java.time.LocalDate, val starts: List<java.time.LocalDateTime>) : AppDestination
    data class MenteeProfile(val backTo: AppDestination) : AppDestination
    data object TransactionHistory : AppDestination
    data object Profile : AppDestination
    data object EditProfile : AppDestination
    data object AddAchievement : AppDestination
    data class ChangePassword(val backTo: AppDestination = Profile) : AppDestination
    data object MentorEditProfile : AppDestination
    data object MentorSessionHistory : AppDestination
    data object MentorIncomeHistory : AppDestination
    data object MentorWithdrawalHistory : AppDestination
    data class TrackerGuide(val scholarshipId: String, val taskId: String) : AppDestination
    data class Notifications(val backTo: AppDestination, val mentor: Boolean = false) : AppDestination
    data class ScholarshipDetail(
        val scholarshipId: String,
        val backTo: AppDestination = Home,
        /** Hanya diisi jika dibuka dari hasil ScholarMatch. */
        val relevancePercent: Int? = null,
    ) : AppDestination
    data object ScholarMatchForm : AppDestination
    data object ScholarMatchResult : AppDestination
    data object ScholarshipList : AppDestination
    data object ProfileMentor : AppDestination
}

@Composable
fun AppNavGraph(
    repository: LocalScholarshipRepository,
    initialDestination: AppDestination = AppDestination.Login,
    onFinish: () -> Unit = {},
) {
    var currentDestination by remember {
        mutableStateOf(initialDestination)
    }
    // Form, hasil, dan Home berbagi ViewModel ScholarMatch selama sesi (memori saja); dibuat ulang saat kembali ke Login.
    var scholarMatchModel by remember { mutableStateOf(ScholarMatchViewModel(repository)) }
    val openScholarMatch = { currentDestination = AppDestination.ScholarMatchForm }
    // Pesanan dan sesi mentoring di memori untuk satu sesi masuk (mentee dan mentor memakai data yang sama).
    var mentoring by remember { mutableStateOf(MentoringStore()) }
    var documents by remember { mutableStateOf(DocumentStore()) }
    var tracker by remember { mutableStateOf(TrackerStore()) }
    var profileStore by remember { mutableStateOf(ProfileStore()) }
    var mentorProfile by remember { mutableStateOf(MentorProfileStore()) }
    val openTransactions = { currentDestination = AppDestination.TransactionHistory }
    val uriHandler = LocalUriHandler.current
    // Pusat Bantuan: belum ada halaman bantuan di Figma, jadi membuka email admin.
    val contactAdmin: () -> Unit = { runCatching { uriHandler.openUri(AdminContact) } }
    // Notifikasi yang sudah dibaca, per sesi masuk.
    var readNotifications by remember { mutableStateOf(emptySet<String>()) }
    val catalog by produceState(emptyList<Scholarship>(), repository) {
        value = repository.getCatalog().getOrNull()?.scholarships.orEmpty()
    }
    val today = remember { LocalDate.now() }
    // Kartu Tracker di Profil; mode baca untuk mentor yang membuka profil mentee.
    val trackerCard: @Composable (Boolean) -> Unit = { readOnly ->
        val selectedId by tracker.selectedId.collectAsState()
        val done by tracker.done.collectAsState()
        val options = trackableScholarships(catalog, today)
        val chosen = options.firstOrNull { it.id == selectedId } ?: options.firstOrNull()
        TrackerCard(
            options = options,
            tracked = chosen?.trackedStage(today),
            done = done,
            today = today,
            onSelect = tracker::select,
            onToggle = if (readOnly) null else { taskId -> chosen?.let { tracker.toggle(it.id, taskId) } },
            onOpenGuide = { task -> chosen?.let { currentDestination = AppDestination.TrackerGuide(it.id, task.id) } },
        )
    }
    val mentorTab = { tab: MentorNavTab ->
        currentDestination = when (tab) {
            MentorNavTab.Schedule -> AppDestination.MentorSchedule()
            MentorNavTab.Home -> AppDestination.MentorHome
            MentorNavTab.Profile -> AppDestination.ProfileMentor
        }
    }

    when (val dest = currentDestination) {
        AppDestination.Login -> {
            // Profil dan hasil ScholarMatch hanya berlaku untuk satu sesi masuk.
            LaunchedEffect(Unit) {
                scholarMatchModel = ScholarMatchViewModel(repository)
                mentoring = MentoringStore()
                documents = DocumentStore()
                tracker = TrackerStore()
                profileStore = ProfileStore()
                mentorProfile = MentorProfileStore()
                readNotifications = emptySet()
            }
            LoginScreen(
                onLoginSuccess = { _, isMentor ->
                    if (isMentor) {
                        currentDestination = AppDestination.MentorHome
                    } else {
                        currentDestination = AppDestination.Home
                    }
                },
                onNavigateToRegister = { currentDestination = AppDestination.Register() },
            )
        }
        is AppDestination.Register -> {
            RegisterScreen(
                initialRole = dest.initialRole,
                onRegisterSuccess = { role ->
                    if (role == AuthRole.Mentor) {
                        currentDestination = AppDestination.MentorHome
                    } else {
                        currentDestination = AppDestination.Home
                    }
                },
                onNavigateToLogin = {
                    currentDestination = AppDestination.Login
                },
            )
        }
        AppDestination.Home -> {
            val matchForm by scholarMatchModel.formState.collectAsState()
            val matchResult by scholarMatchModel.resultState.collectAsState()
            val menteeProfile by profileStore.profile.collectAsState()
            val bookings by mentoring.bookings.collectAsState()
            val now = rememberNow(mentoring)
            val nearest = bookings.filter { it.phaseAt(now)?.isUpcoming == true }.minByOrNull { it.start }
            HomeScreen(
                onNavigateToDetail = { id ->
                    currentDestination = AppDestination.ScholarshipDetail(id)
                },
                nearestSession = nearest?.let { booking -> { BookingSessionCard(booking, now) } },
                onNavigateToSchedule = { currentDestination = AppDestination.MentoringSchedule() },
                onFindMentor = { currentDestination = AppDestination.MentorList },
                profileComplete = menteeProfile.academicComplete,
                onCompleteProfile = { currentDestination = AppDestination.EditProfile },
                onOpenArticles = openTransactions,
                hasRecommendation = matchResult is ScholarMatchResultState.Ready,
                onOpenScholarMatch = openScholarMatch,
                onOpenRecommendation = { currentDestination = AppDestination.ScholarMatchResult },
                onOpenNotifications = { currentDestination = AppDestination.Notifications(backTo = AppDestination.Home) },
                onTabSelect = { tab -> menteeTabDestination(tab)?.let { currentDestination = it } },
            )
        }
        AppDestination.MentorList -> {
            MentorListScreen(
                store = mentoring,
                onMentorClick = { id -> currentDestination = AppDestination.MentorDetail(id) },
                onSeeAllSchedule = { currentDestination = AppDestination.MentoringSchedule(backTo = AppDestination.MentorList) },
                onOpenNotifications = { currentDestination = AppDestination.Notifications(backTo = AppDestination.MentorList) },
                onOpenArticles = openTransactions,
                onTabSelect = { tab -> menteeTabDestination(tab)?.let { currentDestination = it } },
            )
        }
        is AppDestination.MentorDetail -> {
            val backToList = { currentDestination = AppDestination.MentorList }
            BackHandler(onBack = backToList)
            MentorDetailScreen(
                mentorId = dest.mentorId,
                store = mentoring,
                onBack = backToList,
                onConfirmBooking = { draft -> currentDestination = AppDestination.Payment(draft) },
            )
        }
        is AppDestination.Payment -> {
            val back = { currentDestination = AppDestination.MentorDetail(dest.draft.mentor.id) }
            BackHandler(onBack = back)
            PaymentScreen(
                draft = dest.draft,
                onBack = back,
                onPay = {
                    val d = dest.draft
                    val booking = mentoring.book(d.mentor, d.topic, d.start, d.end, d.price)
                    currentDestination = AppDestination.Qris(booking.id, backTo = AppDestination.MentorDetail(d.mentor.id))
                },
            )
        }
        is AppDestination.Qris -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            val bookings by mentoring.bookings.collectAsState()
            val now = rememberNow(mentoring)
            bookings.firstOrNull { it.id == dest.bookingId }?.let { booking ->
                QrisPaymentScreen(
                    order = booking.toPaymentOrder(),
                    now = now,
                    onBack = back,
                    onHome = { currentDestination = AppDestination.Home },
                    onCheckStatus = {
                        val paid = booking.paymentStatusAt(mentoring.now()) != PaymentStatus.Failed
                        if (paid) mentoring.markPaid(booking.id)
                        currentDestination = AppDestination.PaymentResult(booking.id, paid, AppDestination.Home)
                    },
                )
            }
        }
        is AppDestination.PaymentResult -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            mentoring.find(dest.bookingId)?.let { booking ->
                SessionPaymentResultScreen(
                    booking = booking,
                    paid = dest.paid,
                    onBack = back,
                    onHome = { currentDestination = AppDestination.Home },
                    onSeeSchedule = { currentDestination = AppDestination.MentoringSchedule() },
                )
            }
        }
        is AppDestination.MentoringSchedule -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            MentoringScheduleScreen(
                store = mentoring,
                onBack = back,
                onReport = { id -> currentDestination = AppDestination.MentoringReport(id) },
                initialHistory = dest.history,
            )
        }
        is AppDestination.MentoringReport -> {
            val back = { currentDestination = AppDestination.MentoringSchedule(history = true) }
            BackHandler(onBack = back)
            MentoringReportScreen(store = mentoring, bookingId = dest.bookingId, onBack = back, onSubmitted = back)
        }
        AppDestination.ScholarMatchForm -> {
            val back = { currentDestination = AppDestination.Home }
            BackHandler(onBack = back)
            ScholarMatchFormRoute(
                viewModel = scholarMatchModel,
                onBack = back,
                onSubmitted = {
                    // Jawaban profil di form ScholarMatch ikut mengisi Profil.
                    val input = scholarMatchModel.formState.value.input
                    profileStore.update { p ->
                        p.copy(
                            degree = input.degree ?: p.degree,
                            campus = input.campus ?: p.campus,
                            major = input.major ?: p.major,
                            semester = input.semester ?: p.semester,
                            gpa = scholarMatchModel.formState.value.gpaText.ifBlank { p.gpa },
                        )
                    }
                    currentDestination = AppDestination.ScholarMatchResult
                },
            )
        }
        AppDestination.ScholarMatchResult -> {
            val back = { currentDestination = AppDestination.Home }
            BackHandler(onBack = back)
            ScholarMatchResultRoute(
                viewModel = scholarMatchModel,
                onBack = back,
                onEditAnswers = openScholarMatch,
                onOpenDetail = { id, relevance ->
                    currentDestination = AppDestination.ScholarshipDetail(id, backTo = AppDestination.ScholarMatchResult, relevancePercent = relevance)
                },
            )
        }
        AppDestination.ScholarshipList -> {
            val model = remember { ScholarshipListViewModel(repository) }
            ScholarshipListRoute(
                viewModel = model,
                onOpenDetail = { id ->
                    currentDestination = AppDestination.ScholarshipDetail(id, backTo = AppDestination.ScholarshipList)
                },
                onOpenNotifications = { currentDestination = AppDestination.Notifications(backTo = AppDestination.ScholarshipList) },
                onTabSelect = { tab -> menteeTabDestination(tab)?.let { currentDestination = it } },
                onOpenTransactions = openTransactions,
            )
        }
        is AppDestination.Documents -> {
            DocumentsScreen(
                store = documents,
                onBuy = { doc -> currentDestination = AppDestination.DocumentPayment(doc.id) },
                onPreview = { doc -> currentDestination = AppDestination.DocumentPreview(doc.id, backTo = dest) },
                onOpenNotifications = { currentDestination = AppDestination.Notifications(backTo = dest) },
                onTabSelect = { tab -> menteeTabDestination(tab)?.let { currentDestination = it } },
                initialFilter = dest.filter,
                onOpenTransactions = openTransactions,
            )
        }
        is AppDestination.DocumentPayment -> {
            val back = { currentDestination = AppDestination.Documents() }
            BackHandler(onBack = back)
            DocumentCatalog.firstOrNull { it.id == dest.documentId }?.let { doc ->
                DocumentPaymentScreen(doc = doc, onBack = back, onPay = {
                    val purchase = documents.buy(doc)
                    currentDestination = AppDestination.DocumentQris(purchase.id, backTo = AppDestination.Documents())
                })
            }
        }
        is AppDestination.DocumentQris -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            val purchases by documents.purchases.collectAsState()
            val now = rememberNow(mentoring)
            purchases.firstOrNull { it.id == dest.purchaseId }?.let { purchase ->
                QrisPaymentScreen(
                    order = PaymentOrder(
                        title = purchase.document.title,
                        details = "Dokumen Premium\nPembayaran satu kali · Akses selamanya",
                        invoice = purchase.invoice,
                        price = purchase.document.price ?: 0,
                        payBefore = purchase.payBefore,
                        intro = "Selesaikan pembayaran untuk membuka akses penuh dokumen.",
                        warning = "Periksa Kembali Dokumen Pilihanmu" to
                            "Pastikan dokumen yang dipilih sudah sesuai. Akses akan terbuka setelah pembayaran berhasil.",
                    ),
                    now = now,
                    onBack = back,
                    onHome = { currentDestination = AppDestination.Home },
                    onCheckStatus = {
                        val paid = purchase.statusAt(documents.now()) != PaymentStatus.Failed
                        if (paid) documents.markPaid(purchase.id)
                        currentDestination = AppDestination.DocumentResult(purchase.id, paid, AppDestination.Documents())
                    },
                )
            }
        }
        is AppDestination.DocumentResult -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            documents.find(dest.purchaseId)?.let { purchase ->
                PaymentResultScreen(
                    paid = dest.paid,
                    message = if (dest.paid) {
                        "Dokumen berhasil dibeli! Kamu dapat membuka dan mengunduhnya melalui menu Dokumen, pada tab Sudah Dibeli."
                    } else {
                        "Pembayaran belum berhasil diproses. Akses dokumen belum terbuka. Silakan periksa status transaksi atau laporkan kendala."
                    },
                    invoice = listOf(
                        "Detail Dokumen" to listOf(
                            (if (dest.paid) "Judul Dokumen" else "Dokumen") to purchase.document.title,
                            "Kategori" to "Premium",
                            "Metode Pembayaran" to "QRIS",
                            "Akses" to if (dest.paid) "Selamanya" else "Belum terbuka",
                        ),
                        (if (dest.paid) "Detail Pembayaran" else "Detail Transaksi") to listOf(
                            "Nomor Invoice" to purchase.invoice,
                            "Nominal" to rupiah(purchase.document.price ?: 0),
                            if (dest.paid) "Waktu Pembayaran" to (purchase.paidAt ?: purchase.createdAt).invoiceTime() else "Status" to "Gagal",
                        ),
                    ),
                    onBack = back,
                    primary = "Kembali ke Home" to { currentDestination = AppDestination.Home },
                    secondary = "Lihat Dokumen Saya" to { currentDestination = AppDestination.Documents(DocumentFilter.Owned) },
                )
            }
        }
        is AppDestination.DocumentPreview -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            DocumentCatalog.firstOrNull { it.id == dest.documentId }?.let { DocumentPreviewScreen(doc = it, onBack = back) }
        }
        is AppDestination.Notifications -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            NotificationsScreen(
                groups = if (dest.mentor) SampleMentorNotificationGroups else SampleNotificationGroups,
                categories = if (dest.mentor) MentorNotificationCategories else MenteeNotificationCategories,
                readIds = readNotifications,
                onRead = { readNotifications = it },
                onBack = back,
            )
        }
        AppDestination.Profile -> {
            val back = { currentDestination = AppDestination.Home }
            BackHandler(onBack = back)
            val profile by profileStore.profile.collectAsState()
            ProfileScreen(
                profile = profile,
                onBack = back,
                onEditProfile = { currentDestination = AppDestination.EditProfile },
                onAddAchievement = { currentDestination = AppDestination.AddAchievement },
                onRemoveAchievement = profileStore::removeAchievement,
                onHelp = contactAdmin,
                onChangePassword = { currentDestination = AppDestination.ChangePassword() },
                onLogout = { currentDestination = AppDestination.Login },
                onTabSelect = { tab -> menteeTabDestination(tab)?.let { currentDestination = it } },
                tracker = { trackerCard(false) },
            )
        }
        AppDestination.EditProfile -> {
            val back = { currentDestination = AppDestination.Profile }
            BackHandler(onBack = back)
            val profile by profileStore.profile.collectAsState()
            val matchForm by scholarMatchModel.formState.collectAsState()
            EditProfileScreen(
                profile = profile,
                campusOptions = matchForm.campusOptions.map { it.name },
                majorOptions = matchForm.majorOptions,
                onBack = back,
                onSave = { saved ->
                    profileStore.update { saved }
                    scholarMatchModel.applyProfile(saved.degree, saved.campus, saved.major, saved.semester, saved.gpa)
                    back()
                },
            )
        }
        AppDestination.AddAchievement -> {
            val back = { currentDestination = AppDestination.Profile }
            BackHandler(onBack = back)
            AddAchievementScreen(onBack = back, onSave = { title, level, year, organizer ->
                profileStore.addAchievement(title, level, year, organizer)
                back()
            })
        }
        is AppDestination.ChangePassword -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            ChangePasswordScreen(onBack = back, onSaved = back)
        }
        is AppDestination.TrackerGuide -> {
            val back = { currentDestination = AppDestination.Profile }
            BackHandler(onBack = back)
            val tracked = catalog.firstOrNull { it.id == dest.scholarshipId }?.trackedStage(today)
            val task = tracked?.window?.stage?.tasks?.firstOrNull { it.id == dest.taskId }
            if (tracked != null && task != null) TrackerGuideScreen(tracked, task, onBack = back)
        }
        AppDestination.TransactionHistory -> {
            // Dibuka dari ikon struk di kiri header mentee.
            val back = { currentDestination = AppDestination.Home }
            BackHandler(onBack = back)
            TransactionHistoryScreen(
                store = mentoring,
                documents = documents,
                onBack = back,
                onPay = { id, isDocument ->
                    currentDestination = if (isDocument) {
                        AppDestination.DocumentQris(id, backTo = AppDestination.TransactionHistory)
                    } else {
                        AppDestination.Qris(id, backTo = AppDestination.TransactionHistory)
                    }
                },
                onInvoice = { id, isDocument ->
                    currentDestination = if (isDocument) {
                        AppDestination.DocumentResult(id, documents.find(id)?.payment == PaymentStatus.Paid, AppDestination.TransactionHistory)
                    } else {
                        AppDestination.PaymentResult(id, mentoring.find(id)?.payment == PaymentStatus.Paid, AppDestination.TransactionHistory)
                    }
                },
            )
        }
        is AppDestination.ScholarshipDetail -> {
            val model = remember(dest.scholarshipId) {
                ScholarshipDetailViewModel(dest.scholarshipId, repository)
            }
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            ScholarshipDetailScreen(
                model = model,
                onBack = back,
                scholarMatchRelevancePercent = dest.relevancePercent,
                onFindMentor = { currentDestination = AppDestination.MentorList },
            )
        }
        AppDestination.MentorHome -> {
            MentorHomeScreen(
                store = mentoring,
                onBack = { currentDestination = AppDestination.Login },
                onSeeAll = { currentDestination = AppDestination.MentorSessions },
                onOpenMentee = { currentDestination = AppDestination.MenteeProfile(backTo = AppDestination.MentorHome) },
                onTabSelect = mentorTab,
                onOpenSlot = { currentDestination = AppDestination.OpenSlot },
                onOpenNotifications = { currentDestination = AppDestination.Notifications(backTo = AppDestination.MentorHome, mentor = true) },
            )
        }
        AppDestination.MentorSessions -> {
            val back = { currentDestination = AppDestination.MentorHome }
            BackHandler(onBack = back)
            MentorBookedSessionsScreen(
                store = mentoring,
                onBack = back,
                onOpenMentee = { currentDestination = AppDestination.MenteeProfile(backTo = AppDestination.MentorSessions) },
            )
        }
        is AppDestination.MentorSchedule -> {
            val back = { currentDestination = AppDestination.MentorHome }
            BackHandler(onBack = back)
            MentorScheduleScreen(
                store = mentoring,
                onBack = back,
                onOpenSlot = { currentDestination = AppDestination.OpenSlot },
                onOpenMentee = { currentDestination = AppDestination.MenteeProfile(backTo = dest) },
                onTabSelect = mentorTab,
                initialDate = dest.date,
            )
        }
        AppDestination.OpenSlot -> {
            val back = { currentDestination = AppDestination.MentorSchedule() }
            BackHandler(onBack = back)
            OpenSlotScreen(store = mentoring, onBack = back, onOpened = { date, starts -> currentDestination = AppDestination.SlotOpened(date, starts) })
        }
        is AppDestination.SlotOpened -> {
            val toSchedule = { currentDestination = AppDestination.MentorSchedule(dest.date) }
            BackHandler(onBack = toSchedule)
            SlotOpenedScreen(date = dest.date, starts = dest.starts, onBack = toSchedule, onSeeSlots = toSchedule)
        }
        is AppDestination.MenteeProfile -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            // shortcut: profil mentee masih contoh statis (Rayhan) untuk semua sesi; ganti saat profil tersimpan tersedia.
            ProfileScreen(profile = SampleMenteeProfile, onBack = back, readOnly = true, tracker = { trackerCard(true) })
        }
        AppDestination.ProfileMentor -> {
            ProfileMentorScreen(
                store = mentorProfile,
                mentoring = mentoring,
                onBack = { currentDestination = AppDestination.MentorHome },
                onEdit = { currentDestination = AppDestination.MentorEditProfile },
                onSessionHistory = { currentDestination = AppDestination.MentorSessionHistory },
                onIncomeHistory = { currentDestination = AppDestination.MentorIncomeHistory },
                onWithdrawalHistory = { currentDestination = AppDestination.MentorWithdrawalHistory },
                onHelp = contactAdmin,
                onChangePassword = { currentDestination = AppDestination.ChangePassword(backTo = AppDestination.ProfileMentor) },
                onLogout = { currentDestination = AppDestination.Login },
                onTabSelect = mentorTab,
            )
        }
        AppDestination.MentorEditProfile -> {
            val back = { currentDestination = AppDestination.ProfileMentor }
            BackHandler(onBack = back)
            val profile by mentorProfile.profile.collectAsState()
            val matchForm by scholarMatchModel.formState.collectAsState()
            MentorEditProfileScreen(
                profile = profile,
                campusOptions = matchForm.campusOptions.map { it.name },
                majorOptions = matchForm.majorOptions,
                onBack = back,
                onSave = { saved ->
                    mentorProfile.update { saved }
                    back()
                },
            )
        }
        AppDestination.MentorSessionHistory -> {
            val back = { currentDestination = AppDestination.ProfileMentor }
            BackHandler(onBack = back)
            MentorSessionHistoryScreen(mentoring = mentoring, onBack = back, onContactAdmin = contactAdmin)
        }
        AppDestination.MentorIncomeHistory -> {
            val back = { currentDestination = AppDestination.ProfileMentor }
            BackHandler(onBack = back)
            IncomeHistoryScreen(mentoring = mentoring, onBack = back)
        }
        AppDestination.MentorWithdrawalHistory -> {
            val back = { currentDestination = AppDestination.ProfileMentor }
            BackHandler(onBack = back)
            WithdrawalHistoryScreen(store = mentorProfile, mentoring = mentoring, onBack = back)
        }
    }
}

/**
 * Tujuan tiap tab bottom nav mentee. Tab yang layarnya belum dibuat mengembalikan null.
 * Jangan arahkan tab mentee ke layar role mentor (`feature/mentor/`).
 */
private fun menteeTabDestination(tab: MenteeNavTab): AppDestination? = when (tab) {
    MenteeNavTab.Home -> AppDestination.Home
    MenteeNavTab.Mentor -> AppDestination.MentorList
    MenteeNavTab.Profile -> AppDestination.Profile
    MenteeNavTab.Scholarship -> AppDestination.ScholarshipList
    MenteeNavTab.Documents -> AppDestination.Documents()
}
