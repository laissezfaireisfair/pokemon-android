package laiss.pokemon.android.data.dataSources

import android.util.Patterns
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.coroutines.CoroutineContext

private const val POKEMON_REQUEST_LIMIT = 100

class PokeApiDataSource(
    private val client: OkHttpClient,
    private val ioDispatcher: CoroutineContext
) {
    private val baseUrl = "https://pokeapi.co/api/v2"

    suspend fun getPokemonHeadersList(offset: Int, count: Int) = run {
        require(0 <= offset) { "Offset: $offset should be non-negative" }
        require(count in 1..POKEMON_REQUEST_LIMIT) {
            "Count: $count should be in [1, $POKEMON_REQUEST_LIMIT] range"
        }

        preformGetRequest<PokemonHeadersListDto>(
            "$baseUrl/pokemon/?limit=$count&offset=$offset"
        )
    }

    suspend fun getPokemon(name: String) =
        preformGetRequest<PokemonDto>("$baseUrl/pokemon/$name/")

    private suspend inline fun <reified T> preformGetRequest(url: String) =
        withContext(ioDispatcher) {
            require(Patterns.WEB_URL.matcher(url).matches()) { "Invalid url: $url" }

            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful.not()) throw IOException("Request failed: $response")
                response.body?.string()
            }?.let {
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    json.decodeFromString<T>(it)
                } catch (exception: SerializationException) {
                    throw IOException("Bad JSON received $exception")
                } catch (exception: IllegalArgumentException) {
                    throw IOException("Bad type of received body: $exception}")
                } catch (exception: Exception) {
                    throw IOException("Failed to deserialize: $exception")
                }
            } ?: throw IOException("Empty body received")
        }
}