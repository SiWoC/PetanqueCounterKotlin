package nl.siwoc.petanquecounter.core.domain

/**
 * Where the phone ± controls sit, so the board can be used one-handed.
 *
 * Order matches the Unity layouts in Temp `app-main0/1/2`.
 */
enum class PhoneLayout {
    ButtonsRight,
    ButtonsLeft,
    ButtonsCenter,
    ;

    /**
     * Next layout in the Unity cycle: right → left → center → right.
     */
    fun next(): PhoneLayout = when (this) {
        ButtonsRight -> ButtonsLeft
        ButtonsLeft -> ButtonsCenter
        ButtonsCenter -> ButtonsRight
    }
}
