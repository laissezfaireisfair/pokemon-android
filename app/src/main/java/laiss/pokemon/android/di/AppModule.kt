package laiss.pokemon.android.di

import kotlinx.coroutines.Dispatchers
import laiss.pokemon.android.data.IPokemonRepository
import laiss.pokemon.android.data.PokemonRepository
import laiss.pokemon.android.data.dataSources.LocalStorageDataSource
import laiss.pokemon.android.data.dataSources.PokeApiDataSource
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

const val IO_DISPATCHER_NAME = "ioDispatcher"
const val DEFAULT_DISPATCHER_NAME = "defaultDispatcher"

val appModule = module {
    single<CoroutineContext>(named(IO_DISPATCHER_NAME)) { Dispatchers.IO }
    single<CoroutineContext>(named(DEFAULT_DISPATCHER_NAME)) { Dispatchers.Default }
    single { OkHttpClient() }
    single { LocalStorageDataSource(get(), get(named(IO_DISPATCHER_NAME))) }
    single { PokeApiDataSource(get(), get(named(IO_DISPATCHER_NAME))) }
    single<IPokemonRepository> { PokemonRepository(get(), get(), 30) }
}
