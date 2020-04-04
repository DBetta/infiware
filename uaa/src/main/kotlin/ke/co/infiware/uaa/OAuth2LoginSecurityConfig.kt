package ke.co.infiware.uaa

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

/**
 *
 * @author Denis Gitonga
 */
@EnableWebFluxSecurity
class OAuth2LoginSecurityConfig {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {

        http.formLogin().disable()
        http.authorizeExchange { exchange ->
            exchange.anyExchange().authenticated()
        }
        http.oauth2Login()

        return http.build()
    }
}