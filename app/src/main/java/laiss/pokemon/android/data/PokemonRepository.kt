package laiss.pokemon.android.data

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import laiss.pokemon.android.data.dataSources.PokeApiDataSource
import laiss.pokemon.android.data.dataSources.PokemonDto
import kotlin.random.Random

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

class PokemonRepository(
    private val pokeApiDataSource: PokeApiDataSource,
    private val pageSize: Int
) {
    private val pokemonByNameCache = mutableMapOf<String, Pokemon>()
    private val pokemonListCache = mutableListOf<Pokemon?>()

    private val isInitialized
        get() = pokemonListCache.isEmpty().not()

    suspend fun getPage(number: Int, pagingOffset: Int = 0): List<Pokemon> {
        val offset = pageSize * number + pagingOffset
        if (isInitialized) {
            val cached = pokemonListCache.asSequence().drop(offset).take(pageSize)
            if (cached.all { it != null }) return cached.map { it!! }.toList()
        }

        val headerList = pokeApiDataSource.getPokemonHeadersList(offset, pageSize)

        if (isInitialized.not()) pokemonListCache.addAll(List(headerList.count) { null })

        val pokemonList = coroutineScope {
            headerList.results.map { async { getPokemonByName(it.name) } }.awaitAll()
        }
        pokemonList.forEachIndexed { i, pokemon -> pokemonListCache[offset + i] = pokemon }
        return pokemonList
    }

    suspend fun getRandomPageNumberAndOffset(): Pair<Int, Int> {
        ensureIsInitialized()
        val pageNumber = Random.nextInt(0, pokemonListCache.size / pageSize)
        val pagingOffset = Random.nextInt(0, pageSize)
        return pageNumber to pagingOffset
    }

    suspend fun getPokemonByName(pokemonName: String): Pokemon {
        ensureIsInitialized()

        val cachedPokemon = pokemonByNameCache[pokemonName]
        if (cachedPokemon != null) return cachedPokemon

        val pokemon = pokeApiDataSource.getPokemon(pokemonName).toModel()
        pokemonByNameCache[pokemon.name] = pokemon
        return pokemon
    }

    private suspend fun ensureIsInitialized() {
        if (isInitialized) return

        getPage(0)
    }
}