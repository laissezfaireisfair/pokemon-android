package laiss.pokemon.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import laiss.pokemon.android.R
import laiss.pokemon.android.navigation.navigateToDetails
import laiss.pokemon.android.ui.states.OverviewScreenState
import laiss.pokemon.android.ui.theme.PokemonAndroidTheme
import laiss.pokemon.android.ui.viewModels.OverviewScreenViewModel

@Composable
fun OverviewScreen(navHostController: NavHostController, viewModel: OverviewScreenViewModel) {
    val state = viewModel.uiState.collectAsState().value
    OverviewScreenBody(
        state = state,
        onPokemonClick = { navHostController.navigateToDetails(it.lowercase()) },
        onReloadClick = viewModel::reloadFromRandomPage,
        onListEndReached = viewModel::loadNextPage,
        onAttackSortCheckedChange = viewModel::changeSortByAttackStatus,
        onDefenseSortCheckedChange = viewModel::changeSortByDefenseStatus,
        onHpSortCheckedChange = viewModel::changeSortByHpStatus
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreenBody(
    state: OverviewScreenState,
    onPokemonClick: (String) -> Unit = {},
    onReloadClick: () -> Unit = {},
    onListEndReached: () -> Unit = {},
    onAttackSortCheckedChange: (Boolean) -> Unit = {},
    onDefenseSortCheckedChange: (Boolean) -> Unit = {},
    onHpSortCheckedChange: (Boolean) -> Unit = {}
) {
    Scaffold(topBar = { TopAppBar(title = { Text(text = "Overview") }) }, floatingActionButton = {
        with(MaterialTheme.colorScheme) {
            IconButton(
                modifier = Modifier.size(52.dp),
                onClick = onReloadClick,
                colors = IconButtonColors(
                    containerColor = primary,
                    contentColor = onPrimary,
                    disabledContentColor = secondaryContainer,
                    disabledContainerColor = onSecondaryContainer
                ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.dices),
                    contentDescription = "Reload from random place"
                )
            }
        }
    }) { innerPadding ->
        when {
            state.isLoading && state.entries.isEmpty() -> LoadingComposable()

            state.error != null -> ErrorComposable(state.error)

            else -> OkStateComposable(
                innerPadding,
                state,
                onAttackSortCheckedChange,
                onDefenseSortCheckedChange,
                onHpSortCheckedChange,
                onPokemonClick,
                onListEndReached
            )
        }
    }
}

@Composable
private fun OkStateComposable(
    innerPadding: PaddingValues,
    state: OverviewScreenState,
    onAttackSortCheckedChange: (Boolean) -> Unit,
    onDefenseSortCheckedChange: (Boolean) -> Unit,
    onHpSortCheckedChange: (Boolean) -> Unit,
    onPokemonClick: (String) -> Unit,
    onListEndReached: () -> Unit
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.isAttackSortRequested,
                        enabled = state.isLoading.not(),
                        onCheckedChange = onAttackSortCheckedChange
                    )
                    Text("Attack")
                    Spacer(modifier = Modifier.width(20.dp))
                    Checkbox(
                        checked = state.isDefenseSortRequested,
                        enabled = state.isLoading.not(),
                        onCheckedChange = onDefenseSortCheckedChange
                    )
                    Text("Defense")
                    Spacer(modifier = Modifier.width(20.dp))
                    Checkbox(
                        checked = state.isHpSortRequested,
                        enabled = state.isLoading.not(),
                        onCheckedChange = onHpSortCheckedChange
                    )
                    Text("Hp")
                }
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        items(items = state.entries, key = { it.name }) {
            ElevatedCard(onClick = { onPokemonClick(it.name) }) {
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

        item {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(8.dp)
                )
            }
        }
    }

    RegisterListEndCallback(listState = listState, onListEndReached = onListEndReached)
}

@Composable
private fun RegisterListEndCallback(
    listState: LazyListState, buffer: Int = 4, onListEndReached: () -> Unit
) {
    val isListEndReached = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - buffer)
        }
    }

    LaunchedEffect(isListEndReached) {
        snapshotFlow { isListEndReached.value }.distinctUntilChanged()
            .collect { onListEndReached() }
    }
}


@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
private fun OverviewScreenPreviewOk() = PokemonAndroidTheme {
    OverviewScreenBody(state = OverviewScreenState.previewOk)
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
private fun OverviewScreenPreviewLoading() = PokemonAndroidTheme {
    OverviewScreenBody(state = OverviewScreenState.previewLoading)
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xff24273a)
private fun OverviewScreenPreviewError() = PokemonAndroidTheme {
    OverviewScreenBody(state = OverviewScreenState.previewError)
}