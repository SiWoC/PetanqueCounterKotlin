package nl.siwoc.petanquecounter.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import nl.siwoc.petanquecounter.app.ui.ScoreRoute
import nl.siwoc.petanquecounter.app.ui.theme.PetanqueCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        // Transparent bars so navy Compose draws behind them; dark style keeps light icons.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            PetanqueCounterTheme {
                ScoreRoute(onExit = ::finish)
            }
        }
    }
}
