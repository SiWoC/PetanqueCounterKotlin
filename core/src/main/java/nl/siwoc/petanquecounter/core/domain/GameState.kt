package nl.siwoc.petanquecounter.core.domain

/**
 * Match totals and phone-control placement.
 *
 * [history] stores score snapshots only, so Undo reverts points without
 * walking back the user's layout choice.
 *
 * @property nousScore Points for [Team.Nous].
 * @property euxScore Points for [Team.Eux].
 * @property layout Phone ± button placement.
 * @property history Previous score pairs, oldest first; the last entry is Undo.
 */
data class GameState(
    val nousScore: Int = 0,
    val euxScore: Int = 0,
    val layout: PhoneLayout = PhoneLayout.ButtonsCenter,
    val history: List<ScoreSnapshot> = emptyList(),
) {
    /**
     * Current total for [team].
     */
    fun score(team: Team): Int = when (team) {
        Team.Nous -> nousScore
        Team.Eux -> euxScore
    }

    /**
     * True when [team] has reached the usual finishing line. Play can continue.
     */
    fun isWinReached(team: Team): Boolean = score(team) >= WIN_SCORE

    /**
     * True when at least one mène can be undone.
     */
    fun canUndo(): Boolean = history.isNotEmpty()

    /**
     * Applies one mène of [delta] points to [team].
     *
     * Pushes a snapshot first so [undo] can restore the previous totals.
     * Returns this instance unchanged when [delta] is not in ±1..±6 or the
     * result would be below 0.
     */
    fun applyMene(team: Team, delta: Int): GameState {
        if (delta !in MIN_MENE_DELTA..-1 && delta !in 1..MAX_MENE_DELTA) {
            return this
        }
        val next = score(team) + delta
        if (next < 0) {
            return this
        }
        val snapshot = ScoreSnapshot(nousScore = nousScore, euxScore = euxScore)
        return when (team) {
            Team.Nous -> copy(nousScore = next, history = history + snapshot)
            Team.Eux -> copy(euxScore = next, history = history + snapshot)
        }
    }

    /**
     * Restores the last snapshot, or returns this instance when history is empty.
     */
    fun undo(): GameState {
        val previous = history.lastOrNull() ?: return this
        return copy(
            nousScore = previous.nousScore,
            euxScore = previous.euxScore,
            history = history.dropLast(1),
        )
    }

    /**
     * Sets both totals to 0 and clears Undo. Layout is kept as a preference.
     */
    fun reset(): GameState = copy(
        nousScore = 0,
        euxScore = 0,
        history = emptyList(),
    )

    /**
     * Advances [layout] through the three phone placements.
     */
    fun cycleLayout(): GameState = copy(layout = layout.next())

    companion object {
        /**
         * Usual match target. The score turns gold in the UI; the match is not locked.
         */
        const val WIN_SCORE = 13

        /** Smallest allowed subtract-mène size (most negative). */
        const val MIN_MENE_DELTA = -6

        /** Largest allowed add-mène size. */
        const val MAX_MENE_DELTA = 6
    }
}

/**
 * Score pair stored for Undo. Layout is omitted so a layout change is not undone.
 *
 * @property nousScore [GameState.nousScore] at snapshot time.
 * @property euxScore [GameState.euxScore] at snapshot time.
 */
data class ScoreSnapshot(
    val nousScore: Int,
    val euxScore: Int,
)
