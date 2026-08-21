package nl.siwoc.petanquecounter.app.ui

import nl.siwoc.petanquecounter.core.domain.Team

/**
 * Opens the 1–6 mène picker for [team].
 *
 * @property add True for a plus mène, false for minus.
 */
data class MeneRequest(
    val team: Team,
    val add: Boolean,
)
