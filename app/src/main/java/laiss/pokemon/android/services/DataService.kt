package laiss.pokemon.android.services

import laiss.pokemon.android.models.Pokemon

object DataService {
    suspend fun isPokemonEndReached(offset: Int): Boolean = TODO()

    suspend fun getPokemonList(offset: Int, count: Int): List<Pokemon> = TODO()

    suspend fun getPokemonRandomValidOffset(count: Int): Int = TODO()

    suspend fun getPokemonById(pokemonId: Int): Pokemon = TODO()
}