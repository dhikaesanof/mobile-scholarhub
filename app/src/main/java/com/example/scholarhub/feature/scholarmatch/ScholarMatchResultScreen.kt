package com.example.scholarhub.feature.scholarmatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scholarhub.domain.scholarmatch.MatchStatus
import com.example.scholarhub.domain.scholarmatch.ScholarshipMatch
import com.example.scholarhub.feature.scholarship.components.ScholarshipCard
import com.example.scholarhub.feature.scholarship.list.toCatalogItem
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.ErrorContainer
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.OceanDeep
import com.example.scholarhub.ui.theme.OnWarning
import com.example.scholarhub.ui.theme.PillShape
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.Success
import com.example.scholarhub.ui.theme.SuccessContainer
import com.example.scholarhub.ui.theme.TextStrong
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.WarningContainer
import com.example.scholarhub.ui.theme.nunitoSans
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("id-ID"))
private const val MaxReasons = 3

@Composable
fun ScholarMatchResultRoute(
    viewModel: ScholarMatchViewModel,
    onBack: () -> Unit,
    onOpenDetail: (id: String, relevancePercent: Int?) -> Unit,
) {
    val state by viewModel.resultState.collectAsState()
    ScholarMatchResultScreen(state, onBack, onRetry = viewModel::submit, onOpenDetail = onOpenDetail)
}

/**
 * Hasil ScholarMatch. Tidak ada frame Figma khusus; layar ini memakai kartu beasiswa yang sama dengan Home
 * ("Hasil ScholarMatch") ditambah status aturan, prioritas yang cocok, dan alasan.
 */
@Composable
fun ScholarMatchResultScreen(
    state: ScholarMatchResultState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenDetail: (id: String, relevancePercent: Int?) -> Unit,
) {
    Scaffold(containerColor = Base, topBar = { ScholarHubDetailTopBar(title = "Hasil ScholarMatch", onBack = onBack) }) { padding ->
        when (state) {
            ScholarMatchResultState.Idle, ScholarMatchResultState.Loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Navy)
            }
            ScholarMatchResultState.Error -> Column(
                Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            ) {
                Text("Katalog beasiswa gagal dimuat.", style = MaterialTheme.typography.bodyMedium, color = TextVariant)
                PrimaryButton(text = "Coba Lagi", onClick = onRetry)
            }
            is ScholarMatchResultState.Ready -> Column(
                Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 21.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    "${state.matches.size} beasiswa dengan pendaftaran masih berjalan menurut data per ${state.scheduleAsOf.format(DateFormat)}. " +
                        "Status dihitung dari aturan otomatis; tetap cek ketentuan resmi sebelum mendaftar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextVariant,
                )
                if (state.matches.isEmpty()) {
                    Text("Belum ada beasiswa dengan pendaftaran berjalan di katalog.", style = MaterialTheme.typography.bodyMedium, color = TextVariant)
                }
                val now = Instant.now()
                state.matches.forEach { match ->
                    val card = match.scholarship.toCatalogItem(now).card.copy(
                        relevance = match.relevancePercent?.let { "$it% Relevan" },
                    )
                    ScholarshipCard(
                        item = card,
                        onDetailClick = { onOpenDetail(match.scholarship.id, match.relevancePercent) },
                        extraContent = { MatchSummary(match) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MatchSummary(match: ScholarshipMatch) {
    val (bg, fg) = when (match.status) {
        MatchStatus.Eligible -> SuccessContainer to Success
        MatchStatus.NeedsReview -> WarningContainer to OnWarning
        MatchStatus.NotEligible -> ErrorContainer to ErrorRed
    }
    val track = match.bestTrack
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                match.status.label,
                modifier = Modifier.clip(PillShape).background(bg).padding(horizontal = 10.dp, vertical = 4.dp),
                style = nunitoSans(11f, 16.5f, FontWeight.Bold),
                color = fg,
            )
            track.trackName?.let { Text("Jalur $it", style = MaterialTheme.typography.labelSmall, color = TextVariant) }
        }
        if (match.matchedPriorities.isNotEmpty()) {
            Text("Prioritas yang cocok", style = MaterialTheme.typography.labelSmall, color = TextVariant)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                match.matchedPriorities.forEach {
                    Text(
                        it.label,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Sky).padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = OceanDeep,
                    )
                }
            }
        }
        val notes = track.reasons + track.manualChecks
        if (notes.isNotEmpty()) {
            Text(
                if (track.reasons.isNotEmpty() && match.status == MatchStatus.NotEligible) "Syarat yang tidak terpenuhi" else "Perlu dicek",
                style = MaterialTheme.typography.labelSmall,
                color = TextVariant,
            )
            notes.take(MaxReasons).forEach { note ->
                Row {
                    Text("•", modifier = Modifier.width(12.dp), style = MaterialTheme.typography.labelSmall, color = TextStrong)
                    Text(note, style = MaterialTheme.typography.labelSmall, color = TextStrong)
                }
            }
            if (notes.size > MaxReasons) {
                Text("+${notes.size - MaxReasons} ketentuan lain di detail program", style = MaterialTheme.typography.labelSmall, color = TextVariant)
            }
        }
    }
}

