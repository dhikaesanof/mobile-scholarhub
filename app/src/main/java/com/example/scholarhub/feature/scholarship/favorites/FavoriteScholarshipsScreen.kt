package com.example.scholarhub.feature.scholarship.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scholarhub.feature.scholarship.components.ScholarshipCard
import com.example.scholarhub.feature.scholarship.components.ScholarshipCardUi
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.TextMuted

/** Daftar beasiswa yang ditandai hati di Detail Beasiswa. Belum ada frame Figma; memakai kartu katalog yang sama. */
@Composable
fun FavoriteScholarshipsScreen(items: List<ScholarshipCardUi>, onBack: () -> Unit, onOpenDetail: (String) -> Unit) {
    Scaffold(containerColor = Base, topBar = { ScholarHubDetailTopBar(title = "Beasiswa Favorit", onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items.forEach { item -> ScholarshipCard(item = item, onDetailClick = { onOpenDetail(item.id) }) }
            if (items.isEmpty()) {
                Text(
                    "Belum ada beasiswa favorit. Ketuk ikon hati di Detail Beasiswa untuk menyimpannya di sini.",
                    Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                )
            }
        }
    }
}
