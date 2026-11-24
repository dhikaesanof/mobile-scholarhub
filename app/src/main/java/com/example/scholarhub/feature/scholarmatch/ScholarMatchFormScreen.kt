package com.example.scholarhub.feature.scholarmatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.domain.scholarmatch.MatchInput
import com.example.scholarhub.domain.scholarship.CampusOption
import com.example.scholarhub.domain.scholarship.PreferenceOption
import com.example.scholarhub.feature.scholarmatch.components.ChoiceButton
import com.example.scholarhub.feature.scholarmatch.components.CheckRow
import com.example.scholarhub.feature.scholarmatch.components.FormCard
import com.example.scholarhub.feature.scholarmatch.components.FormDropdown
import com.example.scholarhub.feature.scholarmatch.components.FormTextField
import com.example.scholarhub.feature.scholarmatch.components.MainButton
import com.example.scholarhub.feature.scholarmatch.components.Question
import com.example.scholarhub.feature.scholarmatch.components.QuestionText
import com.example.scholarhub.feature.scholarmatch.components.YesNo
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.components.WarningNotice
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.ErrorRed
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Ocean
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.nunitoSans

/** Judul halaman Bold 23 / 1.67 (Figma "Profil & Preferensi", "Prioritas beasiswa"). */
internal val PageTitle = nunitoSans(23f, 38.4f, FontWeight.Bold)

private val Semesters = (1..14).map(Int::toString)

@Composable
fun ScholarMatchFormRoute(viewModel: ScholarMatchViewModel, onBack: () -> Unit, onSubmitted: () -> Unit) {
    val state by viewModel.formState.collectAsState()
    ScholarMatchFormScreen(
        state = state,
        onBack = onBack,
        onUpdate = viewModel::update,
        onSelectCampus = viewModel::selectCampus,
        onGpaChange = viewModel::setGpaText,
        onTogglePriority = viewModel::togglePriority,
        onSubmit = {
            viewModel.submit()
            onSubmitted()
        },
    )
}

/**
 * Form ScholarMatch: satu pengisian berisi profil, kondisi, dan prioritas (Figma `134:1910`).
 * Saat data profil wajib belum lengkap tampil pemberitahuan merah dan tombol nonaktif (Figma `172:2214`).
 */
@Composable
fun ScholarMatchFormScreen(
    state: ScholarMatchFormState,
    onBack: () -> Unit,
    onUpdate: ((MatchInput) -> MatchInput) -> Unit,
    onSelectCampus: (String) -> Unit,
    onGpaChange: (String) -> Unit,
    onTogglePriority: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val input = state.input
    Scaffold(
        containerColor = Base,
        topBar = { ScholarHubDetailTopBar(title = "ScholarMatch", onBack = onBack) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("Profil & Preferensi", style = PageTitle, color = Navy)
                Text("Periksa data profil, jawab kondisi saat ini, lalu pilih manfaat yang kamu cari.", style = QuestionText, color = Navy)
            }

            // Figma `172:2348`: hanya saat data profil wajib kosong.
            if (!input.profileComplete) {
                WarningNotice("Lengkapi informasi profilmu", "Lengkapi kolom di bawah sebelum melihat rekomendasi.")
            }

            FormCard {
                Question("Apakah kamu sedang aktif kuliah?") {
                    YesNo(input.activeStudent) { v -> onUpdate { it.copy(activeStudent = v) } }
                }
                Question("Apa jenjang kuliahmu?") {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        MatchInput.Degrees.forEach { degree ->
                            ChoiceButton(degree, input.degree == degree, { onUpdate { it.copy(degree = degree) } }, Modifier.weight(1f))
                        }
                    }
                }
                Question("Di mana kamu kuliah?") {
                    FormDropdown(
                        value = input.campus,
                        placeholder = "Pilih kampus",
                        options = state.campusOptions.map { it.name } + OtherCampus,
                        onSelect = onSelectCampus,
                    )
                }
                Question("Apa program studimu?") {
                    FormDropdown(
                        value = input.major,
                        placeholder = "Pilih program studi",
                        options = state.majorOptions + OtherMajor,
                        onSelect = { v -> onUpdate { it.copy(major = v) } },
                        showChevron = false,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Question("Semester berapa?", Modifier.weight(1f)) {
                        FormDropdown(
                            value = input.semester?.toString(),
                            placeholder = "Pilih semester",
                            options = Semesters,
                            onSelect = { v -> onUpdate { it.copy(semester = v.toInt()) } },
                            showChevron = false,
                        )
                    }
                    Question(if (state.profileSaved) "IPK dari profil" else "IPK", Modifier.weight(1f)) {
                        // Teks disimpan lokal dan diteruskan ke ViewModel; memakai StateFlow langsung membuat ketikan cepat hilang.
                        var gpa by rememberSaveable { mutableStateOf(state.gpaText) }
                        FormTextField(
                            value = gpa,
                            onValueChange = { v ->
                                gpa = v.filter { it.isDigit() || it == '.' || it == ',' }.take(4)
                                onGpaChange(gpa)
                            },
                            placeholder = "Belum diisi",
                            keyboardType = KeyboardType.Decimal,
                            enabled = !input.noGpa,
                            error = state.gpaError,
                        )
                    }
                }
                CheckRow("Belum punya IPK", checked = input.noGpa, onToggle = { onUpdate { it.copy(noGpa = !it.noGpa) } })
            }

            FormCard {
                Text("Pengalaman & kondisi saat ini", style = MaterialTheme.typography.titleMedium, color = Navy)
                Question("Pernah menjadi anggota organisasi atau panitia?") {
                    YesNo(input.organization) { v -> onUpdate { it.copy(organization = v) } }
                }
                Question("Apakah kamu sedang menerima beasiswa lain?") {
                    YesNo(input.receivingScholarship) { v -> onUpdate { it.copy(receivingScholarship = v) } }
                }
                Question("Bersedia ikut kegiatan wajib dan mengirim laporan selama menerima beasiswa?") {
                    YesNo(input.commitment) { v -> onUpdate { it.copy(commitment = v) } }
                }
            }

            Text("Prioritas beasiswa", style = PageTitle, color = Navy)
            Text(
                "Apa yang paling kamu cari dari beasiswa? Pilih minimal ${MatchInput.MIN_PRIORITIES} dan maksimal " +
                    "${MatchInput.MAX_PRIORITIES} prioritas yang paling kamu butuhkan.",
                style = QuestionText,
                color = Navy,
            )
            FormCard(gap = 8, padding = 12) {
                Text(
                    "${input.priorities.size} dari ${MatchInput.MAX_PRIORITIES} dipilih",
                    style = MaterialTheme.typography.labelLarge,
                    color = Ocean,
                )
                val full = input.priorities.size >= MatchInput.MAX_PRIORITIES
                state.preferenceOptions.groupBy { it.group }.forEach { (group, options) ->
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        group?.let { Text(it, style = MaterialTheme.typography.titleSmall, color = Navy) }
                        options.forEach { option ->
                            CheckRow(
                                label = option.label,
                                checked = option.id in input.priorities,
                                enabled = !full,
                                onToggle = { onTogglePriority(option.id) },
                            )
                        }
                    }
                }
                if (state.optionsError) Text("Pilihan gagal dimuat.", style = QuestionText, color = ErrorRed)
            }

            FormCard(padding = 12) {
                MainButton("Lihat rekomendasi", enabled = input.complete && (state.gpaError == null || input.noGpa), onClick = onSubmit)
            }
        }
    }
}

@Preview(widthDp = 402, heightDp = 2028)
@Composable
private fun ScholarMatchFormScreenPreview() {
    ScholarHubTheme {
        ScholarMatchFormScreen(
            state = ScholarMatchFormState(
                input = MatchInput(activeStudent = true, degree = "S1", priorities = setOf("tuition_support")),
                preferenceOptions = listOf(
                    PreferenceOption("tuition_support", "Bantuan UKT / SPP", "Bantuan dana"),
                    PreferenceOption("mentoring", "Mentoring", "Pengembangan diri"),
                ),
                campusOptions = listOf(CampusOption("Universitas Gadjah Mada", "PTN")),
            ),
            onBack = {}, onUpdate = {}, onSelectCampus = {}, onGpaChange = {}, onTogglePriority = {}, onSubmit = {},
        )
    }
}
