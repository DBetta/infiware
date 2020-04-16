package ke.co.infiware.uaa.useradministration.dtos

import com.github.pozo.KotlinBuilder
import ke.co.infiware.exceptions.utils.ExceptionUtils
import ke.co.infiware.uaa.security.models.IUserDto
import ke.co.infiware.uaa.useradministration.enums.PreferredAuthType
import ke.co.infiware.uaa.useradministration.enums.YesNoType
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
@KotlinBuilder
data class UserDto(
        val code: UUID? = null,

        val emailAddress: String? = null,

        val mobileNumber: String? = null,

        val preferredAuthType: PreferredAuthType = PreferredAuthType.EMAIL_ADDRESS,

        private var password: String? = null,

        private var verified: YesNoType = YesNoType.NO,

        private var disabled: YesNoType = YesNoType.NO
) : IUserDto {
    override fun getUsername(): String = when (preferredAuthType) {
        PreferredAuthType.EMAIL_ADDRESS -> emailAddress
        PreferredAuthType.MOBILE_NUMBER -> mobileNumber
    } ?: throw IllegalArgumentException(
            ExceptionUtils.getMessage(messageKey = "ke.co.infiware.uaa.missingUserName", args = *arrayOf(preferredAuthType))
    )

    override fun getPassword(): String? = password

    override fun getRoles(): Set<String> = emptySet()

    override fun isVerified(): Boolean = verified == YesNoType.YES

    override fun isDisabled(): Boolean = disabled == YesNoType.YES

    override fun eraseCredentials() {
        password = null
    }
}
