package nl.siwoc.petanquecounter.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import nl.siwoc.petanquecounter.app.ui.ScoreRoute
import nl.siwoc.petanquecounter.app.ui.theme.Navy
import nl.siwoc.petanquecounter.app.ui.theme.PetanqueCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        val navy = Navy.toArgb()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(navy),
            navigationBarStyle = SystemBarStyle.dark(navy),
        )
        setContent {
            PetanqueCounterTheme {
                ScoreRoute(onExit = ::finish)
            }
        }
    }
}
