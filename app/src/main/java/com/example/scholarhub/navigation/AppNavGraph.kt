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
import com.example.scholarhub.feature.profile.ProfileSettingId
import com.example.scholarhub.feature.payment.PaymentUiState
import com.example.scholarhub.feature.payment.SamplePaymentUiState
import com.example.scholarhub.feature.transactions.TransactionHistoryScreen
import com.example.scholarhub.feature.mentoring.MentorDetailScreen
import com.example.scholarhub.feature.mentoring.MentorListScreen
import com.example.scholarhub.feature.notifications.NotificationsScreen
import com.example.scholarhub.feature.scholarship.detail.ScholarshipDetailScreen
import com.example.scholarhub.feature.scholarmatch.ScholarMatchFormRoute
import com.example.scholarhub.feature.scholarmatch.ScholarMatchResultRoute
import com.example.scholarhub.feature.scholarmatch.ScholarMatchViewModel
import com.example.scholarhub.feature.scholarship.detail.ScholarshipDetailViewModel
import com.example.scholarhub.feature.scholarship.list.ScholarshipListRoute
import com.example.scholarhub.feature.scholarship.list.ScholarshipListViewModel
import com.example.scholarhub.ui.components.MenteeNavTab

sealed interface AppDestination {
    data object Login : AppDestination
    data class Register(val initialRole: AuthRole = AuthRole.Mahasiswa) : AppDestination
    data object Home : AppDestination
    data object MentorList : AppDestination
    data class MentorDetail(val mentorId: String) : AppDestination
    data class Payment(val payment: PaymentUiState, val backTo: AppDestination) : AppDestination
    data object TransactionHistory : AppDestination
    data object Profile : AppDestination
    data object Documents : AppDestination
    data class Notifications(val backTo: AppDestination) : AppDestination
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
    // Form dan hasil ScholarMatch berbagi ViewModel selama alurnya aktif; dibuang saat kembali ke Home.
    var scholarMatchModel by remember { mutableStateOf<ScholarMatchViewModel?>(null) }
    val openScholarMatch = {
        if (scholarMatchModel == null) scholarMatchModel = ScholarMatchViewModel(repository)
        currentDestination = AppDestination.ScholarMatchForm
    }

    when (val dest = currentDestination) {
        AppDestination.Login -> {
            LoginScreen(
                onLoginSuccess = { _, isMentor ->
                    if (isMentor) {
                        currentDestination = AppDestination.ProfileMentor
                    } else {
                        currentDestination = AppDestination.Home
                    }
                },
                onNavigateToRegister = { roleStr ->
                    val role = if (roleStr.equals("mentor", ignoreCase = true)) {
                        AuthRole.Mentor
                    } else {
                        AuthRole.Mahasiswa
                    }
                    currentDestination = AppDestination.Register(role)
                },
            )
        }
        is AppDestination.Register -> {
            RegisterScreen(
                initialRole = dest.initialRole,
                onRegisterSuccess = {
                    if (dest.initialRole == AuthRole.Mentor) {
                        currentDestination = AppDestination.ProfileMentor
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
            HomeScreen(
                onNavigateToDetail = { id ->
                    currentDestination = AppDestination.ScholarshipDetail(id)
                },
                onOpenScholarMatch = openScholarMatch,
                onOpenNotifications = { currentDestination = AppDestination.Notifications(backTo = AppDestination.Home) },
                onTabSelect = { tab -> menteeTabDestination(tab)?.let { currentDestination = it } },
            )
        }
        AppDestination.MentorList -> {
            MentorListScreen(
                onMentorClick = { id -> currentDestination = AppDestination.MentorDetail(id) },
                onOpenNotifications = { currentDestination = AppDestination.Notifications(backTo = AppDestination.MentorList) },
                onTabSelect = { tab -> menteeTabDestination(tab)?.let { currentDestination = it } },
            )
        }
        is AppDestination.MentorDetail -> {
            val backToList = { currentDestination = AppDestination.MentorList }
            BackHandler(onBack = backToList)
            // Data detail masih contoh statis; mentorId dipakai setelah repository mentor tersedia.
            MentorDetailScreen(
                onBack = backToList,
                onContinueToPayment = { booking ->
                    // Jadwal, harga, dan mentor diambil dari pilihan booking; judul layanan dan metode masih contoh.
                    currentDestination = AppDestination.Payment(
                        payment = SamplePaymentUiState.copy(
                            serviceTitle = "Sesi Bimbingan",
                            mentorName = booking.mentorName,
                            mentorAffiliation = booking.mentorAffiliation,
                            schedule = booking.fullSchedule,
                            price = booking.price,
                        ),
                        backTo = AppDestination.MentorDetail(booking.mentorId),
                    )
                },
            )
        }
        is AppDestination.Payment -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            PaymentScreen(state = dest.payment, onBack = back)
        }
        AppDestination.ScholarMatchForm -> {
            val back = {
                scholarMatchModel = null
                currentDestination = AppDestination.Home
            }
            BackHandler(onBack = back)
            scholarMatchModel?.let { model ->
                ScholarMatchFormRoute(
                    viewModel = model,
                    onBack = back,
                    onSubmitted = { currentDestination = AppDestination.ScholarMatchResult },
                )
            }
        }
        AppDestination.ScholarMatchResult -> {
            val back = { currentDestination = AppDestination.ScholarMatchForm }
            BackHandler(onBack = back)
            scholarMatchModel?.let { model ->
                ScholarMatchResultRoute(
                    viewModel = model,
                    onBack = back,
                    onOpenDetail = { id, relevance ->
                        currentDestination = AppDestination.ScholarshipDetail(id, backTo = AppDestination.ScholarMatchResult, relevancePercent = relevance)
                    },
                )
            }
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
            )
        }
        AppDestination.Documents -> {
            DocumentsScreen(
                onOpenNotifications = { currentDestination = AppDestination.Notifications(backTo = AppDestination.Documents) },
                onTabSelect = { tab -> menteeTabDestination(tab)?.let { currentDestination = it } },
            )
        }
        is AppDestination.Notifications -> {
            val back = { currentDestination = dest.backTo }
            BackHandler(onBack = back)
            NotificationsScreen(onBack = back)
        }
        AppDestination.Profile -> {
            val back = { currentDestination = AppDestination.Home }
            BackHandler(onBack = back)
            ProfileScreen(
                onBack = back,
                onSettingClick = { id ->
                    if (id == ProfileSettingId.History) currentDestination = AppDestination.TransactionHistory
                },
                onLogout = { currentDestination = AppDestination.Login },
                onTabSelect = { tab -> menteeTabDestination(tab)?.let { currentDestination = it } },
            )
        }
        AppDestination.TransactionHistory -> {
            // Dibuka dari Profil > "Riwayat Bimbingan & Transaksi".
            val back = { currentDestination = AppDestination.Profile }
            BackHandler(onBack = back)
            TransactionHistoryScreen(
                onBack = back,
                // Data tagihan contoh sama dengan contoh Detail Pembayaran di Figma.
                onPay = { currentDestination = AppDestination.Payment(SamplePaymentUiState, AppDestination.TransactionHistory) },
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
                onTabSelect = { tab -> menteeTabDestination(tab)?.let { currentDestination = it } },
            )
        }
        AppDestination.ProfileMentor -> {
            ProfileMentorScreen(
                onBack = {
                    currentDestination = AppDestination.Login
                },
                onLogout = {
                    currentDestination = AppDestination.Login
                },
            )
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
    MenteeNavTab.Premium -> AppDestination.Documents
}
