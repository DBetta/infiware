package ke.co.infiware.uaa

import ke.co.infiware.uaa.configuration.UaaProperties
import ke.co.infiware.uaa.security.jwt.BlueTokenService
import ke.co.infiware.uaa.security.jwt.GreenTokenService
import ke.co.infiware.uaa.security.jwt.JweTokenService
import ke.co.infiware.uaa.security.jwt.JwsTokenService
import ke.co.infiware.uaa.useradministration.dtos.UserDto
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
@EnableConfigurationProperties(UaaProperties::class, LiquibaseProperties::class)
class UaaApplication {
    @Bean
    @LoadBalanced
    fun webClientBuilder(): WebClient.Builder? {
        return WebClient.builder()
    }

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
}

fun main(args: Array<String>) {
    runApplication<UaaApplication>(*args)
}
