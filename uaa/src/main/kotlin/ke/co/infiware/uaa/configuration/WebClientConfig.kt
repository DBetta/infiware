package ke.co.infiware.uaa.configuration

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 *
 * @author Denis Gitonga
 */
@Configuration
class WebClientConfig {

    @Bean
    @LoadBalanced
    fun webClientBuilder(): WebClient.Builder? {
        return WebClient.builder()
    }
}