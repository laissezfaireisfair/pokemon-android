package laiss.pokemon.android.data

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import laiss.pokemon.android.data.dataSources.LocalStorageDataSource
import laiss.pokemon.android.data.dataSources.PokeApiDataSource
import laiss.pokemon.android.data.dataSources.PokemonEntity
import laiss.pokemon.android.data.models.Pokemon
import laiss.pokemon.android.data.models.toEntity
import laiss.pokemon.android.data.models.toModel
import kotlin.random.Random

class PokemonRepository(
    internal val pokeApiDataSource: PokeApiDataSource,
    internal val localStorageDataSource: LocalStorageDataSource,
    internal val pageSize: Int
) {
    private lateinit var strategy: IStrategy
    private var isInitialized = false

    private suspend fun ensureIsInitialized() {
        if (isInitialized) return

        val pokemonEntities = localStorageDataSource.getPokemonList()
        try {
            val pokemonCount = pokeApiDataSource.getPokemonHeadersList(0, 1).count
            strategy = OnlineStrategy(pokemonCount, pokemonEntities, this)
        } catch (_: Exception) {
            strategy = OfflineStrategy(pokemonEntities, this)
        }
    }

    suspend fun getPage(number: Int, pagingOffset: Int = 0): List<Pokemon> {
        ensureIsInitialized()
        return strategy.getPage(number, pagingOffset)
    }

    suspend fun getRandomPageNumberAndOffset(): Pair<Int, Int> {
        ensureIsInitialized()
        val pageNumber = Random.nextInt(0, strategy.pokemonCount / pageSize)
        val pagingOffset = Random.nextInt(0, pageSize)
        return pageNumber to pagingOffset
    }

    suspend fun getPokemonByName(pokemonName: String): Pokemon {
        ensureIsInitialized()
        return strategy.getPokemonByName(pokemonName)
    }
}

private interface IStrategy {
    suspend fun getPage(number: Int, pagingOffset: Int): List<Pokemon>
    suspend fun getPokemonByName(pokemonName: String): Pokemon
    val pokemonCount: Int
}

private class OnlineStrategy(
    override val pokemonCount: Int,
    pokemonEntities: List<PokemonEntity>,
    private val repository: PokemonRepository
) : IStrategy {
    /**
     * Copies remote structure, nulls for non-loaded items*/
    private val pokemonListCache = MutableList<Pokemon?>(pokemonCount) { null }
    private val pokemonByNameCache =
        pokemonEntities.map { it.toModel() }.associateBy { it.name }.toMutableMap()

    override suspend fun getPage(number: Int, pagingOffset: Int): List<Pokemon> {
        val offset = repository.pageSize * number + pagingOffset
        val cached = pokemonListCache.asSequence().drop(offset).take(repository.pageSize)
        if (cached.all { it != null }) return cached.map { it!! }.toList()

        val headerList =
            repository.pokeApiDataSource.getPokemonHeadersList(offset, repository.pageSize)

        val pokemonList = coroutineScope {
            headerList.results.map { async { getPokemonByName(it.name) } }.awaitAll()
        }
        pokemonList.forEachIndexed { i, pokemon -> pokemonListCache[offset + i] = pokemon }
        return pokemonList
    }

    override suspend fun getPokemonByName(pokemonName: String): Pokemon {
        val cachedPokemon = pokemonByNameCache[pokemonName]
        if (cachedPokemon != null) return cachedPokemon

        val pokemon = repository.pokeApiDataSource.getPokemon(pokemonName).toModel()
        pokemonByNameCache[pokemon.name] = pokemon
        repository.localStorageDataSource.storePokemon(pokemon.toEntity())
        return pokemon
    }
}

private class OfflineStrategy(
    pokemonEntities: List<PokemonEntity>,
    private val repository: PokemonRepository
) : IStrategy {
    override val pokemonCount: Int
        get() = pokemonList.size
    private val pokemonList = pokemonEntities.map { it.toModel() }
    private val pokemonByName = pokemonList.associateBy { it.name }

    override suspend fun getPage(number: Int, pagingOffset: Int): List<Pokemon> {
        val offset = repository.pageSize * number + pagingOffset
        val cropped = pokemonList.asSequence().drop(offset).take(repository.pageSize)
        return cropped.toList()
    }

    override suspend fun getPokemonByName(pokemonName: String): Pokemon {
        return pokemonByName[pokemonName]
            ?: throw IllegalArgumentException("No pokemon with such name")
    }
}