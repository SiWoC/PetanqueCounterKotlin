package nl.siwoc.petanquecounter.core.data

import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import nl.siwoc.petanquecounter.core.domain.GameState
import nl.siwoc.petanquecounter.core.domain.PhoneLayout
import nl.siwoc.petanquecounter.core.domain.ScoreSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScorePreferencesTest {

    @Test
    fun emptyPreferences_mapToFreshDefaults() {
        val state = emptyPreferences().toGameState()

        assertEquals(0, state.nousScore)
        assertEquals(0, state.euxScore)
        assertEquals(PhoneLayout.ButtonsCenter, state.layout)
        assertTrue(state.history.isEmpty())
    }

    @Test
    fun roundTrip_preservesScoresLayoutAndHistory() {
        val original = GameState(
            nousScore = 7,
            euxScore = 4,
            layout = PhoneLayout.ButtonsLeft,
            history = listOf(
                ScoreSnapshot(nousScore = 0, euxScore = 0),
                ScoreSnapshot(nousScore = 5, euxScore = 0),
            ),
        )

        val restored = original.toPreferences().toGameState()
        assertEquals(original, restored)
    }

    @Test
    fun unknownLayout_fallsBackToButtonsCenter() {
        val prefs = mutablePreferencesOf(
            ScorePreferences.LAYOUT to "NotALayout",
        )

        assertEquals(PhoneLayout.ButtonsCenter, prefs.toGameState().layout)
    }

    @Test
    fun encodeHistory_isCompactOldestFirst() {
        val encoded = encodeHistory(
            listOf(
                ScoreSnapshot(nousScore = 1, euxScore = 2),
                ScoreSnapshot(nousScore = 4, euxScore = 2),
            ),
        )

        assertEquals("1,2;4,2", encoded)
    }

    @Test
    fun decodeHistory_malformedReturnsEmpty() {
        assertTrue(decodeHistory("1,2;bad;3,4").isEmpty())
        assertTrue(decodeHistory("1,2,3").isEmpty())
    }

    @Test
    fun decodeHistory_blankIsEmpty() {
        assertTrue(decodeHistory("").isEmpty())
        assertTrue(decodeHistory("   ").isEmpty())
    }
}
