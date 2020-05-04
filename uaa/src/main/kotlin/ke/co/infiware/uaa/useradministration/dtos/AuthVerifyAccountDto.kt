package ke.co.infiware.uaa.useradministration.dtos

import java.util.*

/**
 *
 * @author Denis Gitonga
 */
data class AuthVerifyAccountDto(
        val userCode: UUID,

        val token: String
)