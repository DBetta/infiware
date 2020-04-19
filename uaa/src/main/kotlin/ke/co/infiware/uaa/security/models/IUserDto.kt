package ke.co.infiware.uaa.security.models

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority

/**
 *
 * @author Denis Gitonga
 */
interface IUserDto : CredentialsContainer {

    @JsonIgnore
    fun getUsername(): String

    @JsonIgnore
    fun getPassword(): String?

    @JsonIgnore
    fun getRoles(): Set<String>

    @JsonIgnore
    fun isVerified(): Boolean

    @JsonIgnore
    fun isDisabled(): Boolean


}