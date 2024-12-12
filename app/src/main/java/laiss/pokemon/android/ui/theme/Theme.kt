package laiss.pokemon.android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = Base,
    onBackground = Text,

    surface = Surface1,
    surfaceBright = Surface2,
    surfaceContainer = Surface1,
    surfaceContainerHigh = Surface2,
    surfaceContainerHighest = Surface2,
    surfaceContainerLow = Surface0,
    surfaceContainerLowest = Surface0,
    surfaceDim = Surface0,
    surfaceVariant = Surface0,
    surfaceTint = Surface0,
    inverseSurface = Crust,
    onSurface = Text,
    onSurfaceVariant = Subtext0,
    inverseOnSurface = Text,

    primary = Peach,
    primaryContainer = Rosewater,
    inversePrimary = Rosewater,
    onPrimary = Mantle,
    onPrimaryContainer = Mantle,

    secondary = Surface2,
    secondaryContainer = Overlay2,
    onSecondary = Text,
    onSecondaryContainer = Crust,

    tertiary = Yellow,
    tertiaryContainer = Lavender,
    onTertiary = Text,
    onTertiaryContainer = Crust,

    outline = Lavender,
    outlineVariant = Overlay0,

    scrim = MantleHalfOpacity,

    error = Red,
    errorContainer = Pink,
    onError = Text,
    onErrorContainer = Mantle,
)

@Composable
fun PokemonAndroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> DarkColorScheme  // TODO: Add light color scheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}