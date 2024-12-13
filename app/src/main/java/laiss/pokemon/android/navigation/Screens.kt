package laiss.pokemon.android.navigation

import androidx.navigation.NavHostController

enum class Screens(val route: String) { Overview("overview"), Details("details") }

fun NavHostController.navigateToDetails(pokemonName: String) {
    navigate("${Screens.Details.route}/$pokemonName")
}