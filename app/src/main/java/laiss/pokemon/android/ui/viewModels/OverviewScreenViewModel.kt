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

data class OverviewScreenEntry(val name: String, val imageUrl: String)

fun Pokemon.toEntry() = OverviewScreenEntry(name = name.capitalize(), imageUrl = imageUrl)

data class OverviewScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val offset: Int = 0,
    val entries: List<OverviewScreenEntry> = emptyList()
)

class OverviewScreenViewModel(private val isPreview: Boolean = false) : ViewModel() {
    private val pokemonInBatch = 30

    private val _uiState = MutableStateFlow(OverviewScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh(randomStart: Boolean = false) {  // TODO: Separate random logic
        if (isPreview) return

        viewModelScope.launch {
            _uiState.update { OverviewScreenState(isLoading = true) }
            try {
                val offset =
                    if (randomStart) PokemonRepository.getPokemonRandomValidOffset(pokemonInBatch) else 0
                val entries =
                    PokemonRepository.getPokemonList(offset, pokemonInBatch).map { it.toEntry() }
                _uiState.update { OverviewScreenState(entries = entries) }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            } catch (error: Error) {
                _uiState.update { OverviewScreenState(error = error.message) }
            }
        }
    }

    fun loadNext() {
        if (isPreview) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val offset = _uiState.value.offset + pokemonInBatch
            try {
                val isEndReached = PokemonRepository.isPokemonEndReached(offset)
                if (isEndReached) {
                    _uiState.update { it.copy(isLoading = false) }
                    return@launch
                }
                val newEntries =
                    PokemonRepository.getPokemonList(offset, pokemonInBatch).map { it.toEntry() }
                _uiState.update { it.copy(entries = it.entries + newEntries) }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            }
        }
    }

    fun setOkPreview() = _uiState.update {
        OverviewScreenState(
            entries = listOf(
                OverviewScreenEntry(
                    name = "bulbasaur".capitalize(),
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
                ),
                OverviewScreenEntry(
                    name = "ivysaur".capitalize(),
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/2.png"
                ),
                OverviewScreenEntry(
                    name = "venusaur".capitalize(),
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/3.png"
                ),
                OverviewScreenEntry(
                    name = "charmander".capitalize(),
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"
                ),
                OverviewScreenEntry(
                    name = "charmeleon".capitalize(),
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/5.png"
                ),
                OverviewScreenEntry(
                    name = "squirtle".capitalize(),
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png"
                ),
            )
        )
    }

    fun setLoadingPreview() = _uiState.update { OverviewScreenState(isLoading = true) }

    fun setErrorPreview() = _uiState.update { OverviewScreenState(error = "404. Not found") }
}