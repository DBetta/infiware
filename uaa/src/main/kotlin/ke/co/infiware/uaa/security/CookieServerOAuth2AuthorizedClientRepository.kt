package ke.co.infiware.uaa.security

import ke.co.infiware.uaa.configuration.UaaProperties
import ke.co.infiware.uaa.utils.*
import kotlinx.coroutines.reactor.mono
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 *
 * @author Denis Gitonga
 */
class CookieServerOAuth2AuthorizedClientRepository(
        private val uaaProperties: UaaProperties
) : ServerOAuth2AuthorizedClientRepository {

    private val log: Logger = LoggerFactory.getLogger(CookieServerOAuth2AuthorizedClientRepository::class.java)

    @Suppress("UNCHECKED_CAST")
    override fun <T : OAuth2AuthorizedClient?> loadAuthorizedClient(
            clientRegistrationId: String?,
            principal: Authentication?,
            exchange: ServerWebExchange?): Mono<T> = mono {

        log.debug("Loading authorized clients for clientRegistrationId {}, principal {}, and exchange {}", clientRegistrationId, principal, exchange)

        val httpCookie = fetchCookie(exchange = exchange, cookieName = AUTHORIZATION_REQUEST_COOKIE_NAME)

        val serializedObj = httpCookie?.value ?: return@mono null

        return@mono deserialize<T>(serializedObj = serializedObj)
    }

    override fun removeAuthorizedClient(
            clientRegistrationId: String?,
            principal: Authentication?,
            exchange: ServerWebExchange?): Mono<Void?> = mono {

        log.debug("Deleting authorised client for clientRegistrationId {}, principal {}, and exchange {}", clientRegistrationId, principal, exchange)

        deleteCookies(exchange = exchange, cookiesToDelete = *arrayOf(AUTHORIZATION_REQUEST_COOKIE_NAME))

        return@mono null
    }

    override fun saveAuthorizedClient(
            authorizedClient: OAuth2AuthorizedClient?,
            principal: Authentication?,
            exchange: ServerWebExchange?): Mono<Void?> = mono {

        log.debug("Saving authorized clients {}, for principal {}, and exchange {}", authorizedClient, principal, exchange)

        val response = exchange?.response

        if (authorizedClient == null) {
            deleteCookies(exchange = exchange, cookiesToDelete = *arrayOf(AUTHORIZATION_REQUEST_COOKIE_NAME, REDIRECT_URI_COOKIE_PARAM_NAME))

            return@mono null
        }

        val cookieExpirySecs = uaaProperties.jwt.shortLivedMillis

        var cookie = ResponseCookie
                .from(AUTHORIZATION_REQUEST_COOKIE_NAME, serialize(obj = authorizedClient))
                .path("/")
                .httpOnly(true)
                .maxAge(cookieExpirySecs)
                .build()
        response?.addCookie(cookie)

        val redirectUri = exchange?.request
                ?.queryParams?.getFirst(REDIRECT_URI_COOKIE_PARAM_NAME)

        if (StringUtils.isNotBlank(redirectUri)) {
            cookie = ResponseCookie
                    .from(REDIRECT_URI_COOKIE_PARAM_NAME, redirectUri!!)
                    .path("/")
                    .httpOnly(true)
                    .maxAge(cookieExpirySecs)
                    .build()

            response?.addCookie(cookie)
        }

        return@mono null
    }
}