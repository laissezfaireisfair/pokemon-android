package laiss.pokemon.android.ui.viewModels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import laiss.pokemon.android.data.IPokemonRepository
import laiss.pokemon.android.ui.states.DetailsScreenState
import laiss.pokemon.android.ui.states.toDetails
import kotlin.coroutines.CoroutineContext

class DetailsScreenViewModel(
    pokemonName: String,
    defaultDispatcher: CoroutineContext,
    pokemonRepository: IPokemonRepository
) : RichViewModel() {
    private val _uiState = MutableStateFlow(DetailsScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        launchFailable {
            _uiState.update { DetailsScreenState(isLoading = true) }
            val pokemon =
                withContext(defaultDispatcher) { pokemonRepository.getPokemonByName(pokemonName) }
            _uiState.update { DetailsScreenState(details = pokemon.toDetails()) }
        }
    }

    override fun passErrorMessageToState(message: String) =
        _uiState.update { DetailsScreenState(error = message) }
}