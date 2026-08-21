package nl.siwoc.petanquecounter.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import nl.siwoc.petanquecounter.core.domain.GameState

/**
 * Device-local Preferences file for [GameState]. Phone and Wear each construct
 * their own instance; nothing here copies scores between processes.
 */
private val Context.scoreDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "score",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
)

/**
 * Loads and stores the current match from Preferences DataStore.
 *
 * ViewModels pass [Context.getApplicationContext] so every screen shares one
 * DataStore file. Tests can pass a [DataStore] directly.
 *
 * @property state Live [GameState]; missing keys become 0 / ButtonsCenter / empty history.
 */
class ScoreRepository internal constructor(
    private val dataStore: DataStore<Preferences>,
) {
    /**
     * Uses the app-wide `score` preferences file under the application context.
     */
    constructor(context: Context) : this(context.applicationContext.scoreDataStore)

    /**
     * Emits the stored match whenever Preferences change.
     */
    val state: Flow<GameState> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { prefs -> prefs.toGameState() }

    /**
     * Applies [transform] in one DataStore transaction so two quick taps cannot
     * overwrite each other with a stale snapshot.
     */
    suspend fun update(transform: (GameState) -> GameState) {
        dataStore.updateData { prefs ->
            val current = prefs.toGameState()
            val next = transform(current)
            if (next == current) prefs else next.toPreferences()
        }
    }
}
