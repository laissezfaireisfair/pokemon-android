package laiss.pokemon.android.web

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient

object WebClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"
    private val client = OkHttpClient()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    suspend fun getPokemonHeadersList(offset: Int, count: Int): PokemonHeadersListDto = TODO()

    suspend fun getPokemon(name: String): PokemonDto = TODO()
}