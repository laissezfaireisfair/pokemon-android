package laiss.pokemon.android.data.dataSources

import laiss.pokemon.android.data.Pokemon

class LocalStorageDataSource {
    suspend fun getPokemonList(): List<Pokemon> = emptyList()  // TODO: Implement

    suspend fun storePokemon(pokemon: Pokemon) {}  // TODO: Implement
}