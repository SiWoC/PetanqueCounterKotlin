package nl.siwoc.petanquecounter.core.controller

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import nl.siwoc.petanquecounter.core.data.ScoreRepository
import nl.siwoc.petanquecounter.core.data.toPreferences
import nl.siwoc.petanquecounter.core.domain.GameState
import nl.siwoc.petanquecounter.core.domain.PhoneLayout
import nl.siwoc.petanquecounter.core.domain.Team
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScoreControllerTest {

    @Test
    fun applyMene_updatesStateAndEnablesUndo() = runTest {
        val controller = controller()

        controller.applyMene(Team.Nous, 3)
        val state = controller.awaitNous(3)

        assertEquals(3, state.nousScore)
        assertEquals(0, state.euxScore)
        assertTrue(controller.canUndo())
        assertTrue(controller.isMinusEnabled(Team.Nous))
        assertFalse(controller.isMinusEnabled(Team.Eux))
    }

    @Test
    fun applyMene_rejectsDeltaOutsidePlusOrMinusOneToSix() = runTest {
        val controller = controller()

        controller.applyMene(Team.Nous, 0)
        controller.applyMene(Team.Nous, 7)
        controller.applyMene(Team.Nous, -7)

        assertEquals(GameState(), controller.state.value)
        assertFalse(controller.canUndo())
    }

    @Test
    fun applyMene_rejectsSubtractThatWouldGoBelowZero() = runTest {
        val controller = controller(GameState(nousScore = 3))

        controller.applyMene(Team.Nous, -4)

        assertEquals(3, controller.awaitNous(3).nousScore)
        assertFalse(controller.canUndo())
        assertTrue(controller.isMinusEnabled(Team.Nous))
    }

    @Test
    fun undo_restoresLastSnapshot() = runTest {
        val controller = controller()
        controller.applyMene(Team.Nous, 2)
        controller.applyMene(Team.Eux, 1)

        controller.undo()
        val undone = controller.awaitEux(0)
        assertEquals(2, undone.nousScore)
        assertEquals(0, undone.euxScore)
        assertTrue(controller.canUndo())

        controller.undo()
        controller.undo()
        val empty = controller.awaitNous(0)
        assertEquals(GameState(), empty)
        assertFalse(controller.canUndo())
    }

    @Test
    fun reset_zerosScoresAndClearsHistory_keepsLayout() = runTest {
        val controller = controller(GameState(layout = PhoneLayout.ButtonsLeft))
        controller.applyMene(Team.Nous, 5)
        controller.applyMene(Team.Eux, 2)

        controller.reset()
        val reset = controller.awaitNous(0)

        assertEquals(0, reset.nousScore)
        assertEquals(0, reset.euxScore)
        assertTrue(reset.history.isEmpty())
        assertEquals(PhoneLayout.ButtonsLeft, reset.layout)
        assertFalse(controller.canUndo())
        assertFalse(controller.isMinusEnabled(Team.Nous))
    }

    @Test
    fun cycleLayout_walksRightLeftCenter() = runTest {
        val controller = controller(GameState(layout = PhoneLayout.ButtonsRight))

        controller.cycleLayout()
        assertEquals(PhoneLayout.ButtonsLeft, controller.awaitLayout(PhoneLayout.ButtonsLeft).layout)

        controller.cycleLayout()
        assertEquals(
            PhoneLayout.ButtonsCenter,
            controller.awaitLayout(PhoneLayout.ButtonsCenter).layout,
        )

        controller.cycleLayout()
        assertEquals(
            PhoneLayout.ButtonsRight,
            controller.awaitLayout(PhoneLayout.ButtonsRight).layout,
        )
    }

    @Test
    fun isWinReached_atThirteen_playCanContinue() = runTest {
        val controller = controller(GameState(nousScore = 12))
        assertFalse(controller.isWinReached(Team.Nous))

        controller.applyMene(Team.Nous, 1)
        assertTrue(controller.awaitNous(13).let { controller.isWinReached(Team.Nous) })
        assertFalse(controller.isWinReached(Team.Eux))

        controller.applyMene(Team.Nous, 1)
        assertEquals(14, controller.awaitNous(14).nousScore)
        assertTrue(controller.isWinReached(Team.Nous))
    }

    private fun TestScope.controller(initial: GameState = GameState()): ScoreController {
        val repository = ScoreRepository(InMemoryDataStore(initial.toPreferences()))
        val scope: CoroutineScope =
            backgroundScope + UnconfinedTestDispatcher(testScheduler)
        return ScoreController(repository, scope)
    }

    private suspend fun ScoreController.awaitNous(score: Int): GameState =
        state.first { it.nousScore == score }

    private suspend fun ScoreController.awaitEux(score: Int): GameState =
        state.first { it.euxScore == score }

    private suspend fun ScoreController.awaitLayout(layout: PhoneLayout): GameState =
        state.first { it.layout == layout }
}

/**
 * In-process [DataStore] so controller tests do not need a device or a file.
 */
private class InMemoryDataStore(
    initial: Preferences = emptyPreferences(),
) : DataStore<Preferences> {
    private val mutex = Mutex()
    private val backing = MutableStateFlow(initial)

    override val data: Flow<Preferences> = backing

    override suspend fun updateData(
        transform: suspend (t: Preferences) -> Preferences,
    ): Preferences = mutex.withLock {
        val next = transform(backing.value)
        backing.value = next
        next
    }
}
