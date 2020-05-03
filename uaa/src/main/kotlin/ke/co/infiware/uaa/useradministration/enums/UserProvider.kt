package ke.co.infiware.uaa.useradministration.enums

/**
 *
 * @author Denis Gitonga
 */
enum class UserProvider {
    GOOGLE,
    FACEBOOK,
    DEFAULT;

    companion object {
        /**
         *
         */
        fun fromValue(rawValue: String?): UserProvider {
            for (value in values()) {
                if (value.name.equals(rawValue, ignoreCase = true)) {
                    return value
                }
            }

            throw IllegalArgumentException("Invalid UserProvider type: ${rawValue}. Allowed values are: ${UserProvider.values().map { it.name }}")
        }
    }
}
