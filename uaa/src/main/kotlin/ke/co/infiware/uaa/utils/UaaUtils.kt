package ke.co.infiware.uaa.utils

import ke.co.infiware.commons.utils.I18nUtils
import org.apache.commons.lang3.SerializationUtils
import org.springframework.http.HttpCookie
import org.springframework.http.ResponseCookie
import org.springframework.web.server.ServerWebExchange
import java.io.Serializable
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
const val TOKEN_PREFIX = "Bearer "
const val TOKEN_PREFIX_LENGTH = 7
const val TOKEN_RESPONSE_HEADER_NAME = "Infiware-Authorization"
const val AUTHORIZATION_REQUEST_COOKIE_NAME = "infiware_oauth2_authorization_request"
const val REDIRECT_URI_COOKIE_PARAM_NAME = "lemon_redirect_uri";

fun getI18Message(messageKey: String, vararg args: Any): String? {
    return I18nUtils.getMessage(messageKey, args)
}

/**
 * Get cookie by name from [ServerWebExchange]
 */
fun fetchCookie(exchange: ServerWebExchange?, cookieName: String): HttpCookie? {
    return exchange?.request?.cookies?.getFirst(cookieName)
}

/**
 *
 */
fun deleteCookies(exchange: ServerWebExchange?, vararg cookiesToDelete: String) {

    val cookies = exchange?.request?.cookies
    val responseCookies = exchange?.response?.cookies

    for (cookieName in cookiesToDelete) {
        if (cookies?.getFirst(cookieName) != null) {
            val cookie = ResponseCookie.from(cookieName, "")
                    .path("/")
                    .maxAge(0L)
                    .build()

            responseCookies?.put(cookieName, listOf(cookie))
        }
    }
}

/**
 * Serializes object to url compatible string
 */
fun serialize(obj: Serializable): String {
    return Base64.getEncoder()
            .encodeToString(SerializationUtils.serialize(obj))
}

/**
 * Deserialize url string to obj
 */
fun <T> deserialize(serializedObj: String): T {
    return SerializationUtils.deserialize(Base64.getDecoder().decode(serializedObj))
}
