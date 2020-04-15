package ke.co.infiware.uaa.security.models

import org.springframework.security.core.GrantedAuthority

/**
 *
 * @author Denis Gitonga
 */
data class InfiwareGrantedAuthority(
        private val authority: String
) : GrantedAuthority {

    override fun getAuthority(): String = authority
}

