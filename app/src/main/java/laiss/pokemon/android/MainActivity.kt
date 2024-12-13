package laiss.pokemon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import laiss.pokemon.android.navigation.Screens
import laiss.pokemon.android.ui.DetailsScreen
import laiss.pokemon.android.ui.OverviewScreen
import laiss.pokemon.android.ui.theme.PokemonAndroidTheme
import laiss.pokemon.android.ui.viewModels.DetailsScreenViewModel
import laiss.pokemon.android.ui.viewModels.OverviewScreenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                            viewModel = viewModel<OverviewScreenViewModel>().apply { setPreviewModeOk() })
                    }
                    composable("${Screens.Details.route}/{pokemonId}") { navBackStackEntry ->
                        val pokemonId =
                            navBackStackEntry.arguments?.getString("pokemonId")?.toIntOrNull()
                                ?: throw IllegalArgumentException("Pokemon ID expected when navigating to details")

                        DetailsScreen(navHostController = navHostController,
                            pokemonId = pokemonId,
                            viewModel = viewModel<DetailsScreenViewModel>().apply { setPreviewModeOk() })
                    }
                }

            }
        }
    }
}