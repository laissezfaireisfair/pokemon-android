package laiss.pokemon.android.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import laiss.pokemon.android.navigation.Screens
import laiss.pokemon.android.navigation.toName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navHostController: NavHostController) = TopAppBar(title = {
    val text = navHostController.currentDestination?.route?.let { Screens[it]?.toName() }
        ?: "Pokemon android"
    Text(text = text)
}, navigationIcon = {
    when {
        navHostController.previousBackStackEntry != null -> IconButton({ navHostController.navigateUp() }) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Navigate back")
        }
    }
})