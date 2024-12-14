package laiss.pokemon.android.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import laiss.pokemon.android.data.Pokemon
import laiss.pokemon.android.data.PokemonRepository
import laiss.pokemon.android.utils.capitalize

data class OverviewScreenEntry(
    val name: String,
    val imageUrl: String?,
    val attack: Int,
    val defense: Int,
    val hp: Int
)

fun Pokemon.toEntry() = OverviewScreenEntry(
    name = name.capitalize(),
    imageUrl = imageUrl,
    attack = attack,
    defense = defense,
    hp = hp
)

data class OverviewScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val pagingOffset: Int = 0,
    val isEndReached: Boolean = false,
    val isAttackSortRequested: Boolean = false,
    val isDefenseSortRequested: Boolean = false,
    val isHpSortRequested: Boolean = false,
    val entries: List<OverviewScreenEntry> = emptyList()
) {
    companion object {
        val previewOk: OverviewScreenState
            get() = OverviewScreenState(
                entries = listOf(
                    OverviewScreenEntry(
                        name = "bulbasaur".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                    OverviewScreenEntry(
                        name = "ivysaur".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/2.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                    OverviewScreenEntry(
                        name = "venusaur".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/3.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                    OverviewScreenEntry(
                        name = "charmander".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                    OverviewScreenEntry(
                        name = "charmeleon".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/5.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                    OverviewScreenEntry(
                        name = "squirtle".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                )
            )

        val previewLoading: OverviewScreenState
            get() = OverviewScreenState(isLoading = true)

        val previewError: OverviewScreenState
            get() = OverviewScreenState(error = "404. Not found")
    }
}

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
                        page = it.page + 1
                    )
                }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            }
        }
    }

    fun changeSortByAttackStatus(newStatus: Boolean) {
        _uiState.update { it.copy(isAttackSortRequested = newStatus) }
        viewModelScope.launch {
            _uiState.update { state -> state.copy(entries = state.entries.sortedByDescending { it.attack }) }
        }
    }

    fun changeSortByDefenseStatus(newStatus: Boolean) {
        _uiState.update { it.copy(isDefenseSortRequested = newStatus) }
        viewModelScope.launch {
            _uiState.update { state -> state.copy(entries = state.entries.sortedByDescending { it.defense }) }
        }
    }

    fun changeSortByHpStatus(newStatus: Boolean) {
        _uiState.update { it.copy(isHpSortRequested = newStatus) }
        viewModelScope.launch {
            _uiState.update { state -> state.copy(entries = state.entries.sortedByDescending { it.hp }) }
        }
    }
}