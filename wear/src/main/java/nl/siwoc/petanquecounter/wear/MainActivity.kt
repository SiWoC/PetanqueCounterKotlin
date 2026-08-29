package nl.siwoc.petanquecounter.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import nl.siwoc.petanquecounter.wear.ui.ScoreRoute
import nl.siwoc.petanquecounter.wear.ui.theme.PetanqueCounterTheme

/**
 * Standalone Wear entry. Hosts [ScoreRoute] and does not require the phone app.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            PetanqueCounterTheme {
                ScoreRoute()
            }
        }
    }
}
