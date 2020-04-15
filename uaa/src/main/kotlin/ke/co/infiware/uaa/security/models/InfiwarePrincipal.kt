package ke.co.infiware.uaa.security.models

import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.OidcUser

/**
 *
 * @author Denis Gitonga
 */
data class InfiwarePrincipal(
        private val userDto: IUserDto,

        private var attributes: Map<String, Any> = emptyMap(),
        private var oidcUserInfo: OidcUserInfo? = null,
        private var name: String? = null,
        private var oidcIdToken: OidcIdToken? = null,
        private var claims: Map<String, Any> = emptyMap()
) : UserDetails, OidcUser, CredentialsContainer {

    override fun getAttributes(): Map<String, Any> = attributes

    override fun getUserInfo(): OidcUserInfo? = oidcUserInfo

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val roles = userDto.getRoles()
        return roles.map { InfiwareGrantedAuthority(authority = "ROLE_$it") }
                .toList()
    }

    override fun getName(): String? = name

    override fun getIdToken(): OidcIdToken? = oidcIdToken

    override fun getClaims(): Map<String, Any> = claims

    override fun isEnabled(): Boolean = true

    override fun getUsername(): String = userDto.getUsername()

    override fun isCredentialsNonExpired(): Boolean = true

    override fun getPassword(): String? = userDto.getPassword()

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun eraseCredentials() {
        userDto.eraseCredentials()
        attributes = emptyMap()
        claims = emptyMap()
        oidcIdToken = null
        name = null
        oidcUserInfo = null
    }


}