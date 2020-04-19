package ke.co.infiware.uaa.security

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import ke.co.infiware.uaa.security.models.InfiwarePrincipal
import ke.co.infiware.uaa.useradministration.enities.InfiwareUser
import ke.co.infiware.uaa.useradministration.mappers.UserMapper
import ke.co.infiware.uaa.useradministration.repositories.InfiwareUserRepository
import ke.co.infiware.uaa.utils.getI18Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.apache.commons.validator.routines.EmailValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.function.Supplier

/**
 *
 * @author Denis Gitonga
 */
@Service
class InfiwareReactiveUserDetailsService(
        private val userMapper: UserMapper,

        private val userRepository: InfiwareUserRepository
) : ReactiveUserDetailsService {

    private val log: Logger = LoggerFactory.getLogger(InfiwareReactiveUserDetailsService::class.java)

    override fun findByUsername(username: String): Mono<UserDetails> = mono {

        log.debug("Loading user having username {}", username)

        val usernameNotFoundMessage = Supplier {
            getI18Message("ke.co.infiware.uaa.userNotFound", username)
        }

        val infiwareUser = findUserByUsername(username = username)
                ?: throw UsernameNotFoundException(usernameNotFoundMessage.get())


        val userDto = userMapper.map(user = infiwareUser)

        log.debug("Authenticated user: {}", userDto)

        return@mono InfiwarePrincipal(userDto = userDto)
    }

    final suspend fun findUserByUsername(username: String): InfiwareUser? {
        return when {
            isEmailAddress(username = username) -> findByEmailAddress(emailAddress = username)
            isPhoneNumber(username = username) -> findByPhoneNumber(phoneNumber = username)
            else -> fetchByUsername(username = username)
        }
    }

    /**
     * Checks if the username is a phoneNumber
     */
    fun isPhoneNumber(username: String): Boolean {
        val phoneUtil = PhoneNumberUtil.getInstance()
        val locale = LocaleContextHolder.getLocale()

        return try {
            val phoneNumber = phoneUtil.parse(username, locale.country)
            return phoneUtil.isValidNumber(phoneNumber)
        } catch (ex: NumberParseException) {
            false
        }
    }

    /**
     * Checks if the username is emailAddress
     */
    fun isEmailAddress(username: String): Boolean {
        return EmailValidator.getInstance(true, true)
                .isValid(username)
    }

    /**
     * Find user by emailAddress
     */
    suspend fun findByEmailAddress(emailAddress: String): InfiwareUser? {
        return withContext(Dispatchers.IO) {
            userRepository.findByEmailAddress(emailAddress = emailAddress)
        }
    }

    /**
     * Find user by phoneNumber
     */
    suspend fun findByPhoneNumber(phoneNumber: String): InfiwareUser? {
        return withContext(Dispatchers.IO) {
            userRepository.findByContactNumber(contactNumber = phoneNumber)
        }
    }

    /**
     * Find user by username¬
     */
    suspend fun fetchByUsername(username: String): InfiwareUser? {
        return withContext(Dispatchers.IO) {
            userRepository.findByUsername(username = username)
        }
    }


}