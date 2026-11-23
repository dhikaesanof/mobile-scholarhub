package com.example.scholarhub.feature.scholarmatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scholarhub.domain.scholarmatch.MatchInput
import com.example.scholarhub.domain.scholarmatch.MatchStatus
import com.example.scholarhub.domain.scholarmatch.ScholarshipMatch
import com.example.scholarhub.domain.scholarship.PreferenceOption
import com.example.scholarhub.feature.scholarmatch.components.FormCard
import com.example.scholarhub.feature.scholarmatch.components.QuestionText
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Sky
import com.example.scholarhub.ui.theme.Steel
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.WarningContainer
import com.example.scholarhub.ui.theme.nunitoSans

private val SummaryGreen = Color(0xFFD7F5E5)
private val SummaryInk = Color(0xFF065F46)
private const val MaxNotes = 3

@Composable
fun ScholarMatchResultRoute(
    viewModel: ScholarMatchViewModel,
    onBack: () -> Unit,
    onEditAnswers: () -> Unit,
    onOpenDetail: (id: String, relevancePercent: Int?) -> Unit,
) {
    val state by viewModel.resultState.collectAsState()
    ScholarMatchResultScreen(state, onBack, onEditAnswers, onRetry = viewModel::submit, onOpenDetail = onOpenDetail)
}

/**
 * Hasil rekomendasi (Figma `134:2154`): prioritas, ringkasan jawaban, rekomendasi teratas, lalu alternatif.
 * Hanya beasiswa yang memenuhi aturan otomatis atau perlu dicek yang tampil.
 */
@Composable
fun ScholarMatchResultScreen(
    state: ScholarMatchResultState,
    onBack: () -> Unit,
    onEditAnswers: () -> Unit,
    onRetry: () -> Unit,
    onOpenDetail: (id: String, relevancePercent: Int?) -> Unit,
) {
    Scaffold(containerColor = Base, topBar = { ScholarHubDetailTopBar(title = "ScholarMatch", onBack = onBack) }) { padding ->
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
            is ScholarMatchResultState.Ready -> {
                val shown = state.matches.filter { it.status != MatchStatus.NotEligible }
                Column(
                    Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("Rekomendasi Untukmu", style = PageTitle, color = Navy)
                        Text("Rekomendasi berdasarkan profil, jawaban, dan prioritas yang kamu pilih.", style = QuestionText, color = Navy)
                    }
                    FormCard(gap = 12) {
                        Text("PRIORITAS KAMU", style = nunitoSans(11f, 16.5f, FontWeight.ExtraBold), color = Navy)
                        Text(state.priorities.joinToString(" · ") { it.label }, style = QuestionText, color = Navy)
                    }
                    Column(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SummaryGreen).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text("Ringkasan jawabanmu", style = MaterialTheme.typography.titleSmall, color = SummaryInk)
                        Text(answerSummary(state.input), style = QuestionText, color = SummaryInk)
                        OutlineButton("Ubah jawaban", onClick = onEditAnswers)
                    }
                    if (shown.isEmpty()) {
                        FormCard {
                            Text(
                                "Belum ada beasiswa yang pendaftarannya masih dibuka dan sesuai jawabanmu. Coba ubah jawaban atau cek lagi nanti.",
                                style = QuestionText,
                                color = Navy,
                            )
                        }
                    } else {
                        Text("Rekomendasi Teratas", style = MaterialTheme.typography.titleMedium, color = Navy)
                        RecommendationCard(shown.first(), state) { onOpenDetail(shown.first().scholarship.id, shown.first().relevancePercent) }
                        if (shown.size > 1) {
                            Text("Alternatif Lainnya", style = MaterialTheme.typography.titleMedium, color = Navy)
                            shown.drop(1).forEach { match ->
                                RecommendationCard(match, state) { onOpenDetail(match.scholarship.id, match.relevancePercent) }
                            }
                        }
                    }
                    Text(
                        "Kecocokan ini berdasarkan data dan jawabanmu. Keputusan penerimaan tetap ditentukan penyelenggara.",
                        style = nunitoSans(11f, 16.5f, FontWeight.Normal),
                        color = Steel,
                    )
                }
            }
        }
    }
}

/** Kartu "ScholarMatch / Recommendation" (Figma `156:2560`). */
@Composable
private fun RecommendationCard(match: ScholarshipMatch, state: ScholarMatchResultState.Ready, onDetail: () -> Unit) {
    val input = state.input
    val unmatched = state.priorities.filter { p -> match.matchedPriorities.none { it.id == p.id } }
    FormCard(gap = 12) {
        Text(match.scholarship.name, style = MaterialTheme.typography.titleMedium, color = Navy)
        Text("${match.score} dari ${state.priorities.size} prioritas cocok", style = MaterialTheme.typography.titleSmall, color = Navy)
        val benefits = listOfNotNull(
            match.matchedPriorities.takeIf { it.isNotEmpty() }?.let { "Cocok: " + it.labels() },
            unmatched.takeIf { it.isNotEmpty() }?.let { "Tidak tersedia: " + it.labels() },
        )
        Text(benefits.joinToString("\n"), style = QuestionText, color = Navy)
        Text("SYARAT DASAR", style = MaterialTheme.typography.labelSmall, color = Steel)
        val academic = listOfNotNull(
            input.degree,
            input.semester?.let { "Semester $it" },
            if (input.noGpa) "Belum punya IPK" else input.gpa?.let { "IPK " + it.toPlainString().replace('.', ',') },
        ).joinToString(" • ")
        val campusLine = if (match.status == MatchStatus.Eligible) "\nKampus dan program studi sesuai" else ""
        Text(academic + campusLine, style = QuestionText, color = Navy)
        StatusBox(match, input)
        OutlineButton("Lihat detail beasiswa", onClick = onDetail)
    }
}

/** Kotak status: sesuai (#D9EEFB) atau perlu dicek beserta ketentuannya. */
@Composable
private fun StatusBox(match: ScholarshipMatch, input: MatchInput) {
    val eligible = match.status == MatchStatus.Eligible
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(if (eligible) Sky else WarningContainer).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            if (eligible) "Syarat dan Ketentuan Telah Sesuai" else "Perlu Dicek",
            style = MaterialTheme.typography.titleSmall,
            color = Navy,
        )
        if (eligible) {
            Text(answerSummary(input, includeOrganization = false), style = QuestionText, color = Navy)
        } else {
            val notes = match.bestTrack.reasons + match.bestTrack.manualChecks
            Column {
                notes.take(MaxNotes).forEach { note ->
                    Row {
                        Text("•", Modifier.width(14.dp), style = QuestionText, color = Navy)
                        Text(note, style = QuestionText, color = Navy)
                    }
                }
                if (notes.size > MaxNotes) {
                    Text("+${notes.size - MaxNotes} ketentuan lain di detail beasiswa", style = QuestionText, color = Navy)
                }
            }
        }
    }
}

private fun List<PreferenceOption>.labels() = joinToString(" · ") { it.label }

/** Kalimat ringkas dari jawaban kondisi, mis. "Tidak menerima beasiswa lain. Bersedia mengikuti pembinaan dan laporan." */
internal fun answerSummary(input: MatchInput, includeOrganization: Boolean = true): String = listOfNotNull(
    when (input.receivingScholarship) {
        false -> "Tidak menerima beasiswa lain."
        true -> "Sedang menerima beasiswa lain."
        null -> null
    },
    when (input.commitment) {
        true -> "Bersedia mengikuti pembinaan dan laporan."
        false -> "Belum bersedia mengikuti kegiatan wajib dan laporan."
        null -> null
    },
    if (includeOrganization) {
        when (input.organization) {
            true -> "Pernah aktif di organisasi atau kepanitiaan."
            false -> "Belum pernah aktif di organisasi atau kepanitiaan."
            null -> null
        }
    } else {
        null
    },
).joinToString(" ")
