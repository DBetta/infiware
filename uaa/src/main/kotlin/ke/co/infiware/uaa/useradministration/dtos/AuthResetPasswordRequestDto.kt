package ke.co.infiware.uaa.useradministration.dtos

import java.util.*

/**
 *
 * @author Denis Gitonga
 */
data class AuthResetPasswordRequestDto(

        val userCode: UUID,

        val token: String,

        val newPassword: String
)