package ke.co.infiware.uaa.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 *
 * @author Denis Gitonga
 */
@ConstructorBinding
@ConfigurationProperties("infiware.uaa")
data class UaaProperties(

        /**
         * The default URL to redirect to after
         * a user logs in using OAuth2/OpenIDConnect
         */
        val oauth2AuthenticationSuccessUrl: String? = "http://localhost:9000/social-login-success?token=",

        /**
         * URL of the login endpoint
         * e.g. POST /api/login
         */
        val loginUrl: String = UaaDefaults.loginUrl,

        /**
         * CORS related properties
         */
        val cors: Cors = Cors(),

        /**
         * JWT token generation related properties
         */
        val jwt: Jwt = Jwt()
) {

    data class Cors(
            /**
             * Comma separated whitelisted URLs for CORS.
             * Should contain the applicationURL at the minimum.
             * Not providing this property would disable CORS configuration.
             */
            val allowedOrigins: List<String> = UaaDefaults.Cors.allowedOrigins,

            /**
             * Methods to be allowed, e.g. GET,POST,...
             */
            val allowedMethods: List<String> = UaaDefaults.Cors.allowedMethods,

            /**
             * Request headers to be allowed, e.g. content-type,accept,origin,x-requested-with,...
             */
            val allowedHeaders: List<String> = UaaDefaults.Cors.allowedHeaders,

            /**
             * Response headers that you want to expose to the client JavaScript programmer, e.g. Infiware-Authorization.
             * I don't think we need to mention here the headers that we don't want to access through JavaScript.
             * Still, by default, we have provided most of the common headers.
             *
             * <br></br>
             * See [
             * here](http://stackoverflow.com/questions/25673089/why-is-access-control-expose-headers-needed#answer-25673446) to know why this could be needed.
             */
            val exposedHeaders: List<String> = UaaDefaults.Cors.exposedHeaders,

            /**
             * CORS `maxAge` long property
             */
            val maxAge: Long = UaaDefaults.Cors.maxAge

    )

    data class Jwt(
            /**
             * Secret for signing JWT
             */
            val secret: String = UaaDefaults.Jwt.secret,

            /**
             * Default expiration milliseconds
             */
            val expirationMillis: Long = UaaDefaults.Jwt.expirationMillis,

            /**
             * Expiration milliseconds for short-lived tokens and cookies
             */
            val shortLivedMillis: Long = UaaDefaults.Jwt.shortLivedMillis
    )
}