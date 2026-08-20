/**
 * Shared Play versioning for `:app` and `:wear` (same applicationId).
 *
 * `versionCode` = `targetSdk * 10_000_000 + type * 1_000_000 + major * 10_000 + minor * 100 + patch`
 * so phone stays below Wear and Play can pick the highest compatible APK.
 */
object PetanqueVersion {
    const val MAJOR = 2
    const val MINOR = 0
    const val PATCH = 0

    /** User-visible product version; same on phone and Wear. */
    val NAME: String = "$MAJOR.$MINOR.$PATCH"

    /** Phone APK type digit (must stay below [TYPE_WEAR]). */
    const val TYPE_PHONE = 0

    /** Wear APK type digit (must stay above [TYPE_PHONE] so watches prefer this APK). */
    const val TYPE_WEAR = 5

    /**
     * Play `versionCode` for this [targetSdk] and APK [type].
     *
     * @param targetSdk Android target API (1–99), kept in sync with `defaultConfig.targetSdk`.
     * @param type [TYPE_PHONE] or [TYPE_WEAR].
     */
    fun code(targetSdk: Int, type: Int): Int {
        require(targetSdk in 1..99) { "targetSdk must fit two digits: $targetSdk" }
        require(type in 0..9) { "type must fit one digit: $type" }
        require(MAJOR in 0..99) { "MAJOR must fit two digits: $MAJOR" }
        require(MINOR in 0..99) { "MINOR must fit two digits: $MINOR" }
        require(PATCH in 0..99) { "PATCH must fit two digits: $PATCH" }
        return targetSdk * 10_000_000 + type * 1_000_000 + MAJOR * 10_000 + MINOR * 100 + PATCH
    }
}
