package ke.co.infiware.uaa.security.jwt

import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.DirectEncrypter
import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.JWEDecryptionKeySelector
import com.nimbusds.jose.proc.JWEKeySelector
import com.nimbusds.jose.proc.SimpleSecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.springframework.security.authentication.BadCredentialsException

/**
 * JWE Service
 *
 * References:
 *
 * https://connect2id.com/products/nimbus-jose-jwt/examples/jwe-with-shared-key
 * https://connect2id.com/products/nimbus-jose-jwt/examples/validating-jwt-access-tokens
 *
 * @author Denis Gitonga
 */
class JweTokenService(
        secret: String
) : AbstractTokenService(), GreenTokenService {

    private val encrypter: DirectEncrypter
    private val header = JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256)
    private val jwtProcessor: ConfigurableJWTProcessor<SimpleSecurityContext>
    init {
        val secretKey = secret.toByteArray()
        encrypter = DirectEncrypter(secretKey)
        jwtProcessor = DefaultJWTProcessor()

        // The JWE key source
        val jweKeySource: JWKSource<SimpleSecurityContext> = ImmutableSecret(secretKey)

        // Configure a key selector to handle the decryption phase
        val jweKeySelector: JWEKeySelector<SimpleSecurityContext> = JWEDecryptionKeySelector(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256, jweKeySource)

        jwtProcessor.setJWEKeySelector(jweKeySelector)
    }

    override fun createToken(aud: String, sub: String, expirationMillis: Long, claimMap: Map<String, Any>): String {
        val payload = createPayload(aud = aud, sub = sub, expirationMillis = expirationMillis, claimMap = claimMap)

        // Create the JWE object and encrypt it
        val jweObject = JWEObject(header, payload)
        jweObject.encrypt(encrypter)

        // Serialize to compact JOSE form...
        return jweObject.serialize()
    }

    override fun parseToken(token: String): JWTClaimsSet {
        return try {
            jwtProcessor.process(token, null)
        } catch (e: Throwable) {
            throw BadCredentialsException(e.message)
        }
    }
}