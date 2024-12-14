package laiss.pokemon.android.services

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import laiss.pokemon.android.models.Pokemon
import laiss.pokemon.android.models.PokemonType
import laiss.pokemon.android.web.PokemonDto
import laiss.pokemon.android.web.WebClient
import kotlin.random.Random

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

object DataService {
    private val pokemonByName = mutableMapOf<String, Pokemon>()
    private val pokemonList = mutableListOf<Pokemon?>()  // Index is id - 1
    private val isInitialized
        get() = pokemonList.isEmpty().not()

    suspend fun isPokemonEndReached(offset: Int): Boolean {
        ensureIsInitialized()
        return pokemonList.count() <= offset
    }

    suspend fun getPokemonList(offset: Int, count: Int): List<Pokemon> {
        if (isInitialized) {
            val cached = pokemonList.asSequence().drop(offset).take(count)
            if (cached.all { it != null }) return cached.map { it!! }.toList()
        }

        val headerList = WebClient.getPokemonHeadersList(offset, count)

        if (isInitialized.not()) pokemonList.addAll(List(headerList.count) { null })

        return coroutineScope {
            headerList.results.map { async { getPokemonByName(it.name) } }.awaitAll()
        }
    }

    suspend fun getPokemonRandomValidOffset(count: Int): Int {
        ensureIsInitialized()
        return Random.nextInt(0, pokemonList.count() - count)
    }

    suspend fun getPokemonByName(pokemonName: String): Pokemon {
        ensureIsInitialized()

        val cachedPokemon = pokemonByName[pokemonName]
        if (cachedPokemon != null) return cachedPokemon

        val pokemonDto = WebClient.getPokemon(pokemonName)
        val pokemon = pokemonDto.toModel()
        pokemonList[pokemon.id - 1] = pokemon
        pokemonByName[pokemon.name] = pokemon
        return pokemon
    }

    private suspend fun ensureIsInitialized() {
        if (isInitialized) return

        getPokemonList(0, 1)
    }
}