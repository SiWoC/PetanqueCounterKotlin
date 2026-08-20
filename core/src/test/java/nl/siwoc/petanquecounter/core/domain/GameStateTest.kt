package nl.siwoc.petanquecounter.core.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStateTest {

    @Test
    fun applyMene_addsPointsAndPushesSnapshotFirst() {
        val start = GameState(nousScore = 4, euxScore = 2)
        val next = start.applyMene(Team.Nous, 3)

        assertEquals(7, next.nousScore)
        assertEquals(2, next.euxScore)
        assertEquals(listOf(ScoreSnapshot(nousScore = 4, euxScore = 2)), next.history)
    }

    @Test
    fun applyMene_rejectsDeltaOutsidePlusOrMinusOneToSix() {
        val start = GameState(nousScore = 4)
        assertSame(start, start.applyMene(Team.Nous, 0))
        assertSame(start, start.applyMene(Team.Nous, 7))
        assertSame(start, start.applyMene(Team.Nous, -7))
    }

    @Test
    fun applyMene_rejectsSubtractThatWouldGoBelowZero() {
        val start = GameState(nousScore = 3)
        assertSame(start, start.applyMene(Team.Nous, -4))
        assertSame(start, start.applyMene(Team.Nous, -6))
    }

    @Test
    fun applyMene_allowsSubtractUpToCurrentScore() {
        val start = GameState(nousScore = 3)
        val next = start.applyMene(Team.Nous, -3)

        assertEquals(0, next.nousScore)
        assertEquals(listOf(ScoreSnapshot(nousScore = 3, euxScore = 0)), next.history)
    }

    @Test
    fun undo_restoresLastSnapshot() {
        val afterTwo = GameState()
            .applyMene(Team.Nous, 2)
            .applyMene(Team.Eux, 1)

        val undone = afterTwo.undo()
        assertEquals(2, undone.nousScore)
        assertEquals(0, undone.euxScore)
        assertEquals(1, undone.history.size)

        val empty = undone.undo().undo()
        assertEquals(GameState(), empty)
        assertFalse(empty.canUndo())
        assertSame(empty, empty.undo())
    }

    @Test
    fun reset_zerosScoresAndClearsHistory_keepsLayout() {
        val playing = GameState(layout = PhoneLayout.ButtonsLeft)
            .applyMene(Team.Nous, 5)
            .applyMene(Team.Eux, 2)

        val reset = playing.reset()
        assertEquals(0, reset.nousScore)
        assertEquals(0, reset.euxScore)
        assertTrue(reset.history.isEmpty())
        assertEquals(PhoneLayout.ButtonsLeft, reset.layout)
    }

    @Test
    fun cycleLayout_walksRightLeftCenter() {
        val start = GameState(layout = PhoneLayout.ButtonsRight)
        val left = start.cycleLayout()
        val center = left.cycleLayout()
        val rightAgain = center.cycleLayout()

        assertEquals(PhoneLayout.ButtonsLeft, left.layout)
        assertEquals(PhoneLayout.ButtonsCenter, center.layout)
        assertEquals(PhoneLayout.ButtonsRight, rightAgain.layout)
    }

    @Test
    fun isWinReached_atThirteen_playCanContinue() {
        val twelve = GameState(nousScore = 12)
        assertFalse(twelve.isWinReached(Team.Nous))

        val thirteen = twelve.applyMene(Team.Nous, 1)
        assertTrue(thirteen.isWinReached(Team.Nous))
        assertFalse(thirteen.isWinReached(Team.Eux))

        val fourteen = thirteen.applyMene(Team.Nous, 1)
        assertEquals(14, fourteen.nousScore)
        assertTrue(fourteen.isWinReached(Team.Nous))
    }
}
