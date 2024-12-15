package laiss.pokemon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import laiss.pokemon.android.data.PokemonRepository
import laiss.pokemon.android.data.dataSources.LocalStorageDataSource
import laiss.pokemon.android.data.dataSources.PokeApiDataSource
import laiss.pokemon.android.navigation.Screens
import laiss.pokemon.android.ui.DetailsScreen
import laiss.pokemon.android.ui.OverviewScreen
import laiss.pokemon.android.ui.theme.PokemonAndroidTheme
import laiss.pokemon.android.ui.viewModels.DetailsScreenViewModel
import laiss.pokemon.android.ui.viewModels.OverviewScreenViewModel
import okhttp3.OkHttpClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val httpClient = OkHttpClient()
        val pokeApiDataSource = PokeApiDataSource(httpClient)
        val localStorageDataSource = LocalStorageDataSource(applicationContext)
        val pokemonRepository = PokemonRepository(pokeApiDataSource, localStorageDataSource, 30)

        enableEdgeToEdge()
        setContent {
            PokemonAndroidTheme {
                val navHostController = rememberNavController()
                NavHost(
                    navController = navHostController,
                    startDestination = Screens.Overview.route,
                ) {
                    composable(Screens.Overview.route) {
                        OverviewScreen(navHostController = navHostController,
                            viewModel = viewModel { OverviewScreenViewModel(pokemonRepository) })
                    }
                    composable("${Screens.Details.route}/{pokemonName}") { navBackStackEntry ->
                        val pokemonName = navBackStackEntry.arguments?.getString("pokemonName")
                            ?: throw IllegalArgumentException("Pokemon name expected when navigating to details")

                        DetailsScreen(navHostController = navHostController, viewModel = viewModel {
                            DetailsScreenViewModel(pokemonRepository, pokemonName)
                        })
                    }
                }

            }
        }
    }
}