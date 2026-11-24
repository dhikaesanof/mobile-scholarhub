package com.example.scholarhub.feature.scholarmatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scholarhub.domain.scholarmatch.MatchInput
import com.example.scholarhub.domain.scholarmatch.ScholarshipMatch
import com.example.scholarhub.domain.scholarmatch.ScholarshipMatcher
import com.example.scholarhub.domain.scholarship.PreferenceOption
import com.example.scholarhub.domain.scholarship.ScholarshipRepository
import com.example.scholarhub.data.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate

data class ScholarMatchFormState(
    val input: MatchInput = MatchInput(),
    /** Teks IPK apa adanya agar pengguna bisa mengetik "3," sebelum angka lengkap. */
    val gpaText: String = "",
    val gpaError: String? = null,
    val preferenceOptions: List<PreferenceOption> = emptyList(),
    val optionsError: Boolean = false,
)

sealed interface ScholarMatchResultState {
    data object Idle : ScholarMatchResultState
    data object Loading : ScholarMatchResultState
    data object Error : ScholarMatchResultState
    data class Ready(val matches: List<ScholarshipMatch>, val scheduleAsOf: LocalDate, val prioritiesSelected: Int) : ScholarMatchResultState
}

/** Dipakai bersama oleh form dan hasil ScholarMatch selama alurnya aktif. Jawaban hanya disimpan di memori. */
class ScholarMatchViewModel(
    private val repository: ScholarshipRepository,
    private val userRepository: UserRepository = UserRepository(),
    private val matcher: ScholarshipMatcher = ScholarshipMatcher(),
) : ViewModel() {
    private val form = MutableStateFlow(ScholarMatchFormState())
    val formState = form.asStateFlow()

    private val result = MutableStateFlow<ScholarMatchResultState>(ScholarMatchResultState.Idle)
    val resultState = result.asStateFlow()

    init {
        loadOptions()
        loadSavedInput()
    }

    private fun loadSavedInput() {
        viewModelScope.launch {
            val savedInput = userRepository.getMatchInput()
            if (savedInput != null) {
                // Konversi angka desimal GPA agar gpaText muncul dengan benar
                val gpaText = savedInput.gpa?.toString() ?: ""
                form.update { 
                    it.copy(
                        input = savedInput, 
                        gpaText = gpaText,
                        profileSaved = true
                    ) 
                }
            }
        }
    }

    fun loadOptions() {
        viewModelScope.launch {
            repository.getPreferenceOptions().fold(
                onSuccess = { options -> form.update { it.copy(preferenceOptions = options, optionsError = false) } },
                onFailure = { form.update { it.copy(optionsError = true) } },
            )
        }
    }

    fun update(transform: (MatchInput) -> MatchInput) {
        form.update { it.copy(input = transform(it.input)) }
    }

    fun setGpaText(text: String) {
        val cleaned = text.filter { it.isDigit() || it == '.' || it == ',' }.take(4)
        val parsed = cleaned.replace(',', '.').toBigDecimalOrNull()
        val error = when {
            cleaned.isEmpty() -> null
            parsed == null -> "Tulis IPK dalam angka, misalnya 3.45"
            parsed < BigDecimal.ZERO || parsed > BigDecimal("4.00") -> "IPK berada di antara 0.00 dan 4.00"
            else -> null
        }
        form.update {
            it.copy(
                gpaText = cleaned,
                gpaError = error,
                input = it.input.copy(gpa = parsed?.takeIf { error == null }),
            )
        }
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
        val input = form.value.input
        result.value = ScholarMatchResultState.Loading
        viewModelScope.launch {
            // Simpan input user ke Firebase sebelum menghitung hasil
            userRepository.saveMatchInput(input)
            
            result.value = repository.getCatalog().fold(
                onSuccess = { catalog ->
                    ScholarMatchResultState.Ready(
                        matches = matcher.match(catalog.scholarships, input, catalog.preferenceOptions),
                        scheduleAsOf = catalog.scheduleAsOf,
                        prioritiesSelected = input.priorities.size,
                    )
                },
                onFailure = { ScholarMatchResultState.Error },
            )
        }
    }
}
