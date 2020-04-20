package ke.co.infiware.uaa.configuration

import ke.co.infiware.uaa.security.InfiwareReactiveUserDetailsService
import ke.co.infiware.uaa.security.OAuth2AuthenticationSuccessHandler
import ke.co.infiware.uaa.security.configuration.InfiwareSecurityConfig
import ke.co.infiware.uaa.security.jwt.BlueTokenService
import ke.co.infiware.uaa.security.jwt.GreenTokenService
import ke.co.infiware.uaa.security.jwt.JweTokenService
import ke.co.infiware.uaa.security.jwt.JwsTokenService
import ke.co.infiware.uaa.useradministration.mappers.UserMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

/**
 *
 * @author Denis Gitonga
 */
@Configuration
class SecurityConfig {

    @Bean
    fun blueTokenService(uaaProperties: UaaProperties): BlueTokenService {
        return JwsTokenService(secret = uaaProperties.jwt.secret)
    }

    @Bean
    fun greenTokenService(uaaProperties: UaaProperties): GreenTokenService {
        return JweTokenService(secret = uaaProperties.jwt.secret)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun springSecurityFilterChain(
            http: ServerHttpSecurity,
            blueTokenService: BlueTokenService,
            oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
            uaaProperties: UaaProperties,
            userDetailsService: InfiwareReactiveUserDetailsService,
            userMapper: UserMapper
    ): SecurityWebFilterChain {

        val securityConfig = InfiwareSecurityConfig(
                blueTokenService = blueTokenService,
                oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler,
                uaaProperties = uaaProperties,
                userDetailsService = userDetailsService,
                userMapper = userMapper
        )

        return securityConfig.springSecurityFilterChain(http = http)
    }

}