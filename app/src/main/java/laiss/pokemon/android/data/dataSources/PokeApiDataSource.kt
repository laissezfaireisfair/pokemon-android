package laiss.pokemon.android.data.dataSources

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

@Serializable
data class PokemonHeaderDto(val name: String)

@Serializable
data class PokemonHeadersListDto(val count: Int, val results: List<PokemonHeaderDto>)

@Serializable
data class PokemonSpritesDto(val front_default: String?)

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

class PokeApiDataSource(private val client: OkHttpClient) {
    private val baseUrl = "https://pokeapi.co/api/v2"
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    suspend fun getPokemonHeadersList(offset: Int, count: Int) =
        preformGetRequest<PokemonHeadersListDto>("$baseUrl/pokemon/?limit=$count&offset=$offset")

    suspend fun getPokemon(name: String) = preformGetRequest<PokemonDto>("$baseUrl/pokemon/$name/")

    private suspend inline fun <reified T> preformGetRequest(url: String) = scope.async {
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
    }.await()
}