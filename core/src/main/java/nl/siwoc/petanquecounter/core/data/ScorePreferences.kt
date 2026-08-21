package nl.siwoc.petanquecounter.core.data

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import nl.siwoc.petanquecounter.core.domain.GameState
import nl.siwoc.petanquecounter.core.domain.PhoneLayout
import nl.siwoc.petanquecounter.core.domain.ScoreSnapshot

/**
 * Preference keys and the compact undo encoding used by [ScoreRepository].
 *
 * History is a single string so Preferences can hold the Undo stack without
 * a second store or a match log.
 */
internal object ScorePreferences {
    val NOUS_SCORE = intPreferencesKey("nousScore")
    val EUX_SCORE = intPreferencesKey("euxScore")
    val LAYOUT = stringPreferencesKey("layout")
    val HISTORY = stringPreferencesKey("history")
}

/**
 * Reads a [GameState], using fresh defaults when a key is missing or invalid.
 */
internal fun Preferences.toGameState(): GameState = GameState(
    nousScore = (this[ScorePreferences.NOUS_SCORE] ?: 0).coerceAtLeast(0),
    euxScore = (this[ScorePreferences.EUX_SCORE] ?: 0).coerceAtLeast(0),
    layout = parseLayout(this[ScorePreferences.LAYOUT]),
    history = decodeHistory(this[ScorePreferences.HISTORY].orEmpty()),
)

/**
 * Writes every field so a later read reconstructs the same [GameState].
 */
internal fun GameState.toPreferences(): Preferences = mutablePreferencesOf(
    ScorePreferences.NOUS_SCORE to nousScore,
    ScorePreferences.EUX_SCORE to euxScore,
    ScorePreferences.LAYOUT to layout.name,
    ScorePreferences.HISTORY to encodeHistory(history),
)

/**
 * Encodes Undo snapshots as `nous,eux;nous,eux` (oldest first).
 */
internal fun encodeHistory(history: List<ScoreSnapshot>): String =
    history.joinToString(";") { "${it.nousScore},${it.euxScore}" }

/**
 * Parses [encodeHistory] output. Any failure drops the whole stack so Undo
 * never applies a half-read snapshot list.
 */
internal fun decodeHistory(raw: String): List<ScoreSnapshot> = try {
    if (raw.isBlank()) {
        emptyList()
    } else {
        raw.split(";").map { token ->
            val parts = token.split(",")
            require(parts.size == 2)
            ScoreSnapshot(nousScore = parts[0].toInt(), euxScore = parts[1].toInt())
        }
    }
} catch (_: Exception) {
    emptyList()
}

private fun parseLayout(stored: String?): PhoneLayout =
    PhoneLayout.entries.find { it.name == stored } ?: PhoneLayout.ButtonsCenter
