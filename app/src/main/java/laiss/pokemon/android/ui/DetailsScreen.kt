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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import laiss.pokemon.android.R
import laiss.pokemon.android.ui.states.Details
import laiss.pokemon.android.ui.states.DetailsScreenState
import laiss.pokemon.android.ui.theme.PokemonAndroidTheme
import laiss.pokemon.android.ui.theme.Subtext1
import laiss.pokemon.android.ui.viewModels.DetailsScreenViewModel

@Composable
fun DetailsScreen(navHostController: NavHostController, viewModel: DetailsScreenViewModel) {
    val state = viewModel.uiState.collectAsState().value
    DetailsScreenBody(state = state, onBackClick = navHostController::navigateUp)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenBody(state: DetailsScreenState, onBackClick: () -> Unit = {}) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Details") }, navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, "Navigate back")
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                state.isLoading -> LoadingComposable()

                state.error != null || state.details == null -> ErrorComposable(state.error)

                else -> OkStateComposable(state.details)
            }
        }
    }
}

@Composable
private fun OkStateComposable(details: Details) {
    ElevatedCard {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally),
                model = details.imageUrl,
                contentScale = ContentScale.FillBounds,
                contentDescription = "Front image"
            )

            HorizontalDivider()
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = details.name,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            StatEntry(
                value = details.heightCm,
                name = "Height",
                unit = "cm",
                painter = painterResource(R.drawable.height)
            )
            StatEntry(
                value = details.weightKg,
                name = "Weight",
                unit = "kg",
                painter = painterResource(R.drawable.weight)
            )
            StatEntry(
                value = details.attack,
                name = "Attack",
                painter = painterResource(R.drawable.attack)
            )
            StatEntry(
                value = details.defence,
                name = "Defence",
                painter = painterResource(R.drawable.defense)
            )
            StatEntry(
                value = details.hp,
                name = "HP",
                painter = painterResource(R.drawable.hp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(details.types) {
                    Text(text = it, color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}

@Composable
private fun StatEntry(painter: Painter, name: String, value: String, unit: String? = null) =
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(17.dp),
            painter = painter,
            contentDescription = "$name icon"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value)
        if (unit != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = unit, color = Subtext1)
        }
    }


@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
private fun DetailsScreenPreviewOk() = PokemonAndroidTheme {
    DetailsScreenBody(state = DetailsScreenState.previewOk)
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
private fun DetailsScreenPreviewLoading() = PokemonAndroidTheme {
    DetailsScreenBody(state = DetailsScreenState.previewLoading)
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
private fun DetailsScreenPreviewError() = PokemonAndroidTheme {
    DetailsScreenBody(state = DetailsScreenState.previewError)
}