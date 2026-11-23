package com.example.scholarhub.feature.scholarmatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scholarhub.domain.scholarmatch.MatchInput
import com.example.scholarhub.domain.scholarmatch.ScholarshipMatch
import com.example.scholarhub.domain.scholarmatch.ScholarshipMatcher
import com.example.scholarhub.domain.scholarship.CampusOption
import com.example.scholarhub.domain.scholarship.PreferenceOption
import com.example.scholarhub.domain.scholarship.ScholarshipRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.Clock
import java.time.LocalDate

/** Pilihan kampus di luar `campusOptions`; tipenya tidak diketahui. */
const val OtherCampus = "Kampus lain"
const val OtherMajor = "Program studi lain"

data class ScholarMatchFormState(
    val input: MatchInput = MatchInput(),
    /** Teks IPK apa adanya agar pengguna bisa mengetik "3," sebelum angka lengkap. */
    val gpaText: String = "",
    val gpaError: String? = null,
    val preferenceOptions: List<PreferenceOption> = emptyList(),
    val campusOptions: List<CampusOption> = emptyList(),
    val majorOptions: List<String> = emptyList(),
    val optionsError: Boolean = false,
    /** Profil sudah pernah disimpan di sesi ini; label IPK menjadi "IPK dari profil". */
    val profileSaved: Boolean = false,
)

sealed interface ScholarMatchResultState {
    data object Idle : ScholarMatchResultState
    data object Loading : ScholarMatchResultState
    data object Error : ScholarMatchResultState
    data class Ready(val matches: List<ScholarshipMatch>, val input: MatchInput, val priorities: List<PreferenceOption>) :
        ScholarMatchResultState
}

/**
 * Dipakai bersama oleh form, hasil, dan Home selama sesi aplikasi. Jawaban dan profil hanya disimpan di memori;
 * bagian profil mengisi form berikutnya agar lebih cepat.
 */
class ScholarMatchViewModel(
    private val repository: ScholarshipRepository,
    private val matcher: ScholarshipMatcher = ScholarshipMatcher(),
    private val clock: Clock = Clock.systemDefaultZone(),
) : ViewModel() {
    private val form = MutableStateFlow(ScholarMatchFormState())
    val formState = form.asStateFlow()

    private val result = MutableStateFlow<ScholarMatchResultState>(ScholarMatchResultState.Idle)
    val resultState = result.asStateFlow()

    init {
        loadOptions()
    }

    fun loadOptions() {
        viewModelScope.launch {
            repository.getCatalog().fold(
                onSuccess = { catalog ->
                    form.update {
                        it.copy(
                            preferenceOptions = catalog.preferenceOptions,
                            campusOptions = catalog.campusOptions,
                            majorOptions = catalog.majorOptions,
                            optionsError = false,
                        )
                    }
                },
                onFailure = { form.update { it.copy(optionsError = true) } },
            )
        }
    }

    fun update(transform: (MatchInput) -> MatchInput) {
        form.update { it.copy(input = transform(it.input)) }
    }

    /** Memilih kampus sekaligus tipenya; [OtherCampus] membuat tipe kampus belum diketahui. */
    fun selectCampus(name: String) {
        form.update { state ->
            val type = state.campusOptions.firstOrNull { it.name == name }?.type
            state.copy(input = state.input.copy(campus = name, campusType = type))
        }
    }

    fun setGpaText(text: String) {
        val cleaned = text.filter { it.isDigit() || it == '.' || it == ',' }.take(4)
        val parsed = cleaned.replace(',', '.').toBigDecimalOrNull()
        val error = when {
            cleaned.isEmpty() -> null
            parsed == null -> "Tulis IPK dalam angka, misalnya 3,45"
            parsed < BigDecimal.ZERO || parsed > BigDecimal("4.00") -> "IPK berada di antara 0,00 dan 4,00"
            else -> null
        }
        form.update {
            it.copy(gpaText = cleaned, gpaError = error, input = it.input.copy(gpa = parsed?.takeIf { error == null }))
        }
    }

    /** Mengisi bagian profil form dari Edit Profil; field kosong di profil tidak menimpa jawaban yang sudah ada. */
    fun applyProfile(degree: String?, campus: String?, major: String?, semester: Int?, gpaText: String) {
        update { input ->
            input.copy(
                activeStudent = if (semester != null) true else input.activeStudent,
                degree = degree ?: input.degree,
                major = major ?: input.major,
                semester = semester ?: input.semester,
            )
        }
        campus?.let(::selectCampus)
        if (gpaText.isNotBlank()) setGpaText(gpaText)
        form.update { it.copy(profileSaved = true) }
    }

    /** Menambah/menghapus prioritas; pilihan baru diabaikan jika sudah ada [MatchInput.MAX_PRIORITIES]. */
    fun togglePriority(id: String) {
        form.update { state ->
            val current = state.input.priorities
            val next = when {
                id in current -> current - id
                current.size >= MatchInput.MAX_PRIORITIES -> current
                else -> current + id
            }
            state.copy(input = state.input.copy(priorities = next))
        }
    }

    fun submit() {
        val state = form.value
        val input = state.input
        form.update { it.copy(profileSaved = true) }
        result.value = ScholarMatchResultState.Loading
        viewModelScope.launch {
            result.value = repository.getCatalog().fold(
                onSuccess = { catalog ->
                    val today = LocalDate.now(clock.withZone(catalog.timeZone))
                    ScholarMatchResultState.Ready(
                        matches = matcher.match(catalog.scholarships, input, catalog.preferenceOptions, today),
                        input = input,
                        priorities = catalog.preferenceOptions.filter { it.id in input.priorities },
                    )
                },
                onFailure = { ScholarMatchResultState.Error },
            )
        }
    }
}
