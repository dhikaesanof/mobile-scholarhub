package com.example.scholarhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.scholarhub.data.scholarship.LocalScholarshipRepository
import com.example.scholarhub.navigation.AppDestination
import com.example.scholarhub.navigation.AppNavGraph
import com.example.scholarhub.ui.theme.ScholarHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(0xFFF1F1F1.toInt(), 0xFFF1F1F1.toInt()))
        val repository = LocalScholarshipRepository(assets)
        // Extra sementara untuk membuka layar tertentu saat pengembangan:
        // `scholarshipId` membuka detail beasiswa, `startScreen=transactions` membuka Riwayat Transaksi
        // (pintu masuknya dari Profil belum dibuat).
        val initialDestination = when {
            intent.hasExtra("scholarshipId") ->
                AppDestination.ScholarshipDetail(intent.getStringExtra("scholarshipId") ?: "SH28")
            intent.getStringExtra("startScreen") == "transactions" -> AppDestination.TransactionHistory
            else -> AppDestination.Login
        }
        setContent {
            ScholarHubTheme {
                AppNavGraph(
                    repository = repository,
                    initialDestination = initialDestination,
                    onFinish = ::finish,
                )
            }
        }
    }
}
