package laiss.pokemon.android.di

import laiss.pokemon.android.data.IPokemonRepository
import laiss.pokemon.android.data.PokemonRepository
import laiss.pokemon.android.data.dataSources.LocalStorageDataSource
import laiss.pokemon.android.data.dataSources.PokeApiDataSource
import laiss.pokemon.android.ui.viewModels.DetailsScreenViewModel
import laiss.pokemon.android.ui.viewModels.OverviewScreenViewModel
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { OkHttpClient() }
    single { LocalStorageDataSource(get()) }
    single { PokeApiDataSource(get()) }
    single<IPokemonRepository> { PokemonRepository(get(), get(), 30) }
}