package nl.siwoc.petanquecounter.wear

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nl.siwoc.petanquecounter.core.controller.ScoreController
import nl.siwoc.petanquecounter.core.domain.GameState
import nl.siwoc.petanquecounter.core.domain.Team

/**
 * Owns [ScoreController] for the Wear UI. Composables collect [state] and call
 * the command methods; they do not talk to DataStore.
 */
class ScoreViewModel(application: Application) : AndroidViewModel(application) {
    private val controller = ScoreController(application, viewModelScope)

    /** Live match; starts at [GameState] defaults until DataStore emits. */
    val state: StateFlow<GameState> = controller.state

    /**
     * Applies one mène of [delta] points to [team].
     */
    fun applyMene(team: Team, delta: Int) {
        viewModelScope.launch { controller.applyMene(team, delta) }
    }

    /**
     * Restores the last score snapshot.
     */
    fun undo() {
        viewModelScope.launch { controller.undo() }
    }

    /**
     * Zeros both totals and clears Undo. Layout is kept.
     */
    fun reset() {
        viewModelScope.launch { controller.reset() }
    }
}
