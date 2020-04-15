package ke.co.infiware.uaa.security.configuration

import com.nimbusds.jwt.JWTClaimsSet
import ke.co.infiware.exceptions.utils.ExceptionUtils
import ke.co.infiware.uaa.security.jwt.BlueTokenService
import ke.co.infiware.uaa.security.models.IUserDto
import ke.co.infiware.uaa.security.models.InfiwarePrincipal
import ke.co.infiware.uaa.utils.TOKEN_PREFIX
import ke.co.infiware.uaa.utils.TOKEN_PREFIX_LENGTH
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import reactor.core.publisher.Mono

/**
 *
 * @author Denis Gitonga
 */
abstract class AbstractSecurityConfig<T : IUserDto> constructor(
        private val blueTokenService: BlueTokenService
) {

    private val log: Logger = LoggerFactory.getLogger(AbstractSecurityConfig::class.java)

    /**
     *
     */
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        log.debug("Configuring SecurityWebFilterChain...")


        formLogin(http = http) // Configure form login

        authorizeExchange(http = http) // configure authorization

        oauth2Login(http = http) // configure OAuth2 login

        // @formatter:off
        return http
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling()
                    .accessDeniedHandler{ _, exception -> Mono.error(exception) }
                    .authenticationEntryPoint { _, exception -> Mono.error(exception) }
                .and()
                    .cors()
                .and()
                    .csrf().disable()
                    .addFilterAt(tokenAuthenticationFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                .logout().disable()
                .build()
        // @formatter:on
    }

    /**
     * Override this to configure form login.
     */
    open fun formLogin(http: ServerHttpSecurity) {

    }

    /**
     * Override this to configure authorization
     */
    open fun authorizeExchange(http: ServerHttpSecurity) {

        http.authorizeExchange()
                .anyExchange().permitAll()

    }

    /**
     * Override this to configure oauth login
     */
    open fun oauth2Login(http: ServerHttpSecurity) {

    }

    open fun tokenAuthenticationFilter(): AuthenticationWebFilter {
        val filter = AuthenticationWebFilter(tokenAuthenticationManager())
        filter.setServerAuthenticationConverter(tokenAuthenticationConverter())
        filter.setAuthenticationFailureHandler { _, exception -> Mono.error(exception) }

        return filter
    }

    open fun tokenAuthenticationManager(): ReactiveAuthenticationManager {
        return ReactiveAuthenticationManager { authentication ->
            log.debug("Authenticating with token...")

            val token = authentication.credentials as? String
                    ?: throw BadCredentialsException(ExceptionUtils.getMessage(messageKey = "ke.co.infiware.uaa.wrong.audience"))
            val claims = blueTokenService.parseToken(token = token, aud = BlueTokenService.AUTH_AUDIENCE)
            val userDto = getUserDto(claims = claims)
            val user = if (userDto == null)
                fetchUser(username = claims.subject)
            else
                Mono.just(userDto)


            return@ReactiveAuthenticationManager user.map { InfiwarePrincipal(userDto = it) }
                    .doOnNext { it.eraseCredentials() }
                    .map { principal -> UsernamePasswordAuthenticationToken(principal, token, principal.authorities) }
        }
    }

    /**
     * Default behaviour is to throw error. To be overridden in auth service.
     *
     * @param username
     * @return
     */
    open fun fetchUser(username: String): Mono<T> {
        val message = ExceptionUtils.getMessage(messageKey = "ke.co.infiware.uaa.missingUserClaim")
        return Mono.error(BadCredentialsException(message))
    }

    open fun tokenAuthenticationConverter(): ServerAuthenticationConverter {
        return ServerAuthenticationConverter { exchange ->
            val authorization = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)

            if (authorization == null || authorization.startsWith(TOKEN_PREFIX).not())
                Mono.empty()
            else
                Mono.just(UsernamePasswordAuthenticationToken(null, authorization.substring(TOKEN_PREFIX_LENGTH)))
        }
    }

    private fun getUserDto(claims: JWTClaimsSet): T? {
        val userClaim = claims.getClaim(BlueTokenService.USER_CLAIM)
                as? String
                ?: return null
        return blueTokenService.deserialize(serializedObj = userClaim)
    }
}