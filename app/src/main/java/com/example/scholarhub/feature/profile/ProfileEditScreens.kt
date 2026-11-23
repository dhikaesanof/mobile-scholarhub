package com.example.scholarhub.feature.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.scholarhub.ui.components.FormPickerField
import com.example.scholarhub.ui.components.FormSectionCard
import com.example.scholarhub.ui.components.FormTextField
import com.example.scholarhub.ui.components.OutlineButton
import com.example.scholarhub.ui.components.PhotoCard
import com.example.scholarhub.ui.components.PrimaryButton
import com.example.scholarhub.ui.components.ScholarHubDetailTopBar
import com.example.scholarhub.ui.theme.Base
import com.example.scholarhub.ui.theme.Navy
import com.example.scholarhub.ui.theme.Steel
import com.example.scholarhub.ui.theme.nunitoSans
import java.time.Year

private val Caption = nunitoSans(11f, 16.5f, FontWeight.Normal)

/** Kerangka layar form profil: header, pengantar SemiBold 13, kartu-kartu berjarak 20. */
@Composable
internal fun FormScaffold(title: String, intro: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(containerColor = Base, topBar = { ScholarHubDetailTopBar(title = title, onBack = onBack) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(intro, style = MaterialTheme.typography.bodyMedium, color = Navy)
            content()
        }
    }
}

/** Kartu aksi di akhir form (Figma `Simpan perubahan`, `Tambahkan ke profil`). */
@Composable
internal fun FormActionCard(title: String, note: String, primary: String, enabled: Boolean, onPrimary: () -> Unit, onCancel: (() -> Unit)? = null) {
    FormSectionCard(title) {
        Text(note, style = Caption, color = Steel)
        PrimaryButton(text = primary, onClick = onPrimary, enabled = enabled, modifier = Modifier.fillMaxWidth().height(48.dp), solidShadow = null)
        onCancel?.let { OutlineButton("Batal", onClick = it) }
    }
}

/**
 * Edit Profil mentee (Figma `213:3162`): foto, identitas, informasi akademik, kemampuan bahasa (opsional).
 * Kampus dan prodi dipilih dari daftar katalog agar cocok dengan ScholarMatch.
 */
@Composable
fun EditProfileScreen(
    profile: MenteeProfile,
    campusOptions: List<String>,
    majorOptions: List<String>,
    onBack: () -> Unit,
    onSave: (MenteeProfile) -> Unit,
) {
    var draft by remember { mutableStateOf(profile) }
    var semesterText by remember { mutableStateOf(profile.semester?.toString().orEmpty()) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> uri?.let { draft = draft.copy(photo = it) } }
    val semester = semesterText.toIntOrNull()
    val semesterError = if (semesterText.isNotBlank() && (semester == null || semester !in 1..14)) "Semester berada di antara 1 dan 14." else null
    val gpaError = gpaProblem(draft.gpa)
    val ieltsError = draft.ieltsScore.takeIf { it.isNotBlank() }?.replace(',', '.')?.toDoubleOrNull()
        .let { if (draft.ieltsScore.isNotBlank() && (it == null || it !in 0.0..9.0)) "Skor IELTS berada di antara 0 dan 9." else null }
    val valid = draft.name.isNotBlank() && semesterError == null && gpaError == null && ieltsError == null

    FormScaffold("Edit Profil", "Perbarui informasi dirimu. Data ini membantu ScholarMatch mencocokkan syarat beasiswa.", onBack) {
        PhotoCard(draft.photo, null) { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        FormSectionCard("Identitas diri") {
            FormTextField("Nama Lengkap", draft.name, { draft = draft.copy(name = it) }, "Nama lengkap", "Gunakan nama yang ingin ditampilkan di profil.",
                error = if (draft.name.isBlank()) "Nama wajib diisi." else null)
        }
        FormSectionCard("Informasi Akademik") {
            FormPickerField("Jenjang Pendidikan", draft.degree, "Pilih jenjang", DegreeOptions, { draft = draft.copy(degree = it) }, "Pilihan: D3, D4, S1, S2, atau S3.")
            FormPickerField("Perguruan Tinggi", draft.campus, "Pilih perguruan tinggi", campusOptions, { draft = draft.copy(campus = it) },
                "Cari dan pilih nama perguruan tinggimu.", showChevron = true)
            FormPickerField("Program Studi", draft.major, "Pilih program studi", majorOptions, { draft = draft.copy(major = it) },
                "Pilih program studi sesuai data PDDIKTI.")
            FormTextField("Semester Aktif", semesterText, { semesterText = it.filter(Char::isDigit).take(2) }, "Contoh: 6",
                "Isi semester pada jenjang yang sedang ditempuh.", semesterError, KeyboardType.Number)
            FormTextField("Indeks Prestasi Kumulatif (IPK)", draft.gpa, { draft = draft.copy(gpa = it.filter { c -> c.isDigit() || c == ',' || c == '.' }.take(4)) },
                "Contoh: 3,84", "Kosongkan jika belum memiliki IPK.", gpaError, KeyboardType.Decimal)
        }
        FormSectionCard("Kemampuan bahasa · opsional") {
            Text(
                "Belum memiliki sertifikat kemampuan bahasa? Kamu dapat melewati bagian ini dan melengkapinya nanti.",
                style = MaterialTheme.typography.bodyMedium,
                color = Navy,
            )
            FormPickerField("Jenis IELTS", draft.ieltsType, "Pilih jenis IELTS", IeltsTypes, { draft = draft.copy(ieltsType = it) }, "Academic / General Training.")
            FormTextField("Skor IELTS", draft.ieltsScore, { draft = draft.copy(ieltsScore = it.filter { c -> c.isDigit() || c == '.' || c == ',' }.take(3)) },
                "Contoh: 7.5", "Overall Band (0–9).", ieltsError, KeyboardType.Decimal)
            FormPickerField("Jenis TOEFL", draft.toeflType, "Pilih jenis TOEFL", ToeflTypes, { draft = draft.copy(toeflType = it) }, "ITP / iBT.")
            FormTextField("Skor TOEFL", draft.toeflScore, { draft = draft.copy(toeflScore = it.filter(Char::isDigit).take(3)) },
                "Contoh: 650", "mengikuti skala masing-masing jenis tes.", keyboardType = KeyboardType.Number)
        }
        FormActionCard("Simpan perubahan", "Informasi yang belum tersedia boleh dilengkapi nanti.", "Simpan Perubahan", valid, {
            onSave(draft.copy(name = draft.name.trim(), semester = semester))
        })
    }
}

/** Tambah Prestasi (Figma `213:3248`). */
@Composable
fun AddAchievementScreen(onBack: () -> Unit, onSave: (title: String, level: String, year: Int, organizer: String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var level by remember { mutableStateOf<String?>(null) }
    var organizer by remember { mutableStateOf("") }
    var year by remember { mutableStateOf<Int?>(null) }
    val thisYear = Year.now().value
    val valid = title.isNotBlank() && level != null && organizer.isNotBlank() && year != null

    FormScaffold("Tambah Prestasi", "Masukkan nama penghargaan, peringkat, dan nama kegiatan yang diikuti.", onBack) {
        FormSectionCard("Detail prestasi") {
            FormTextField("Nama Prestasi", title, { title = it }, "Contoh: Juara 1 Lomba Karya Tulis Ilmiah", "Cantumkan peringkat dan nama kegiatan.")
            FormPickerField("Tingkat Prestasi", level, "Pilih tingkat", AchievementLevels, { level = it }, "Kampus, kota/kabupaten, provinsi, nasional, atau internasional.")
            FormTextField("Penyelenggara", organizer, { organizer = it }, "Nama lembaga penyelenggara",
                "Masukkan nama lembaga, organisasi, atau institusi penyelenggara kegiatan.")
            FormPickerField("Tahun Perolehan", year?.toString(), "Pilih tahun", (thisYear downTo thisYear - 10).map(Int::toString),
                { year = it.toInt() }, "Gunakan tahun ketika prestasi diraih.")
        }
        FormActionCard("Tambahkan ke profil", "Pastikan informasi prestasi sudah benar sebelum disimpan.", "Simpan Prestasi", valid, {
            onSave(title.trim(), level!!, year!!, organizer.trim())
        }, onCancel = onBack)
    }
}

/**
 * Ganti Password (Figma `213:3306`). shortcut: belum ada autentikasi, jadi password saat ini hanya wajib diisi;
 * periksa ke server saat login sungguhan tersedia.
 */
@Composable
fun ChangePasswordScreen(onBack: () -> Unit, onSaved: () -> Unit) {
    var current by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(false) }
    val nextError = next.takeIf { it.isNotEmpty() }?.let(::passwordProblem)
    val confirmError = if (confirm.isNotEmpty() && confirm != next) "Password tidak sama." else null
    val valid = current.isNotEmpty() && next.isNotEmpty() && nextError == null && confirm == next

    FormScaffold("Ganti Password", "Gunakan password yang kuat dan tidak mudah ditebak untuk menjaga keamanan akunmu.", onBack) {
        FormSectionCard("Password akun") {
            FormTextField("Password Saat Ini", current, { current = it }, "Masukkan password saat ini", "Masukkan password yang sedang kamu gunakan.", password = !show)
            FormTextField("Password Baru", next, { next = it }, "Masukkan password baru", "Minimal 8 karakter, mengandung huruf dan angka.", nextError, password = !show)
            FormTextField("Konfirmasi Password Baru", confirm, { confirm = it }, "Ulangi password baru", "Harus sama dengan password baru.", confirmError, password = !show)
            OutlineButton(if (show) "Sembunyikan Password" else "Tampilkan Password", onClick = { show = !show })
        }
        FormActionCard("Simpan password baru", "Password baru langsung berlaku setelah disimpan.", "Simpan Password", valid, onSaved, onCancel = onBack)
    }
}
