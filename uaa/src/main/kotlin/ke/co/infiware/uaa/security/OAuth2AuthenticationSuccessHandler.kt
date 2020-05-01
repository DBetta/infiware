package ke.co.infiware.uaa.security

import ke.co.infiware.exceptions.utils.ExceptionUtils
import ke.co.infiware.uaa.configuration.UaaProperties
import ke.co.infiware.uaa.security.jwt.BlueTokenService
import ke.co.infiware.uaa.security.models.IUserDto
import ke.co.infiware.uaa.security.models.InfiwarePrincipal
import ke.co.infiware.uaa.useradministration.enities.InfiwareUser
import ke.co.infiware.uaa.useradministration.mappers.UserMapper
import ke.co.infiware.uaa.useradministration.repositories.InfiwareUserRepository
import ke.co.infiware.uaa.utils.AUTHORIZATION_REQUEST_COOKIE_NAME
import ke.co.infiware.uaa.utils.REDIRECT_URI_COOKIE_PARAM_NAME
import ke.co.infiware.uaa.utils.deleteCookies
import ke.co.infiware.uaa.utils.fetchCookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.StandardClaimNames
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.DefaultServerRedirectStrategy
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
@Component
class OAuth2AuthenticationSuccessHandler(
        private val blueTokenService: BlueTokenService,

        private val uaaProperties: UaaProperties,

        private val userDetailsService: InfiwareReactiveUserDetailsService,

        private val userMapper: UserMapper,

        private val passwordEncoder: PasswordEncoder,

        private val userRepository: InfiwareUserRepository
) : ServerAuthenticationSuccessHandler {

    private val redirectStrategy = DefaultServerRedirectStrategy()
    private val log: Logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler::class.java)

    override fun onAuthenticationSuccess(
            webFilterExchange: WebFilterExchange?,
            authentication: Authentication?): Mono<Void> = mono {

        val exchange = webFilterExchange?.exchange

        // we expect that context won't be empty
        val oauth2AuthenticationToken = ReactiveSecurityContextHolder.getContext()
                .awaitFirst().authentication as OAuth2AuthenticationToken

        val principal = buildPrincipal(oauth2User = oauth2AuthenticationToken.principal,
                registrationId = oauth2AuthenticationToken.authorizedClientRegistrationId)

        val userDto = principal.getCurrentUser()

        val token = getAuthToken(userDto = userDto)

        val targetUrl = getTargetUrl(exchange = exchange, shortLivedToken = token)

        val location = URI.create(targetUrl)

        return@mono redirectStrategy.sendRedirect(exchange, location).awaitFirstOrNull()
    }

    /**
     * Builds the security principal from the given userRequest.
     * Registers the user if not already registered
     */
    suspend fun buildPrincipal(oauth2User: OAuth2User, registrationId: String): InfiwarePrincipal {
        log.debug("building principal")

        val attributes = oauth2User.attributes
        val emailAddress = getOAuth2Email(registrationId = registrationId, attributes = attributes)
        ExceptionUtils.validate(
                valid = emailAddress != null,
                messageKey = "ke.co.infiware.uaa.oauth2EmailNeeded",
                args = *arrayOf(registrationId)).go()

        val emailVerified = getOAuth2AccountVerified(registrationId = registrationId, attributes = attributes)

        ExceptionUtils.validate(
                valid = emailVerified,
                messageKey = "ke.co.infiware.uaa.oauth2EmailNotVerified",
                args = *arrayOf(emailAddress)).go()

        val infiwareUser = userDetailsService.findByEmailAddress(emailAddress = emailAddress!!)
                ?: createUser(emailAddress = emailAddress, registrationId = registrationId, attributes = attributes)
        log.debug("successfully created/fetched user {}", infiwareUser.code)

        val userDto = userMapper.map(user = infiwareUser)
        return InfiwarePrincipal(
                userDto = userDto,
                attributes = attributes,
                name = oauth2User.name
        )
    }

    private suspend fun createUser(emailAddress: String, registrationId: String, attributes: Map<String, Any>): InfiwareUser {
        val defaultPassword = passwordEncoder.encode(UUID.randomUUID().toString())
        val infiwareUser = InfiwareUser(
                password = defaultPassword,
                emailAddress = emailAddress)

        val augmentedUser = fillAdditionalFields(user = infiwareUser, registrationId = registrationId, attributes = attributes)

        return withContext(Dispatchers.IO) {
            userRepository.save(augmentedUser)
        }
    }

    private fun fillAdditionalFields(user: InfiwareUser, registrationId: String, attributes: Map<String, Any>): InfiwareUser {

        return if (registrationId == "google" || registrationId == "facebook") {

            val firstName = attributes[StandardClaimNames.GIVEN_NAME] as? String
            val middleName = attributes[StandardClaimNames.MIDDLE_NAME] as? String
            val lastName = attributes[StandardClaimNames.FAMILY_NAME] as? String
            val photoUrl = attributes.getOrDefault(StandardClaimNames.PROFILE, attributes[StandardClaimNames.PICTURE]) as? String
            val providerId = attributes[StandardClaimNames.SUB] as String?

            user.copy(
                    firstName = firstName,
                    middleName = middleName,
                    lastName = lastName,
                    photoUrl = photoUrl,
                    provider = registrationId,
                    providerId = providerId
            )
        } else {
            user
        }
    }

    /**
     * Extracts the email id from user attributes received from OAuth2 provider, e.g. Google
     *
     */
    private fun getOAuth2Email(registrationId: String, attributes: Map<String, Any>): String? {
        log.debug("Getting ouath2 email from {}", registrationId)

        return attributes[StandardClaimNames.EMAIL] as? String
    }

    /**
     * Checks if the account at the OAuth2 provider is verified
     */
    private fun getOAuth2AccountVerified(registrationId: String, attributes: Map<String, Any>): Boolean {
        log.debug("Checking if account for {} is verified.", registrationId)

        return when (registrationId) {
            "facebook" -> true
            else -> (attributes[StandardClaimNames.EMAIL_VERIFIED] as? Boolean
                    ?: attributes["verified"] as? Boolean) == true

        }

    }

    private fun getAuthToken(userDto: IUserDto): String {
        return blueTokenService.createToken(
                aud = BlueTokenService.AUTH_AUDIENCE,
                sub = userDto.getUsername(),
                expirationMillis = uaaProperties.jwt.shortLivedMillis
        )
    }

    private fun getTargetUrl(exchange: ServerWebExchange?, shortLivedToken: String): String {
        val cookie = fetchCookie(exchange = exchange, cookieName = REDIRECT_URI_COOKIE_PARAM_NAME)
        val targetUrl = cookie?.value ?: uaaProperties.oauth2AuthenticationSuccessUrl

        deleteCookies(exchange = exchange, cookiesToDelete = *arrayOf(
                AUTHORIZATION_REQUEST_COOKIE_NAME,
                REDIRECT_URI_COOKIE_PARAM_NAME
        ))

        return targetUrl + shortLivedToken
    }
}
