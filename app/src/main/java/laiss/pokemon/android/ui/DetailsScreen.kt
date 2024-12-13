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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import laiss.pokemon.android.ui.theme.PokemonAndroidTheme
import laiss.pokemon.android.ui.theme.Subtext0
import laiss.pokemon.android.ui.theme.Subtext1
import laiss.pokemon.android.ui.viewModels.DetailsScreenViewModel

@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
fun DetailsScreenPreviewOk() = PokemonAndroidTheme {
    DetailsScreen(
        navHostController = rememberNavController(),
        viewModel = viewModel<DetailsScreenViewModel>().apply { setPreviewModeOk() },
        pokemonId = 1
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
fun DetailsScreenPreviewLoading() = PokemonAndroidTheme {
    DetailsScreen(
        navHostController = rememberNavController(),
        viewModel = viewModel<DetailsScreenViewModel>().apply { setPreviewModeLoading() },
        pokemonId = 1
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
fun DetailsScreenPreviewError() = PokemonAndroidTheme {
    DetailsScreen(
        navHostController = rememberNavController(),
        viewModel = viewModel<DetailsScreenViewModel>().apply { setPreviewModeError() },
        pokemonId = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navHostController: NavHostController,
    viewModel: DetailsScreenViewModel = viewModel(),
    pokemonId: Int
) {
    viewModel.launch(pokemonId)

    val state = viewModel.uiState.collectAsState().value

    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Details") }, navigationIcon = {
            IconButton(onClick = { navHostController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, "Navigate back")
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                state.isLoading -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { Text("Loading...", color = Subtext0) }

                state.error != null || state.details == null -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Something went wrong", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = state.error, fontSize = 10.sp, color = Subtext0)
                    }
                }

                else -> ElevatedCard {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .size(150.dp)
                                .align(Alignment.CenterHorizontally),
                            model = state.details.imageUrl,
                            contentScale = ContentScale.FillBounds,
                            contentDescription = "Front image"
                        )

                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.details.name,
                            fontSize = 25.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        StatEntry("Height", state.details.height)
                        StatEntry("Weight", state.details.weight)
                        StatEntry("Attack", state.details.attack)
                        StatEntry("Defence", state.details.defence)
                        StatEntry("HP", state.details.hp)

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(state.details.types) {
                                Text(text = it, color = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatEntry(name: String, value: String) = Row {
    Text(text = "$name:", color = Subtext1)
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = value)
}