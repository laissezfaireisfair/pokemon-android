package laiss.pokemon.android.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import laiss.pokemon.android.data.PokemonRepository
import laiss.pokemon.android.ui.states.OverviewScreenState
import laiss.pokemon.android.ui.states.toEntry

class OverviewScreenViewModel(private val pokemonRepository: PokemonRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(OverviewScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { OverviewScreenState(isLoading = true) }
            try {
                val entries = pokemonRepository.getPage(0).map { it.toEntry() }
                _uiState.update { OverviewScreenState(entries = entries) }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            } catch (error: Error) {
                _uiState.update { OverviewScreenState(error = error.message) }
            }
        }
    }

    fun reloadFromRandomPage() {
        val minOnPage = 30

        viewModelScope.launch {
            _uiState.update { OverviewScreenState(isLoading = true) }
            try {
                val (newPage, newPagingOffset) = pokemonRepository.getRandomPageNumberAndOffset()
                val entries =
                    pokemonRepository.getPage(newPage, newPagingOffset).map { it.toEntry() }
                        .toMutableList()
                if (entries.size < minOnPage)  // Random page can start even on last element
                    entries += pokemonRepository.getPage(0, newPagingOffset).map { it.toEntry() }
                _uiState.update {
                    OverviewScreenState(
                        entries = entries,
                        page = newPage,
                        pagingOffset = newPagingOffset
                    )
                }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            } catch (error: Error) {
                _uiState.update { OverviewScreenState(error = error.message) }
            }
        }
    }

    fun loadNextPage() {
        if (uiState.value.isEndReached) return
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val newEntries =
                    pokemonRepository.getPage(uiState.value.page + 1, uiState.value.pagingOffset)
                        .map { it.toEntry() }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        entries = it.entries + newEntries,
                        isEndReached = newEntries.isEmpty(),
                        page = it.page + 1,
                        isAttackSortRequested = false,
                        isDefenseSortRequested = false,
                        isHpSortRequested = false
                    )
                }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            }
        }
    }

    fun changeSortByAttackStatus(newStatus: Boolean) {
        _uiState.update { it.copy(isAttackSortRequested = newStatus) }
        try {
            viewModelScope.launch {
                _uiState.update { state -> state.copy(entries = state.entries.sortedByDescending { it.attack }) }
            }
        } catch (exception: Exception) {
            _uiState.update { OverviewScreenState(error = exception.message) }
        } catch (exception: Error) {
            _uiState.update { OverviewScreenState(error = exception.message) }
        }
    }

    fun changeSortByDefenseStatus(newStatus: Boolean) {
        _uiState.update { it.copy(isDefenseSortRequested = newStatus) }
        try {
            viewModelScope.launch {
                _uiState.update { state -> state.copy(entries = state.entries.sortedByDescending { it.defense }) }
            }
        } catch (exception: Exception) {
            _uiState.update { OverviewScreenState(error = exception.message) }
        } catch (exception: Error) {
            _uiState.update { OverviewScreenState(error = exception.message) }
        }
    }

    fun changeSortByHpStatus(newStatus: Boolean) {
        _uiState.update { it.copy(isHpSortRequested = newStatus) }
        try {
            viewModelScope.launch {
                _uiState.update { state -> state.copy(entries = state.entries.sortedByDescending { it.hp }) }
            }
        } catch (exception: Exception) {
            _uiState.update { OverviewScreenState(error = exception.message) }
        } catch (exception: Error) {
            _uiState.update { OverviewScreenState(error = exception.message) }
        }
    }
}