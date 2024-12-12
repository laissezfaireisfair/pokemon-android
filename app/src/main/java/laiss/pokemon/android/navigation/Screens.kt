package laiss.pokemon.android.navigation

enum class Screens(val route: String) {
    Overview("overview");

    companion object {
        private val screenByRoute = entries.associateBy { it.route }
        operator fun get(route: String) = screenByRoute[route]
    }
}

fun Screens.toName() = when (this) {
    Screens.Overview -> "Overview"
}