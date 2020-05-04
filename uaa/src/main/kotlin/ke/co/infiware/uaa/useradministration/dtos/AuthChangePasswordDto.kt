package ke.co.infiware.uaa.useradministration.dtos

import java.util.*

/**
 *
 * @author Denis Gitonga
 */
data class AuthChangePasswordDto(
        val userCode: UUID,

        val oldPassword: String,

        val newPassword: String
)