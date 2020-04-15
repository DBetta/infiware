package ke.co.infiware.uaa.security.jwt

import com.nimbusds.jwt.JWTClaimsSet
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
interface TokenService {


    fun createToken(aud: String, sub: String, expirationMillis: Long, claimMap: Map<String, Any>): String

    fun createToken(aud: String, sub: String, expirationMillis: Long): String

    fun parseToken(token: String, aud: String): JWTClaimsSet

    fun parseToken(token: String, aud: String, issuedAfter: Long): JWTClaimsSet

    fun <T> parseClaim(token: String, claim: String): T?

    /**
     * Serializes object to url compatible string
     */
    fun serialize(obj: Serializable): String {
        return Base64.getEncoder()
                .encodeToString(SerializationUtils.serialize(obj))
    }

    /**
     * Deserialize url string to obj
     */
    fun <T> deserialize(serializedObj: String): T {
        return SerializationUtils.deserialize(Base64.getDecoder().decode(serializedObj))
    }

    companion object {
        @JvmStatic
        val IAT = "lemon-iat"
    }
}