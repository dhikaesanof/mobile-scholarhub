package com.example.scholarhub.feature.scholarship.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scholarhub.domain.scholarship.ScholarshipRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Clock

/** Memuat katalog dari [ScholarshipRepository] dan menyiapkan kartu untuk Beasiswa Page. */
class ScholarshipListViewModel(
    private val repository: ScholarshipRepository,
    private val clock: Clock = Clock.systemDefaultZone(),
) : ViewModel() {
    private val state = MutableStateFlow<ScholarshipListState>(ScholarshipListState.Loading)
    val uiState = state.asStateFlow()

    init {
        load()
    }

    fun load() {
        state.value = ScholarshipListState.Loading
        viewModelScope.launch {
            state.value = repository.getCatalog().fold(
                onSuccess = { catalog ->
                    ScholarshipListState.Ready(catalog.scholarships.map { it.toCatalogItem(clock.instant()) }.sortedForCatalog())
                },
                onFailure = { ScholarshipListState.Error },
            )
        }
    }
}

sealed interface ScholarshipListState {
    data object Loading : ScholarshipListState
    data object Error : ScholarshipListState
    data class Ready(val items: List<CatalogItem>) : ScholarshipListState
}
