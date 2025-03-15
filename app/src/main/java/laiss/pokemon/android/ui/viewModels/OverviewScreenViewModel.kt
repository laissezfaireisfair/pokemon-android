package laiss.pokemon.android.ui.viewModels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import laiss.pokemon.android.data.IPokemonRepository
import laiss.pokemon.android.ui.states.OverviewScreenState
import laiss.pokemon.android.ui.states.toEntry
import kotlin.coroutines.CoroutineContext

private const val MIN_ON_PAGE = 30

class OverviewScreenViewModel(
    private val defaultDispatcher: CoroutineContext,
    private val pokemonRepository: IPokemonRepository
) : RichViewModel() {
    private val _uiState = MutableStateFlow(OverviewScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        launchFailable {
            _uiState.update { OverviewScreenState(isLoading = true) }
            val entries = withContext(defaultDispatcher) {
                pokemonRepository.getPage(0).map { it.toEntry() }
            }
            _uiState.update { OverviewScreenState(entries = entries) }
        }
    }

    fun reloadFromRandomPage() = launchFailable {
        _uiState.update { OverviewScreenState(isLoading = true) }

        val (newPage, newPagingOffset) = withContext(defaultDispatcher) {
            pokemonRepository.getRandomPageNumberAndOffset()
        }
        val entries = withContext(defaultDispatcher) {
            pokemonRepository
                .getPage(newPage, newPagingOffset)
                .map { it.toEntry() }
                .toMutableList()
                .let { entries ->
                    if (entries.size < MIN_ON_PAGE)  // Random page can start even on last element
                        entries += pokemonRepository.getPage(0, newPagingOffset)
                            .map { it.toEntry() }
                    entries
                }
        }

        _uiState.update {
            OverviewScreenState(entries = entries, page = newPage, pagingOffset = newPagingOffset)
        }
    }

    fun loadNextPage() = launchFailable {
        if (uiState.value.isEndReached) return@launchFailable
        if (uiState.value.isLoading) return@launchFailable

        _uiState.update { it.copy(isLoading = true) }

        val newEntries = withContext(defaultDispatcher) {
            pokemonRepository
                .getPage(uiState.value.page + 1, uiState.value.pagingOffset)
                .map { it.toEntry() }
        }

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
        val sorted = withContext(defaultDispatcher) {
            uiState.value.entries.sortedByDescending { it.attack }
        }
        _uiState.update { it.copy(entries = sorted) }
    }


    fun changeSortByDefenseStatus(newStatus: Boolean) = launchFailable {
        _uiState.update { it.copy(isDefenseSortRequested = newStatus) }

        val sorted = withContext(defaultDispatcher) {
            uiState.value.entries.sortedByDescending { it.defense }
        }
        _uiState.update { it.copy(entries = sorted) }
    }

    fun changeSortByHpStatus(newStatus: Boolean) = launchFailable {
        _uiState.update { it.copy(isHpSortRequested = newStatus) }
        val sorted = withContext(defaultDispatcher) {
            uiState.value.entries.sortedByDescending { it.hp }
        }
        _uiState.update { it.copy(entries = sorted) }
    }

    override fun passErrorMessageToState(message: String) =
        _uiState.update { OverviewScreenState(error = message) }
}