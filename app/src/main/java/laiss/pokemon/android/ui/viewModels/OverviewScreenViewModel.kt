package laiss.pokemon.android.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import laiss.pokemon.android.services.DataService

data class OverviewScreenEntry(
    val name: String, val imageUrl: String
)

data class OverviewScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val offset: Int = 0,
    val entries: List<OverviewScreenEntry> = emptyList()
)

class OverviewScreenViewModel : ViewModel() {
    private val pokemonInBatch = 30

    private val _uiState = MutableStateFlow(OverviewScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { OverviewScreenState(isLoading = true) }
            try {
                val offset = DataService.getPokemonRandomValidOffset(pokemonInBatch)
                val entries = DataService.getPokemonList(offset, pokemonInBatch)
                    .map { OverviewScreenEntry(name = it.name, imageUrl = it.imageUrl) }
                _uiState.update { OverviewScreenState(entries = entries) }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            }
        }
    }

    fun loadNext() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val offset = _uiState.value.offset + pokemonInBatch
            try {
                val newEntries = DataService.getPokemonList(offset, pokemonInBatch)
                    .map { OverviewScreenEntry(name = it.name, imageUrl = it.imageUrl) }
                _uiState.update { it.copy(entries = it.entries + newEntries) }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            }
        }
    }

    fun setPreviewMode() {
        _uiState.update {
            OverviewScreenState(
                entries = listOf(
                    OverviewScreenEntry(
                        name = "bulbasaur",
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
                    ),
                    OverviewScreenEntry(
                        name = "ivysaur",
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/2.png"
                    ),
                    OverviewScreenEntry(
                        name = "venusaur",
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/3.png"
                    ),
                    OverviewScreenEntry(
                        name = "charmander",
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"
                    ),
                    OverviewScreenEntry(
                        name = "charmeleon",
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/5.png"
                    ),
                    OverviewScreenEntry(
                        name = "squirtle",
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png"
                    ),
                )
            )
        }
    }
}