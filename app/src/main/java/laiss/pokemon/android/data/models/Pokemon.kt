package laiss.pokemon.android.data.models

import laiss.pokemon.android.data.dataSources.PokemonDto
import laiss.pokemon.android.data.dataSources.PokemonEntity

enum class PokemonType(val typeString: String) {
    Normal("normal"),
    Fire("fire"),
    Fighting("fighting"),
    Water("water"),
    Flying("flying"),
    Grass("grass"),
    Poison("poison"),
    Electric("electric"),
    Ground("ground"),
    Psychic("psychic"),
    Rock("rock"),
    Ice("ice"),
    Bug("bug"),
    Dragon("dragon"),
    Ghost("ghost"),
    Dark("dark"),
    Steel("steel"),
    Fairy("fairy"),
    Stellar("stellar"),
    Unknown("???");

    companion object {
        operator fun get(typeString: String) = entries.firstOrNull { it.typeString == typeString }
    }
}

class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val height: Double,
    val weight: Double,
    val types: List<PokemonType>,
    val attack: Int,
    val defense: Int,
    val hp: Int
)

fun PokemonDto.toModel() = Pokemon(
    id = id,
    name = name,
    imageUrl = sprites.front_default,
    height = height,
    weight = weight,
    types = types.map { PokemonType[it.type.name] ?: PokemonType.Unknown },
    attack = stats.first { it.stat.name == "attack" }.base_stat,
    defense = stats.first { it.stat.name == "defense" }.base_stat,
    hp = stats.first { it.stat.name == "hp" }.base_stat
)

fun PokemonEntity.toModel() = Pokemon(
    id = id,
    name = name,
    imageUrl = imageUrl,
    height = height,
    weight = weight,
    types = types.split(" ").map { PokemonType[it] ?: PokemonType.Unknown },
    attack = attack,
    defense = defense,
    hp = hp
)

fun Pokemon.toEntity() = PokemonEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    height = height,
    weight = weight,
    types = types.joinToString(" ") { it.typeString },
    attack = attack,
    defense = defense,
    hp = hp
)