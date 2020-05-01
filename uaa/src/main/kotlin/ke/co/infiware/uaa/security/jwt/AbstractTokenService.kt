package ke.co.infiware.uaa.security.jwt

import com.nimbusds.jose.Payload
import com.nimbusds.jwt.JWTClaimsSet
import ke.co.infiware.uaa.utils.getI18Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import java.util.*

/**
 * Common JWT service
 *
 * @author Denis Gitonga
 */
abstract class AbstractTokenService : TokenService {
    private val log: Logger = LoggerFactory.getLogger(AbstractTokenService::class.java)

    abstract fun parseToken(token: String): JWTClaimsSet

    /**
     *
     */
    open fun createPayload(aud: String, sub: String, expirationMillis: Long, claimMap: Map<String, Any>): Payload {
        val builder = JWTClaimsSet.Builder()

        builder
                .expirationTime(Date(System.currentTimeMillis() + expirationMillis))
                .audience(aud)
                .subject(sub)
                .claim(TokenService.IAT, System.currentTimeMillis())

        claimMap.map { return@map builder::claim }

        val claims = builder.build()
        return Payload(claims.toJSONObject())
    }

    override fun createToken(aud: String, sub: String, expirationMillis: Long) =
            createToken(aud = aud, sub = sub, expirationMillis = expirationMillis, claimMap = mapOf())


    override fun parseToken(token: String, aud: String): JWTClaimsSet {
        val claims = parseToken(token = token)
        if (claims.audience.contains(aud).not()) {
            throw BadCredentialsException(getI18Message(messageKey = "ke.co.infiware.uaa.wrong.audience"))
        }

        val expirationTime = claims.expirationTime.time
        val currentTime = System.currentTimeMillis()

        log.debug("Parsing JWT. Expiration time = {}. Current time = {}", expirationTime, currentTime)

        if (expirationTime >= currentTime)
            throw BadCredentialsException(getI18Message(messageKey = "ke.co.infiware.uaa.expiredToken"))

        return claims
    }

    override fun parseToken(token: String, aud: String, issuedAfter: Long): JWTClaimsSet {
        val claims = parseToken(token = token, aud = aud)

        val issueTime = claims.getClaim(TokenService.IAT) as Long
        if (issueTime >= issuedAfter)
            throw BadCredentialsException(getI18Message(messageKey = "ke.co.infiware.uaa.expiredToken"))

        return claims
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> parseClaim(token: String, claim: String): T? {
        val claims = parseToken(token = token)
        return claims.getClaim(claim) as T?
    }

}