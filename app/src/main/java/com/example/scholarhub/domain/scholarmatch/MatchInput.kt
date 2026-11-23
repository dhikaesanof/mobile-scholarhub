package com.example.scholarhub.domain.scholarmatch

import java.math.BigDecimal

/**
 * Jawaban form ScholarMatch (Figma `134:1910`). Nilai null berarti belum dijawab.
 * Bagian profil (status kuliah sampai IPK) bisa diisi dari profil yang tersimpan di sesi.
 */
data class MatchInput(
    val activeStudent: Boolean? = null,
    /** Salah satu dari D3, D4, S1, S2, S3. */
    val degree: String? = null,
    /** Nama dari `campusOptions`; [campusType] ikut diisi dari pilihan itu. */
    val campus: String? = null,
    val campusType: String? = null,
    /** Nama dari `majorOptions`. */
    val major: String? = null,
    val semester: Int? = null,
    val gpa: BigDecimal? = null,
    /** "Belum punya IPK": syarat IPK menjadi perlu dicek, bukan gagal. */
    val noGpa: Boolean = false,
    val organization: Boolean? = null,
    val receivingScholarship: Boolean? = null,
    val commitment: Boolean? = null,
    /** ID dari `preferenceOptions`, [MIN_PRIORITIES]–[MAX_PRIORITIES] pilihan. */
    val priorities: Set<String> = emptySet(),
) {
    /** Data profil wajib sebelum melihat rekomendasi (Figma `172:2348`). */
    val profileComplete: Boolean
        get() = activeStudent != null && degree != null && campus != null && major != null &&
            semester != null && (gpa != null || noGpa)

    val complete: Boolean
        get() = profileComplete && organization != null && receivingScholarship != null && commitment != null &&
            priorities.size in MIN_PRIORITIES..MAX_PRIORITIES

    companion object {
        const val MIN_PRIORITIES = 2
        const val MAX_PRIORITIES = 5
        val Degrees = listOf("D3", "D4", "S1", "S2", "S3")
    }
}
