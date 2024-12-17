package laiss.pokemon.android.ui.states

import laiss.pokemon.android.data.models.Pokemon
import laiss.pokemon.android.utils.capitalize

data class OverviewScreenEntry(
    val name: String,
    val imageUrl: String?,
    val attack: Int,
    val defense: Int,
    val hp: Int
)

fun Pokemon.toEntry() = OverviewScreenEntry(
    name = name.capitalize(),
    imageUrl = imageUrl,
    attack = attack,
    defense = defense,
    hp = hp
)

data class OverviewScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 0,
    val pagingOffset: Int = 0,
    val isEndReached: Boolean = false,
    val isAttackSortRequested: Boolean = false,
    val isDefenseSortRequested: Boolean = false,
    val isHpSortRequested: Boolean = false,
    val entries: List<OverviewScreenEntry> = emptyList()
) {
    companion object {
        val previewOk: OverviewScreenState
            get() = OverviewScreenState(
                entries = listOf(
                    OverviewScreenEntry(
                        name = "bulbasaur".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                    OverviewScreenEntry(
                        name = "ivysaur".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/2.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                    OverviewScreenEntry(
                        name = "venusaur".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/3.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                    OverviewScreenEntry(
                        name = "charmander".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                    OverviewScreenEntry(
                        name = "charmeleon".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/5.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                    OverviewScreenEntry(
                        name = "squirtle".capitalize(),
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/6.png",
                        attack = 10, defense = 20, hp = 30
                    ),
                )
            )

        val previewLoading: OverviewScreenState
            get() = OverviewScreenState(isLoading = true)

        val previewError: OverviewScreenState
            get() = OverviewScreenState(error = "404. Not found")
    }
}