package laiss.pokemon.android.models

class Pokemon(
    val name: String,
    val imageUrl: String,
    val height: Double,
    val weight: Double,
    val types: List<PokemonType>,
    val attack: Int,
    val defence: Int,
    val hp: Int
)