package com.example.scholarhub.domain.scholarmatch

import com.example.scholarhub.domain.scholarship.PreferenceOption
import com.example.scholarhub.domain.scholarship.Scholarship

/** Hasil evaluasi aturan otomatis. */
enum class MatchStatus(val label: String) {
    Eligible("Memenuhi aturan otomatis"),
    NotEligible("Tidak memenuhi aturan"),
    NeedsReview("Perlu dicek"),
}

/** Hasil satu jalur (atau aturan umum jika beasiswa tidak punya `tracks`). */
data class TrackResult(
    val trackId: String?,
    val trackName: String?,
    val status: MatchStatus,
    /** Syarat yang gagal atau belum bisa dinilai dari jawaban. */
    val reasons: List<String>,
    /** Ketentuan `manualChecks` yang berlaku jika aturan otomatis terpenuhi. */
    val manualChecks: List<String>,
)

data class ScholarshipMatch(
    val scholarship: Scholarship,
    val status: MatchStatus,
    /** Jalur yang menentukan [status] (jalur terbaik); null jika beasiswa tidak punya jalur. */
    val bestTrack: TrackResult,
    val tracks: List<TrackResult>,
    /** Prioritas pengguna yang cocok dengan tag manfaat yang didukung data. */
    val matchedPriorities: List<PreferenceOption>,
    /** Jumlah prioritas unik yang cocok. */
    val score: Int,
    /** Persentase prioritas yang cocok; null jika pengguna tidak memilih prioritas. */
    val relevancePercent: Int?,
)
