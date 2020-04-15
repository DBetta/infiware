package ke.co.infiware.uaa.security.jwt

/**
 *
 * @author Denis Gitonga
 */
interface BlueTokenService : TokenService {
    companion object {
        @JvmStatic
        val USER_CLAIM = "user"

        @JvmStatic
        val AUTH_AUDIENCE = "auth"
    }

}