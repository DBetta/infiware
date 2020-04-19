package ke.co.infiware.uaa.security.models

/**
 *
 * @author Denis Gitonga
 */
sealed class AuthUsername {
    data class EmailAddress(val emailAddress: String): AuthUsername()

    data class PhoneNumber(val phoneNumber: String): AuthUsername()

    data class Username(val username: String): AuthUsername()
}