package com.example.scholarhub.domain.scholarmatch

import java.math.BigDecimal

/** Jawaban sementara form ScholarMatch. Nilai null berarti belum dijawab. */
data class MatchInput(
    val degree: DegreeAnswer? = null,
    val semester: SemesterAnswer? = null,
    val gpa: BigDecimal? = null,
    val campusName: String? = null,
    val major: String? = null,
    val campusType: CampusTypeAnswer? = null,
    val parentIncome: IncomeAnswer? = null,
    val receivingScholarship: Boolean? = null,
    /** Jawaban berikut ditanyakan di form, tetapi skema katalog v1 belum punya field aturan untuknya. */
    val englishTest: EnglishTestAnswer? = null,
    val englishScore: String? = null,
    val achievement: AchievementAnswer? = null,
    val organization: OrganizationAnswer? = null,
    val from3TRegion: Boolean? = null,
    /** ID dari `preferenceOptions`, maksimal [MAX_PRIORITIES]. */
    val priorities: Set<String> = emptySet(),
) {
    companion object {
        const val MAX_PRIORITIES = 5
    }
}

/** "D3 / D4 (Vokasi)" mencakup dua jenjang, sehingga aturan yang hanya menerima salah satunya menjadi belum diketahui. */
enum class DegreeAnswer(val values: Set<String>) {
    Vocational(setOf("D3", "D4")),
    Bachelor(setOf("S1")),
    Master(setOf("S2")),
    Doctoral(setOf("S3")),
}

/** Rentang semester di form. "7-8+" mencakup semester 7 sampai 14. */
enum class SemesterAnswer(val values: Set<Int>) {
    Sem1To2(setOf(1, 2)),
    Sem3To4(setOf(3, 4)),
    Sem5To6(setOf(5, 6)),
    Sem7Plus((7..14).toSet()),
}

enum class CampusTypeAnswer(val value: String) { PTN("PTN"), PTS("PTS"), PTK("PTK") }

enum class IncomeAnswer { Below3M, From3MTo7_5M, From7_5MTo15M, Above15M }

enum class EnglishTestAnswer { None, ToeflItp, ToeflIbt, Ielts }

enum class AchievementAnswer { International, National, Regional, None }

enum class OrganizationAnswer { CoreBoard, Coordinator, ActiveMember, Inactive }
