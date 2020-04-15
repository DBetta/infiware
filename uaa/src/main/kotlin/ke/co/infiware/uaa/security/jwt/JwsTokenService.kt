package ke.co.infiware.uaa.security.jwt

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import ke.co.infiware.uaa.security.jwt.AbstractTokenService
import ke.co.infiware.uaa.security.jwt.BlueTokenService
import org.springframework.security.authentication.BadCredentialsException

/**
 *
 * @author Denis Gitonga
 */
class JwsTokenService internal constructor(
        secret: String
) : AbstractTokenService(), BlueTokenService {

    private val signer: JWSSigner
    private val verifier: JWSVerifier

    init {
        signer = MACSigner(secret)
        verifier = MACVerifier(secret)
    }

    override fun createToken(aud: String, sub: String, expirationMillis: Long, claimMap: Map<String, Any>): String {
        val payload = createPayload(aud = aud, sub = sub, expirationMillis = expirationMillis, claimMap = claimMap)

        // Prepare JWS object
        val jwsObject = JWSObject(JWSHeader(JWSAlgorithm.HS256), payload)

        // Apply the HMAC
        jwsObject.sign(signer)

        // To serialize to compact form, produces something like
        // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
        return jwsObject.serialize()
    }

    override fun parseToken(token: String): JWTClaimsSet {
        val jwsObject = JWSObject.parse(token)
        if (jwsObject.verify(verifier))
            return JWTClaimsSet.parse(jwsObject.payload.toJSONObject())

        throw BadCredentialsException("JWS verification failed!")
    }
}