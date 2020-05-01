package ke.co.infiware.uaa.useradministration.dtos

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @author Denis Gitonga
 */
class AuthTokenDto(

        @JsonProperty("token_type")
        val tokenType: String,

        @JsonProperty("access_token")
        val accessToken: String
)