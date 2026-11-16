package com.example.scholarhub.navigation

import androidx.compose.runtime.*
import com.example.scholarhub.data.scholarship.LocalScholarshipRepository
import com.example.scholarhub.feature.auth.AuthRole
import com.example.scholarhub.feature.auth.LoginScreen
import com.example.scholarhub.feature.auth.RegisterScreen
import com.example.scholarhub.feature.home.HomeScreen
import com.example.scholarhub.feature.mentor.ProfileMentorScreen
import com.example.scholarhub.feature.scholarship.detail.ScholarshipDetailScreen
import com.example.scholarhub.feature.scholarship.detail.ScholarshipDetailViewModel

sealed interface AppDestination {
    data object Login : AppDestination
    data class Register(val initialRole: AuthRole = AuthRole.Mahasiswa) : AppDestination
    data object Home : AppDestination
    data class ScholarshipDetail(val scholarshipId: String) : AppDestination
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
                // Tab Mentor/Profil mentee punya halaman sendiri (belum dibuat),
                // jangan diarahkan ke ProfileMentorScreen milik role mentor.
                onTabSelect = { },
            )
        }
        is AppDestination.ScholarshipDetail -> {
            val model = remember(dest.scholarshipId) {
                ScholarshipDetailViewModel(dest.scholarshipId, repository)
            }
            ScholarshipDetailScreen(
                model = model,
                onBack = {
                    currentDestination = AppDestination.Home
                },
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
