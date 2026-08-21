package nl.siwoc.petanquecounter.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PetanqueColorScheme = darkColorScheme(
    primary = FlagBlue,
    onPrimary = Color.White,
    error = FlagRed,
    onError = Color.White,
    background = Navy,
    onBackground = Color.White,
    surface = Navy,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF3D5C7A),
    onSurfaceVariant = Color.White,
)

/**
 * Navy well, white type, flag-blue ±, red Reset. [WinGold] is only for scores ≥ 13.
 */
@Composable
fun PetanqueCounterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PetanqueColorScheme,
        typography = Typography,
        content = content,
    )
}
