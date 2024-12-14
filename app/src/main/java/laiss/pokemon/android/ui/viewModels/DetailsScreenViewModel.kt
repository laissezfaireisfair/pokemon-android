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

data class Details(
    val name: String,
    val imageUrl: String,
    val weight: String,
    val height: String,
    val types: List<String>,
    val attack: String,
    val defence: String,
    val hp: String,
)

fun Pokemon.toDetails() = Details(
    name = name.capitalize(),
    imageUrl = imageUrl,
    weight = weight.toString(),
    height = height.toString(),
    types = types.map { it.typeString },
    attack = attack.toString(),
    defence = defense.toString(),
    hp = hp.toString()
)

data class DetailsScreenState(
    val isLoading: Boolean = false, val error: String? = null, val details: Details? = null
) {
    companion object {
        val previewOk: DetailsScreenState
            get() = DetailsScreenState(
                details = Details(
                    name = "bulbasaur".capitalize(),
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                    weight = 69.0.toString(),
                    height = 7.0.toString(),
                    types = listOf("grass", "poison"),
                    attack = 49.toString(),
                    defence = 49.toString(),
                    hp = 45.toString()
                )
            )
        val previewLoading: DetailsScreenState
            get() = DetailsScreenState(isLoading = true)

        val previewError: DetailsScreenState
            get() = DetailsScreenState(error = "404. Not found")
    }
}

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