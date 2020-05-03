package ke.co.infiware.gateway

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.html

/**
 *
 * @author Denis Gitonga
 */
@Configuration
class PlaygroundConfiguration(
        @Value("classpath:/graphql-playground.html") private val playgroundHtml: Resource
) {

    private fun body(serviceName: String = ""): String {

        val baseUrl = if (serviceName.isEmpty()) "" else "${serviceName}/"

        return playgroundHtml.inputStream.bufferedReader().use { reader ->
            reader.readText()
                    .replace("\${graphQLEndpoint}", baseUrl + "graphql")
                    .replace("\${subscriptionsEndpoint}", baseUrl + "subscriptions")
        }
    }

    @Bean
    fun playgroundRoute() = coRouter {
        GET("/playground") {

            val serviceName = it.queryParam("serviceName").orElse("")

            ok().html().bodyValueAndAwait(body(serviceName = serviceName))
        }
    }

}