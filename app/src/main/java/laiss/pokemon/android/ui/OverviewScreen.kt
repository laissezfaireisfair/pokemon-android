package laiss.pokemon.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import laiss.pokemon.android.navigation.navigateToDetails
import laiss.pokemon.android.ui.theme.PokemonAndroidTheme
import laiss.pokemon.android.ui.theme.Subtext0
import laiss.pokemon.android.ui.viewModels.OverviewScreenViewModel

@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
fun OverviewScreenPreviewOk() = PokemonAndroidTheme {
    OverviewScreen(navHostController = rememberNavController(),
        viewModel = viewModel<OverviewScreenViewModel> { OverviewScreenViewModel(isPreview = true) }.apply { setOkPreview() })
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
fun OverviewScreenPreviewLoading() = PokemonAndroidTheme {
    OverviewScreen(navHostController = rememberNavController(),
        viewModel = viewModel<OverviewScreenViewModel> { OverviewScreenViewModel(isPreview = true) }.apply { setLoadingPreview() })
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
fun OverviewScreenPreviewError() = PokemonAndroidTheme {
    OverviewScreen(navHostController = rememberNavController(),
        viewModel = viewModel<OverviewScreenViewModel> { OverviewScreenViewModel(isPreview = true) }.apply { setErrorPreview() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    navHostController: NavHostController, viewModel: OverviewScreenViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsState().value

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Overview") })
    }) { innerPadding ->
        when {
            state.isLoading -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) { Text("Loading...", color = Subtext0) }

            state.error != null -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = state.error, fontSize = 10.sp, color = Subtext0)
            }

            else -> LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.entries) {
                    ElevatedCard(onClick = { navHostController.navigateToDetails(it.name.lowercase()) }) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AsyncImage(
                                modifier = Modifier.size(50.dp),
                                model = it.imageUrl,
                                contentDescription = "Pokemon front image"
                            )
                            Text(text = it.name)
                        }
                    }
                }
            }
        }
    }
}