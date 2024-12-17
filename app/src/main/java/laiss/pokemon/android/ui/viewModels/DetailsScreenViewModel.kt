package laiss.pokemon.android.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import laiss.pokemon.android.data.PokemonRepository
import laiss.pokemon.android.ui.states.DetailsScreenState
import laiss.pokemon.android.ui.states.toDetails

class DetailsScreenViewModel(
    private val pokemonRepository: PokemonRepository, pokemonName: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailsScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { DetailsScreenState(isLoading = true) }
            try {
                val pokemon = pokemonRepository.getPokemonByName(pokemonName)
                _uiState.update { DetailsScreenState(details = pokemon.toDetails()) }
            } catch (exception: Exception) {
                _uiState.update { DetailsScreenState(error = exception.message) }
            } catch (error: Error) {
                _uiState.update { DetailsScreenState(error = error.message) }
            }
        }
    }
}