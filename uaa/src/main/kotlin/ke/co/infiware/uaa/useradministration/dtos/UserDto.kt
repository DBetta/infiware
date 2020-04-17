package ke.co.infiware.uaa.useradministration.dtos

import com.github.pozo.KotlinBuilder
import ke.co.infiware.uaa.security.models.IUserDto
import ke.co.infiware.uaa.useradministration.enums.PreferredAuthType
import ke.co.infiware.uaa.useradministration.enums.YesNoType
import ke.co.infiware.uaa.utils.getI18Message
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
@KotlinBuilder
data class UserDto(
        val code: UUID? = null,

        val username: UsernameDto = UsernameDto(),

        val preferredAuthType: PreferredAuthType = PreferredAuthType.EMAIL_ADDRESS,

        var rawPassword: String? = null,

        val fistName: String? = null,

        val middleName: String? = null,

        val lastName: String? = null,

        val photoUrl: String? = null,

        private var verified: YesNoType = YesNoType.NO,

        private var disabled: YesNoType = YesNoType.NO
) : IUserDto {
    override fun getUsername(): String = when (preferredAuthType) {
        PreferredAuthType.EMAIL_ADDRESS -> username.emailAddress
        PreferredAuthType.MOBILE_NUMBER -> username.mobileNumber
    } ?: throw IllegalArgumentException(
            getI18Message(messageKey = "ke.co.infiware.uaa.missingUserName", args = *arrayOf(preferredAuthType))
    )

    override fun getPassword(): String? = rawPassword

    override fun getRoles(): Set<String> = emptySet()

    override fun isVerified(): Boolean = verified == YesNoType.YES

    override fun isDisabled(): Boolean = disabled == YesNoType.YES

    override fun eraseCredentials() {
        rawPassword = null
    }
}
