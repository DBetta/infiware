package ke.co.infiware.uaa.useradministration.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.pozo.KotlinBuilder
import ke.co.infiware.uaa.security.models.IUserDto
import ke.co.infiware.uaa.useradministration.enums.UserProvider
import ke.co.infiware.uaa.utils.getI18Message
import java.time.LocalDateTime
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
@KotlinBuilder
data class UserDto(
        val code: UUID? = null,

        private val username: String? = null,

        var rawPassword: String? = null,

        val firstName: String? = null,

        val middleName: String? = null,

        val lastName: String? = null,

        val emailAddress: String? = null,

        val contactNumber: String? = null,

        val photoUrl: String? = null,

        val provider: UserProvider = UserProvider.DEFAULT,

        val providerId: String? = null,

        private val verified: Boolean = false,

        private val disabled: Boolean = false,

        @JsonIgnore
        private val password: String? = null,

        @JsonIgnore
        val credentialsUpdatedMillis: LocalDateTime = LocalDateTime.now()
) : IUserDto {
    override fun getUsername(): String {

        return emailAddress ?: contactNumber ?: username
        ?: throw IllegalArgumentException(getI18Message(messageKey = "ke.co.infiware.uaa.missingUserName"))
    }

    override fun getPassword(): String? = password

    override fun getRoles(): Set<String> = emptySet()

    override fun isVerified(): Boolean = verified

    override fun isDisabled(): Boolean = disabled

    override fun eraseCredentials() {
        rawPassword = null
    }
}
