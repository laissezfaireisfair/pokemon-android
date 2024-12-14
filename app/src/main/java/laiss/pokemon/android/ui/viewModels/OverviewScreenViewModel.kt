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
) {
    companion object {
        val previewOk: OverviewScreenState
            get() = OverviewScreenState(
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

        val previewLoading: OverviewScreenState
            get() = OverviewScreenState(isLoading = true)

        val previewError: OverviewScreenState
            get() = OverviewScreenState(error = "404. Not found")
    }
}

class OverviewScreenViewModel(private val pokemonRepository: PokemonRepository) : ViewModel() {
    private val pokemonInBatch = 30

    private val _uiState = MutableStateFlow(OverviewScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh(randomStart: Boolean = false) {  // TODO: Separate random logic
        viewModelScope.launch {
            _uiState.update { OverviewScreenState(isLoading = true) }
            try {
                val offset =
                    if (randomStart) pokemonRepository.getPokemonRandomValidOffset(pokemonInBatch) else 0
                val entries =
                    pokemonRepository.getPokemonList(offset, pokemonInBatch).map { it.toEntry() }
                _uiState.update { OverviewScreenState(entries = entries) }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            } catch (error: Error) {
                _uiState.update { OverviewScreenState(error = error.message) }
            }
        }
    }

    fun loadNext() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val offset = _uiState.value.offset + pokemonInBatch
            try {
                val isEndReached = pokemonRepository.isPokemonEndReached(offset)
                if (isEndReached) {
                    _uiState.update { it.copy(isLoading = false) }
                    return@launch
                }
                val newEntries =
                    pokemonRepository.getPokemonList(offset, pokemonInBatch).map { it.toEntry() }
                _uiState.update { it.copy(entries = it.entries + newEntries) }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            }
        }
    }
}