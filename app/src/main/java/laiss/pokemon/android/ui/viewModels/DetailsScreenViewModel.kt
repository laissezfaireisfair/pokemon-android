package laiss.pokemon.android.ui.viewModels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import laiss.pokemon.android.data.IPokemonRepository
import laiss.pokemon.android.ui.states.DetailsScreenState
import laiss.pokemon.android.ui.states.toDetails
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DetailsScreenViewModel(pokemonName: String) : RichViewModel(), KoinComponent {
    private val pokemonRepository: IPokemonRepository by inject()

    private val _uiState = MutableStateFlow(DetailsScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        launchFailable {
            _uiState.update { DetailsScreenState(isLoading = true) }
            val pokemon = pokemonRepository.getPokemonByName(pokemonName)
            _uiState.update { DetailsScreenState(details = pokemon.toDetails()) }
        }
    }

    override fun passErrorMessageToState(message: String) =
        _uiState.update { DetailsScreenState(error = message) }
}