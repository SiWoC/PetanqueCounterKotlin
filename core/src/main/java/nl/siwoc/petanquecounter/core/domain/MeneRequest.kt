package nl.siwoc.petanquecounter.core.domain

/**
 * Opens the 1–6 mène picker for [team]. Phone and Wear share this type.
 *
 * @property add True for a plus mène, false for minus.
 */
data class MeneRequest(
    val team: Team,
    val add: Boolean,
)
