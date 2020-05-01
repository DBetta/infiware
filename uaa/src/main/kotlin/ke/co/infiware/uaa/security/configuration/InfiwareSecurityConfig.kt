package ke.co.infiware.uaa.security.configuration

import com.nimbusds.jwt.JWTClaimsSet
import ke.co.infiware.uaa.configuration.UaaProperties
import ke.co.infiware.uaa.security.CookieServerOAuth2AuthorizedClientRepository
import ke.co.infiware.uaa.security.InfiwareReactiveUserDetailsService
import ke.co.infiware.uaa.security.OAuth2AuthenticationSuccessHandler
import ke.co.infiware.uaa.security.jwt.BlueTokenService
import ke.co.infiware.uaa.useradministration.dtos.UserDto
import ke.co.infiware.uaa.useradministration.mappers.UserMapper
import ke.co.infiware.uaa.utils.AUTHORIZATION_REQUEST_COOKIE_NAME
import ke.co.infiware.uaa.utils.REDIRECT_URI_COOKIE_PARAM_NAME
import ke.co.infiware.uaa.utils.deleteCookies
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.WebFilterChainServerAuthenticationSuccessHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import reactor.core.publisher.Mono

/**
 *
 * @author Denis Gitonga
 */
class InfiwareSecurityConfig(
        blueTokenService: BlueTokenService,

        private val uaaProperties: UaaProperties,

        private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,

        private val userDetailsService: InfiwareReactiveUserDetailsService,

        private val userMapper: UserMapper
) : AbstractSecurityConfig<UserDto>(blueTokenService = blueTokenService) {

    /**
     * Configure formLogin
     */
    override fun formLogin(http: ServerHttpSecurity) {

        // disable httpBasic
        http.httpBasic().disable()

        // disable formLogin
        http.formLogin().disable()

               /*.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .loginPage(loginPage()) // Should be "/login" by default, but not providing that overwrites our AuthenticationFailureHandler, because this is called later
                .authenticationFailureHandler { _, exception -> Mono.error(exception) }
                .authenticationSuccessHandler(WebFilterChainServerAuthenticationSuccessHandler())*/
    }

    /**
     * Configure OAuth2 login
     */
    override fun oauth2Login(http: ServerHttpSecurity) {
        http.oauth2Login()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizedClientRepository(CookieServerOAuth2AuthorizedClientRepository(uaaProperties = uaaProperties))
                .authenticationSuccessHandler(oAuth2AuthenticationSuccessHandler)
                .authenticationFailureHandler { filter, exception ->
                    onOauth2AuthenticationFailure(filter, exception)
                }
    }

    /**
     * Override this to change login URL
     */
    private fun loginPage(): String {
        val loginUrl = uaaProperties.loginUrl
        log.debug("The loginUrl is: {}", loginUrl)
        return loginUrl
    }

    override fun fetchUser(claims: JWTClaimsSet): Mono<UserDto> = mono {

        val username = claims.subject

        val user = userDetailsService.findUserByUsername(username)
                ?: throw UsernameNotFoundException(username)

        // TODO: validate token has not expired

        return@mono userMapper.map(user = user)

    }


    private fun onOauth2AuthenticationFailure(
            webFilterExchange: WebFilterExchange,
            exception: AuthenticationException): Mono<Void> {

        val exchange = webFilterExchange.exchange

        deleteCookies(exchange = exchange, cookiesToDelete = *arrayOf(
                AUTHORIZATION_REQUEST_COOKIE_NAME, REDIRECT_URI_COOKIE_PARAM_NAME
        ))

        return Mono.error(exception)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(InfiwareSecurityConfig::class.java)
    }
}