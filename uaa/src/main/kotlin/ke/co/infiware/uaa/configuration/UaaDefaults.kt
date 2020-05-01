package ke.co.infiware.uaa.configuration

import ke.co.infiware.uaa.utils.TOKEN_RESPONSE_HEADER_NAME
import org.springframework.http.HttpHeaders

/**
 *
 * @author Denis Gitonga
 */
class UaaDefaults {

   companion object{
       const val loginUrl: String = "/api/login"
   }

    class Cors {
        companion object {
            val allowedOrigins: List<String> = emptyList()

            val allowedMethods = listOf("GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "OPTIONS", "PATCH")

            val allowedHeaders = listOf(
                    "Accept",
                    "Accept-Encoding",
                    "Accept-Language",
                    "Cache-Control",
                    "Connection",
                    "Content-Length",
                    "Content-Type",
                    "Cookie",
                    "Host",
                    "Origin",
                    "Pragma",
                    "Referer",
                    "User-Agent",
                    "x-requested-with",
                    HttpHeaders.AUTHORIZATION
            )

            val exposedHeaders = listOf(
                    "Cache-Control",
                    "Connection",
                    "Content-Type",
                    "Date",
                    "Expires",
                    "Pragma",
                    "Server",
                    "Set-Cookie",
                    "Transfer-Encoding",
                    "X-Content-Type-Options",
                    "X-XSS-Protection",
                    "X-Frame-Options",
                    "X-Application-Context",
                    TOKEN_RESPONSE_HEADER_NAME
            )

            const val maxAge = 3_600L
        }
    }

    class Jwt {
        companion object {
            val secret = ""

            val expirationMillis = 864_000_000L // 10 days

            val shortLivedMillis = 120_000L // Two minutes
        }
    }
}