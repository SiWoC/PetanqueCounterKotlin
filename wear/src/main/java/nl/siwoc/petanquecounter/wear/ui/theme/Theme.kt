package nl.siwoc.petanquecounter.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

private val PetanqueColorScheme = ColorScheme(
    primary = FlagBlue,
    primaryDim = FlagBlue,
    primaryContainer = FlagBlue,
    onPrimary = Color.White,
    onPrimaryContainer = Color.White,
    error = FlagRed,
    errorDim = FlagRed,
    errorContainer = FlagRed,
    onError = Color.White,
    onErrorContainer = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surfaceContainerLow = Color(0xFF1A1A1A),
    surfaceContainer = Color(0xFF2C2C2C),
    surfaceContainerHigh = Color(0xFF3D3D3D),
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFB0B0B0),
)

/**
 * Black AMOLED well, white type, flag-blue ±, red Reset. [WinGold] is only for scores ≥ 13.
 */
@Composable
fun PetanqueCounterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PetanqueColorScheme,
        content = content,
    )
}
