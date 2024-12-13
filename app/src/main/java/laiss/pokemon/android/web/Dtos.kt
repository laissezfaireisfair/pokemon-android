package laiss.pokemon.android.web

import kotlinx.serialization.Serializable

@Serializable
data class PokemonHeaderDto(val name: String)

@Serializable
data class PokemonHeadersListDto(val count: Int, val results: List<PokemonHeaderDto>)

@Serializable
data class PokemonSpritesDto(val front_default: String)

@Serializable
data class PokemonTypeDto(val name: String)

@Serializable
data class PokemonTypesDto(val type: PokemonTypeDto)

@Serializable
data class PokemonStatDto(val name: String)

@Serializable
data class PokemonStatsDto(val base_stat: Int, val stat: PokemonStatDto)

@Serializable
data class PokemonDto(
    val name: String,
    val id: Int,
    val height: Double,
    val weight: Double,
    val sprites: PokemonSpritesDto,
    val types: List<PokemonTypesDto>,
    val stats: List<PokemonStatsDto>
)