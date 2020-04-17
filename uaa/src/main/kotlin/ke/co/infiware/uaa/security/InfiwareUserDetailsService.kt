package ke.co.infiware.uaa.security


import ke.co.infiware.uaa.security.models.InfiwarePrincipal
import ke.co.infiware.uaa.useradministration.enities.InfiwareUser
import ke.co.infiware.uaa.useradministration.mappers.UserMapper
import ke.co.infiware.uaa.useradministration.repositories.UserRepository
import ke.co.infiware.uaa.utils.getI18Message
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import reactor.core.publisher.Mono

/**
 *
 * @author Denis Gitonga
 */
class InfiwareUserDetailsService(
        private val userMapper: UserMapper
) : ReactiveUserDetailsService {

    private val log: Logger = LoggerFactory.getLogger(InfiwareUserDetailsService::class.java)

    override fun findByUsername(username: String): Mono<UserDetails> {
        log.debug("Finding user by username: {}", username)
        return mono {
            val user = findUserByUserName(username = username)
            if (user == null){
                log.debug("could not find user by username: {}", username)
                throw UsernameNotFoundException(
                        getI18Message(messageKey = "ke.co.infiware.uaa.userNotFound", args = *arrayOf(username))
                )
            }

            InfiwarePrincipal(userDto = userMapper.map(user = user))
        }

    }

    private suspend fun findUserByUserName(username: String): InfiwareUser? {
        // check if this is an email or a mobile number

        TODO("Not yet implemented")
    }
}