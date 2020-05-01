package ke.co.infiware.uaa.useradministration.services.impl

import ke.co.infiware.uaa.configuration.UaaProperties
import ke.co.infiware.uaa.security.jwt.BlueTokenService
import ke.co.infiware.uaa.useradministration.dtos.AuthDto
import ke.co.infiware.uaa.useradministration.dtos.AuthTokenDto
import ke.co.infiware.uaa.useradministration.services.AuthService
import ke.co.infiware.uaa.utils.getI18Message
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 *
 * @author Denis Gitonga
 */
@Service
class AuthServiceImpl(
        private val userDetailsService: ReactiveUserDetailsService,
        private val passwordEncoder: PasswordEncoder,
        private val blueTokenService: BlueTokenService,
        private val uaaProperties: UaaProperties
) : AuthService {
    override suspend fun authenticate(dto: AuthDto): AuthTokenDto {
        log.debug("Authenticating {}", dto.username)

        val userDetails = userDetailsService.findByUsername(dto.username).awaitFirst()

        val passwordMatches = passwordEncoder.matches(dto.password, userDetails.password)

        if (passwordMatches.not()) {
            val message = getI18Message(messageKey = "ke.co.infiware.uaa.BadCredentials")
            throw BadCredentialsException(message)
        }

        log.debug("Generating token for {}", userDetails.username)
        val token = blueTokenService.createToken(
                aud = BlueTokenService.AUTH_AUDIENCE,
                sub = userDetails.username,
                expirationMillis = uaaProperties.jwt.shortLivedMillis)

        return AuthTokenDto(
                tokenType = "Bearer",
                accessToken = token
        )
    }


    companion object {
        private val log: Logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)
    }
}