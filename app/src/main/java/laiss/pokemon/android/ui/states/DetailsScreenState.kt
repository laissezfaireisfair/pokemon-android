package laiss.pokemon.android.ui.states

import laiss.pokemon.android.data.models.Pokemon
import laiss.pokemon.android.utils.capitalize

data class Details(
    val name: String,
    val imageUrl: String?,
    val weightKg: String,
    val heightCm: String,
    val types: List<String>,
    val attack: String,
    val defence: String,
    val hp: String,
)

fun Pokemon.toDetails() = Details(
    name = name.capitalize(),
    imageUrl = imageUrl,
    weightKg = (weightHg / 10.0).toString(),
    heightCm = (heightDm * 10).toString(),
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
                    weightKg = 69.0.toString(),
                    heightCm = 7.0.toString(),
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