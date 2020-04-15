package ke.co.infiware.uaa.security.jwt

/**
 *
 * @author Denis Gitonga
 */
interface GreenTokenService : TokenService {
    val VERIFY_AUDIENCE: String
        get() = "verify"
    val FORGOT_PASSWORD_AUDIENCE: String
        get() = "forgot-password"
    val CHANGE_EMAIL_AUDIENCE: String
        get() = "change-email"

}