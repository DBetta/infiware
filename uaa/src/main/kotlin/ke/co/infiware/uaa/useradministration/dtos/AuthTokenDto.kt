package ke.co.infiware.uaa.useradministration.dtos

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @author Denis Gitonga
 */
class AuthTokenDto(

        val tokenType: String,

        val accessToken: String
)