package laiss.pokemon.android.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import laiss.pokemon.android.models.Pokemon
import laiss.pokemon.android.services.DataService
import laiss.pokemon.android.utils.capitalize

data class OverviewScreenEntry(val id: Int, val name: String, val imageUrl: String)

fun Pokemon.toEntry() = OverviewScreenEntry(id = id, name = name.capitalize(), imageUrl = imageUrl)

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

    private var _isPreview = false

    fun refresh() {
        if (_isPreview) return

        viewModelScope.launch {
            _uiState.update { OverviewScreenState(isLoading = true) }
            try {
                val offset = DataService.getPokemonRandomValidOffset(pokemonInBatch)
                val entries =
                    DataService.getPokemonList(offset, pokemonInBatch).map { it.toEntry() }
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
                val isEndReached = DataService.isPokemonEndReached(offset)
                if (isEndReached) {
                    _uiState.update { it.copy(isLoading = false) }
                    return@launch
                }
                val newEntries =
                    DataService.getPokemonList(offset, pokemonInBatch).map { it.toEntry() }
                _uiState.update { it.copy(entries = it.entries + newEntries) }
            } catch (exception: Exception) {
                _uiState.update { OverviewScreenState(error = exception.message) }
            }
        }
    }

    fun setPreviewModeOk() {
        _isPreview = true
        _uiState.update {
            OverviewScreenState(
                entries = listOf(
                    OverviewScreenEntry(
                        id = 1,
                        name = "bulbasaur".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
                    ),
                    OverviewScreenEntry(
                        id = 2,
                        name = "ivysaur".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/2.png"
                    ),
                    OverviewScreenEntry(
                        id = 3,
                        name = "venusaur".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/3.png"
                    ),
                    OverviewScreenEntry(
                        id = 4,
                        name = "charmander".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png"
                    ),
                    OverviewScreenEntry(
                        id = 5,
                        name = "charmeleon".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/5.png"
                    ),
                    OverviewScreenEntry(
                        id = 6,
                        name = "squirtle".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png"
                    ),
                )
            )
        }
    }

    fun setPreviewModeLoading() {
        _isPreview = true
        _uiState.update { OverviewScreenState(isLoading = true) }
    }

    fun setPreviewModeError() {
        _isPreview = true
        _uiState.update { OverviewScreenState(error = "404. Not found") }
    }
}