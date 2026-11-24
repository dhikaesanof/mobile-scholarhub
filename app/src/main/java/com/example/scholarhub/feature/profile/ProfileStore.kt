package com.example.scholarhub.feature.profile

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val AchievementLevels = listOf("Kampus", "Kota/Kabupaten", "Provinsi", "Nasional", "Internasional")
val DegreeOptions = listOf("D3", "D4", "S1", "S2", "S3")
val IeltsTypes = listOf("Academic", "General Training")
val ToeflTypes = listOf("ITP", "iBT")

data class Achievement(val id: String, val title: String, val level: String, val year: Int, val organizer: String)

/**
 * Profil mentee (Figma `143:2716`, kosong `143:2995`). Field akademik juga mengisi form ScholarMatch dan sebaliknya,
 * sehingga profil menjadi jalan pintas mengisi form. Nilai kosong ditampilkan "-".
 */
data class MenteeProfile(
    val name: String = "Rayhan Pratama",
    val photo: Uri? = null,
    val degree: String? = null,
    val campus: String? = null,
    val major: String? = null,
    val semester: Int? = null,
    /** Teks IPK apa adanya, mis. "3,84". */
    val gpa: String = "",
    val ieltsType: String? = null,
    val ieltsScore: String = "",
    val toeflType: String? = null,
    val toeflScore: String = "",
    val achievements: List<Achievement> = emptyList(),
) {
    /** Data yang diperlukan agar kartu "Lengkapi profilmu" di Home hilang. */
    val academicComplete: Boolean get() = degree != null && campus != null && major != null && semester != null
}

/** Profil mentee contoh (Figma `239:13964`) yang dilihat mentor dari kartu sesi. */
val SampleMenteeProfile = MenteeProfile(
    campus = "Universitas Indonesia", major = "Teknik Informatika", semester = 6, gpa = "3,84",
    ieltsType = "Academic", ieltsScore = "7.5", toeflType = "ITP", toeflScore = "650",
    achievements = listOf(
        Achievement("gemastik", "Juara 1 Hackathon Nasional GEMASTIK 2023", "Nasional", 2023, "Puspresnas Kemendikbudristek RI"),
        Achievement("ieee", "Best Paper International Conference on AI & Data", "Internasional", 2024, "IEEE CS Chapter"),
    ),
)

/** Profil di memori untuk satu sesi masuk. shortcut: belum tersimpan permanen; pindahkan ke penyimpanan akun saat backend ada. */
class ProfileStore {
    private val state = MutableStateFlow(MenteeProfile())
    val profile: StateFlow<MenteeProfile> = state.asStateFlow()

    fun update(transform: (MenteeProfile) -> MenteeProfile) = state.update(transform)

    fun addAchievement(title: String, level: String, year: Int, organizer: String) = state.update {
        it.copy(achievements = listOf(Achievement("A${System.nanoTime()}", title, level, year, organizer)) + it.achievements)
    }

    fun removeAchievement(id: String) = state.update { p -> p.copy(achievements = p.achievements.filterNot { it.id == id }) }
}

/** Aturan password baru (Figma `213:3306`): minimal 8 karakter, berisi huruf dan angka. Null = valid. */
fun passwordProblem(value: String): String? = when {
    value.length < 8 -> "Minimal 8 karakter."
    value.none(Char::isLetter) || value.none(Char::isDigit) -> "Harus mengandung huruf dan angka."
    else -> null
}

/** Null = valid atau kosong. */
fun gpaProblem(text: String): String? {
    if (text.isBlank()) return null
    val value = text.replace(',', '.').toBigDecimalOrNull() ?: return "Tulis IPK dalam angka, misalnya 3,45"
    return if (value < java.math.BigDecimal.ZERO || value > java.math.BigDecimal("4.00")) "IPK berada di antara 0,00 dan 4,00" else null
}
