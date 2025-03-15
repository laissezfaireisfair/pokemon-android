package laiss.pokemon.android.ui.viewModels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import laiss.pokemon.android.data.IPokemonRepository
import laiss.pokemon.android.ui.states.OverviewScreenState
import laiss.pokemon.android.ui.states.toEntry

private const val MIN_ON_PAGE = 30

class OverviewScreenViewModel(private val pokemonRepository: IPokemonRepository) : RichViewModel() {
    private val _uiState = MutableStateFlow(OverviewScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        launchFailable {
            _uiState.update { OverviewScreenState(isLoading = true) }
            val entries = pokemonRepository.getPage(0).map { it.toEntry() }
            _uiState.update { OverviewScreenState(entries = entries) }
        }
    }

    fun reloadFromRandomPage() = launchFailable {
        _uiState.update { OverviewScreenState(isLoading = true) }

        val (newPage, newPagingOffset) = pokemonRepository.getRandomPageNumberAndOffset()
        val entries = pokemonRepository
            .getPage(newPage, newPagingOffset)
            .map { it.toEntry() }
            .toMutableList()

        if (entries.size < MIN_ON_PAGE)  // Random page can start even on last element
            entries += pokemonRepository.getPage(0, newPagingOffset).map { it.toEntry() }

        _uiState.update {
            OverviewScreenState(entries = entries, page = newPage, pagingOffset = newPagingOffset)
        }
    }

    fun loadNextPage() = launchFailable {
        if (uiState.value.isEndReached) return@launchFailable
        if (uiState.value.isLoading) return@launchFailable

        _uiState.update { it.copy(isLoading = true) }

        val newEntries = pokemonRepository
            .getPage(uiState.value.page + 1, uiState.value.pagingOffset)
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
    }

    fun changeSortByAttackStatus(newStatus: Boolean) = launchFailable {
        _uiState.update { it.copy(isAttackSortRequested = newStatus) }
        _uiState.update { state ->
            state.copy(entries = state.entries.sortedByDescending { it.attack })
        }
    }


    fun changeSortByDefenseStatus(newStatus: Boolean) = launchFailable {
        _uiState.update { it.copy(isDefenseSortRequested = newStatus) }
        _uiState.update { state ->
            state.copy(entries = state.entries.sortedByDescending { it.defense })
        }
    }

    fun changeSortByHpStatus(newStatus: Boolean) = launchFailable {
        _uiState.update { it.copy(isHpSortRequested = newStatus) }
        _uiState.update { state ->
            state.copy(entries = state.entries.sortedByDescending { it.hp })
        }
    }

    override fun passErrorMessageToState(message: String) =
        _uiState.update { OverviewScreenState(error = message) }
}