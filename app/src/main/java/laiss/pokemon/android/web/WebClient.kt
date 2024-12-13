package laiss.pokemon.android.web

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object WebClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2"
    private val client = OkHttpClient()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    suspend fun getPokemonHeadersList(offset: Int, count: Int) =
        preformGetRequest<PokemonHeadersListDto>("$BASE_URL/pokemon/?offset=$offset&count=$count")

    suspend fun getPokemon(name: String) = preformGetRequest<PokemonDto>("$BASE_URL/pokemon/$name/")

    private suspend inline fun <reified T> preformGetRequest(url: String) = scope.async {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful.not()) throw IOException("Request failed: $response")
            response.body?.string()
        }?.let {
            try {
                Json.decodeFromString<T>(it)
            } catch (exception: SerializationException) {
                throw IOException("Bad JSON received $exception")
            } catch (exception: IllegalArgumentException) {
                throw IOException("Bad type of received body: $exception}")
            } catch (exception: Exception) {
                throw IOException("Failed to deserialize: $exception")
            }
        } ?: throw IOException("Empty body received")
    }.await()
}