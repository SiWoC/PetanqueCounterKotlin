package nl.siwoc.petanquecounter.core.controller

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import nl.siwoc.petanquecounter.core.data.ScoreRepository
import nl.siwoc.petanquecounter.core.domain.GameState
import nl.siwoc.petanquecounter.core.domain.Team

/**
 * Applies match commands and exposes a single [state] stream for the UI.
 *
 * ViewModels construct this with the application [Context] and `viewModelScope`
 * so phone and Wear share the same rules without putting logic in composables.
 *
 * @property state Current match; starts as [GameState] defaults until DataStore emits.
 */
class ScoreController(
    private val repository: ScoreRepository,
    scope: CoroutineScope,
) {
    /**
     * Uses the device-local `score` store under the application context.
     */
    constructor(context: Context, scope: CoroutineScope) : this(
        ScoreRepository(context),
        scope,
    )

    val state: StateFlow<GameState> = repository.state.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = GameState(),
    )

    /**
     * Adds or subtracts one mène for [team]. No-ops when [delta] is bigger than 6
     * or the total would drop below 0.
     */
    suspend fun applyMene(team: Team, delta: Int) {
        repository.update { it.applyMene(team, delta) }
    }

    /**
     * Restores the last score snapshot, or no-ops when history is empty.
     */
    suspend fun undo() {
        repository.update { it.undo() }
    }

    /**
     * Zeros both totals and clears Undo. Layout is kept.
     */
    suspend fun reset() {
        repository.update { it.reset() }
    }

    /**
     * Advances phone ± placement through the Unity cycle.
     */
    suspend fun cycleLayout() {
        repository.update { it.cycleLayout() }
    }

    /**
     * True when [team] has at least 1 point, so a subtract mène overlay can open.
     */
    fun isMinusEnabled(team: Team): Boolean = state.value.score(team) > 0

    /**
     * True when Undo has a snapshot to restore.
     */
    fun canUndo(): Boolean = state.value.canUndo()

    /**
     * True when [team] has reached [GameState.WIN_SCORE]. Play can continue.
     */
    fun isWinReached(team: Team): Boolean = state.value.isWinReached(team)
}
