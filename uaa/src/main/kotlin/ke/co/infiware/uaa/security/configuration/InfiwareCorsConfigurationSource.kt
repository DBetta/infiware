package ke.co.infiware.uaa.security.configuration

import ke.co.infiware.uaa.configuration.UaaProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.server.ServerWebExchange

/**
 * CORS Configuration
 *
 * @author Denis Gitonga
 */
class InfiwareCorsConfigurationSource(
        private val uaaProperties: UaaProperties
) : CorsConfigurationSource {

    override fun getCorsConfiguration(exchange: ServerWebExchange): CorsConfiguration {
        val cors = uaaProperties.cors
        val config = CorsConfiguration()

        config.allowCredentials = true
        config.allowedMethods = cors.allowedMethods
        config.allowedHeaders = cors.allowedHeaders
        config.allowedOrigins = cors.allowedOrigins
        config.exposedHeaders = cors.exposedHeaders
        config.maxAge = cors.maxAge

        return config
    }
}