package com.example.scholarhub.feature.scholarmatch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scholarhub.R
import com.example.scholarhub.domain.scholarmatch.AchievementAnswer
import com.example.scholarhub.domain.scholarmatch.CampusTypeAnswer
import com.example.scholarhub.domain.scholarmatch.DegreeAnswer
import com.example.scholarhub.domain.scholarmatch.EnglishTestAnswer
import com.example.scholarhub.domain.scholarmatch.IncomeAnswer
import com.example.scholarhub.domain.scholarmatch.MatchInput
import com.example.scholarhub.domain.scholarmatch.OrganizationAnswer
import com.example.scholarhub.domain.scholarmatch.SemesterAnswer
import com.example.scholarhub.domain.scholarship.PreferenceOption
import com.example.scholarhub.feature.scholarmatch.components.Caption
import com.example.scholarhub.feature.scholarmatch.components.CheckboxCard
import com.example.scholarhub.feature.scholarmatch.components.FormSectionCard
import com.example.scholarhub.feature.scholarmatch.components.FormTextField
import com.example.scholarhub.feature.scholarmatch.components.OptionButton
import com.example.scholarhub.feature.scholarmatch.components.Question
import com.example.scholarhub.feature.scholarmatch.components.RadioRow
import com.example.scholarhub.feature.scholarmatch.components.Small
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Outline
import com.example.scholarhub.ui.theme.ScholarHubTheme
import com.example.scholarhub.ui.theme.ShadowMedium
import com.example.scholarhub.ui.theme.SkyFaint
import com.example.scholarhub.ui.theme.TextVariant
import com.example.scholarhub.ui.theme.nunitoSans

@Composable
fun ScholarMatchFormRoute(viewModel: ScholarMatchViewModel, onBack: () -> Unit, onSubmitted: () -> Unit) {
    val state by viewModel.formState.collectAsState()
    ScholarMatchFormScreen(
        state = state,
        onBack = onBack,
        onUpdate = viewModel::update,
        onGpaChange = viewModel::setGpaText,
        onTogglePriority = viewModel::togglePriority,
        onSubmit = {
            viewModel.submit()
            onSubmitted()
        },
    )
}

/** Form ScholarMatch (Figma 846:3881). Bagian A–D, 13 pertanyaan, lalu tombol simpan dan cari. */
@Composable
fun ScholarMatchFormScreen(
    state: ScholarMatchFormState,
    onBack: () -> Unit,
    onUpdate: ((MatchInput) -> MatchInput) -> Unit,
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
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 14.346.dp, end = 14.346.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(21.519.dp),
        ) {
            FormSectionCard("A", "Informasi Akademik", "Ceritakan sedikit tentang kuliahmu supaya kami bisa mencocokkan syarat beasiswa.") {
                Question(1, "Apa jenjang pendidikanmu saat ini?", first = true) {
                    OptionGrid(
                        options = listOf(
                            DegreeAnswer.Vocational to "D3 / D4 (Vokasi)",
                            DegreeAnswer.Bachelor to "S1 (Sarjana)",
                            DegreeAnswer.Master to "S2 (Magister)",
                            DegreeAnswer.Doctoral to "S3 (Doktor)",
                        ),
                        selected = input.degree,
                        onSelect = { v -> onUpdate { it.copy(degree = v) } },
                    )
                }
                Question(2, "Kamu sedang berada di semester berapa?") {
                    OptionRow(
                        options = listOf(
                            SemesterAnswer.Sem1To2 to "Sem 1-2",
                            SemesterAnswer.Sem3To4 to "Sem 3-4",
                            SemesterAnswer.Sem5To6 to "Sem 5-6",
                            SemesterAnswer.Sem7Plus to "Sem 7-8+",
                        ),
                        selected = input.semester,
                        gap = 5.38,
                        onSelect = { v -> onUpdate { it.copy(semester = v) } },
                    )
                }
                Question(3, "Berapa IPK kamu saat ini?") {
                    // Teks disimpan lokal dan diteruskan ke ViewModel; memakai StateFlow langsung membuat ketikan cepat hilang.
                    var gpa by rememberSaveable { mutableStateOf(state.gpaText) }
                    FormTextField(
                        value = gpa,
                        onValueChange = { v ->
                            gpa = v.filter { it.isDigit() || it == '.' || it == ',' }.take(4)
                            onGpaChange(gpa)
                        },
                        placeholder = "Contoh: 3.45",
                        suffix = "/ 4.00 Skala",
                        keyboardType = KeyboardType.Decimal,
                        textStyle = nunitoSans(12.553f, 16f, FontWeight.Bold),
                        error = state.gpaError,
                    )
                }
                Question(4, "Nama universitas atau perguruan tinggi") {
                    var campus by rememberSaveable { mutableStateOf(input.campusName.orEmpty()) }
                    FormTextField(
                        value = campus,
                        onValueChange = { v -> campus = v; onUpdate { it.copy(campusName = v) } },
                        placeholder = "Contoh: Universitas Indonesia (UI)",
                        leadingIcon = R.drawable.ic_match_campus,
                    )
                }
                Question(5, "Jurusan atau program studi kamu") {
                    var major by rememberSaveable { mutableStateOf(input.major.orEmpty()) }
                    FormTextField(
                        value = major,
                        onValueChange = { v -> major = v; onUpdate { it.copy(major = v) } },
                        placeholder = "Contoh: Teknik Informatika",
                        leadingIcon = R.drawable.ic_match_major,
                        leadingWidth = 18.dp,
                        leadingHeight = 14.dp,
                    )
                }
                Question(6, "Tipe perguruan tinggi tempatmu kuliah") {
                    OptionRow(
                        options = listOf(
                            CampusTypeAnswer.PTN to "PTN (Negeri)",
                            CampusTypeAnswer.PTS to "PTS (Swasta)",
                            CampusTypeAnswer.PTK to "PTK (Kedinasan)",
                        ),
                        selected = input.campusType,
                        onSelect = { v -> onUpdate { it.copy(campusType = v) } },
                    )
                }
            }

            FormSectionCard("B", "Kondisi Ekonomi", "Membantu menemukan beasiswa yang relevan berdasarkan persyaratan ekonomi.") {
                Question(7, "Berapa penghasilan orang tua per bulan?", first = true) {
                    Column(verticalArrangement = Arrangement.spacedBy(7.173.dp)) {
                        listOf(
                            IncomeAnswer.Below3M to "< Rp 3.000.000 (Target KIP-K / Afirmasi)",
                            IncomeAnswer.From3MTo7_5M to "Rp 3.000.000 - Rp 7.500.000 / bln",
                            IncomeAnswer.From7_5MTo15M to "Rp 7.500.000 - Rp 15.000.000 / bln",
                            IncomeAnswer.Above15M to "> Rp 15.000.000 / bln",
                        ).forEach { (value, label) ->
                            RadioRow(label, selected = input.parentIncome == value, onClick = { onUpdate { it.copy(parentIncome = value) } })
                        }
                    }
                }
                Question(8, "Apakah kamu sedang menerima beasiswa aktif saat ini?") {
                    OptionGrid(
                        options = listOf(false to "Tidak Ada (Belum Menerima)", true to "Ya, Beasiswa Lain"),
                        selected = input.receivingScholarship,
                        onSelect = { v -> onUpdate { it.copy(receivingScholarship = v) } },
                    )
                }
            }

            FormSectionCard("C", "Kompetensi & Pengalaman", "Digunakan jika beasiswa mempertimbangkan kemampuan atau pengalaman.") {
                Question(9, "Berapa skor TOEFL / IELTS kamu, jika ada?", first = true) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        EnglishTestDropdown(input.englishTest) { v -> onUpdate { it.copy(englishTest = v, englishScore = if (v == EnglishTestAnswer.None) null else it.englishScore) } }
                        if (input.englishTest != null && input.englishTest != EnglishTestAnswer.None) {
                            var score by rememberSaveable { mutableStateOf(input.englishScore.orEmpty()) }
                            FormTextField(
                                value = score,
                                onValueChange = { v -> score = v.take(5); onUpdate { it.copy(englishScore = score) } },
                                placeholder = "Skor",
                                suffix = "Skor",
                                keyboardType = KeyboardType.Decimal,
                            )
                        }
                    }
                }
                Question(10, "Tingkat prestasi akademik / non-akademikmu") {
                    OptionGrid(
                        options = listOf(
                            AchievementAnswer.International to "Tingkat Internasional",
                            AchievementAnswer.National to "Tingkat Nasional (Aktif)",
                            AchievementAnswer.Regional to "Tingkat Provinsi / Kota",
                            AchievementAnswer.None to "Belum Ada Prestasi Resmi",
                        ),
                        selected = input.achievement,
                        onSelect = { v -> onUpdate { it.copy(achievement = v) } },
                    )
                }
                Question(11, "Tingkat keaktifanmu dalam organisasi?") {
                    OptionGrid(
                        options = listOf(
                            OrganizationAnswer.CoreBoard to "Ketua / Pengurus Inti",
                            OrganizationAnswer.Coordinator to "Koordinator / Staf Divisi",
                            OrganizationAnswer.ActiveMember to "Anggota Aktif",
                            OrganizationAnswer.Inactive to "Tidak Aktif Organisasi",
                        ),
                        selected = input.organization,
                        onSelect = { v -> onUpdate { it.copy(organization = v) } },
                    )
                }
                Question(12, "Berasal dari daerah tertinggal, terdepan, & terluar (3T)?") {
                    OptionGrid(
                        options = listOf(false to "Bukan Daerah 3T (Reguler)", true to "Ya, Daerah 3T (Afirmasi)"),
                        selected = input.from3TRegion,
                        onSelect = { v -> onUpdate { it.copy(from3TRegion = v) } },
                    )
                }
            }

            FormSectionCard("D", "Preferensi Beasiswa", "Digunakan untuk menentukan beasiswa yang paling sesuai kebutuhanmu.") {
                Question(13, "Apa manfaat beasiswa yang paling kamu prioritaskan?", first = true) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.966.dp)) {
                        Text(
                            "Pilih satu atau lebih manfaat yang kamu cari (maksimal ${MatchInput.MAX_PRIORITIES}):",
                            style = Caption,
                            color = TextVariant,
                        )
                        PriorityOptions(state.preferenceOptions, input.priorities, onTogglePriority)
                        if (state.optionsError) Text("Pilihan prioritas gagal dimuat.", style = Caption, color = TextVariant)
                    }
                }
            }

            Column {
                PrimaryButton(
                    text = "Simpan dan Temukan Beasiswa yang Relevan",
                    onClick = onSubmit,
                    modifier = Modifier.padding(vertical = 14.233.dp).fillMaxWidth().height(41.534.dp)
                        .shadow(4.dp, RoundedCornerShape(10.384.dp), ambientColor = ShadowMedium, spotColor = ShadowMedium),
                    solidShadow = null,
                    contentPadding = PaddingValues(horizontal = 17.306.dp),
                    textStyle = nunitoSans(10.384f, 13.845f, FontWeight.Bold, tracking = 0.2596f),
                    iconGap = 6.914.dp,
                    trailingContent = {
                        Image(painterResource(R.drawable.ic_match_sparkle), null, Modifier.size(16.2.dp, 16.181.dp))
                    },
                )
                // Figma menulis "Data terenkripsi…"; aplikasi hanya menyimpan jawaban sementara di memori, jadi teks disesuaikan.
                Row(
                    Modifier.fillMaxWidth().padding(top = 7.dp, bottom = 14.346.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.38.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(painterResource(R.drawable.ic_match_lock), null, Modifier.size(10.4.dp, 12.8.dp))
                    Text("Jawaban hanya dipakai sementara untuk pencocokan beasiswa.", style = Caption, color = TextVariant)
                }
            }
        }
    }
}

@Composable
private fun <T> OptionGrid(options: List<Pair<T, String>>, selected: T?, onSelect: (T) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.173.dp)) {
        options.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(7.173.dp)) {
                row.forEach { (value, label) ->
                    OptionButton(label, selected = selected == value, onClick = { onSelect(value) }, modifier = Modifier.weight(1f))
                }
                if (row.size == 1) Box(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun <T> OptionRow(options: List<Pair<T, String>>, selected: T?, onSelect: (T) -> Unit, gap: Double = 7.173) {
    Row(horizontalArrangement = Arrangement.spacedBy(gap.dp)) {
        options.forEach { (value, label) ->
            OptionButton(label, selected = selected == value, onClick = { onSelect(value) }, modifier = Modifier.weight(1f), minHeight = 32.93.dp)
        }
    }
}

@Composable
private fun PriorityOptions(options: List<PreferenceOption>, selected: Set<String>, onToggle: (String) -> Unit) {
    val full = selected.size >= MatchInput.MAX_PRIORITIES
    Column(verticalArrangement = Arrangement.spacedBy(8.966.dp)) {
        // Pilihan diambil dari `preferenceOptions` katalog; data belum punya deskripsi, jadi hanya label yang tampil.
        options.forEach { option ->
            CheckboxCard(
                title = option.label,
                checked = option.id in selected,
                enabled = !full,
                onToggle = { onToggle(option.id) },
            )
        }
    }
}

private val EnglishTestLabels = linkedMapOf(
    EnglishTestAnswer.None to "Belum Ada Tes",
    EnglishTestAnswer.ToeflItp to "TOEFL ITP",
    EnglishTestAnswer.ToeflIbt to "TOEFL iBT",
    EnglishTestAnswer.Ielts to "IELTS",
)

@Composable
private fun EnglishTestDropdown(selected: EnglishTestAnswer?, onSelect: (EnglishTestAnswer) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(10.759.dp)
    Box {
        Row(
            Modifier.fillMaxWidth().height(39.dp).clip(shape).background(SkyFaint).border(0.897.dp, Outline, shape)
                .clickable { open = true }.padding(horizontal = 15.25.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(EnglishTestLabels[selected ?: EnglishTestAnswer.None].orEmpty(), modifier = Modifier.weight(1f), style = nunitoSans(12f, 14.346f, FontWeight.Medium), color = TextVariant)
            Image(painterResource(R.drawable.ic_profile_chevron), null, Modifier.size(6.167.dp, 10.dp).rotate(90f))
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            EnglishTestLabels.forEach { (value, label) ->
                DropdownMenuItem(text = { Text(label, style = Small, color = Navy) }, onClick = { onSelect(value); open = false })
            }
        }
    }
}

@Preview(widthDp = 402, heightDp = 2527)
@Composable
private fun ScholarMatchFormScreenPreview() {
    ScholarHubTheme {
        ScholarMatchFormScreen(
            state = ScholarMatchFormState(
                input = MatchInput(degree = DegreeAnswer.Bachelor, semester = SemesterAnswer.Sem5To6, campusType = CampusTypeAnswer.PTN),
                gpaText = "3.84",
                preferenceOptions = listOf(PreferenceOption("tuition_support", "Bantuan UKT"), PreferenceOption("monthly_funding", "Dana bulanan")),
            ),
            onBack = {}, onUpdate = {}, onGpaChange = {}, onTogglePriority = {}, onSubmit = {},
        )
    }
}
