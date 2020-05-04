package ke.co.infiware.uaa.useradministration.services.impl

import ke.co.infiware.uaa.useradministration.dtos.UserDto
import ke.co.infiware.uaa.useradministration.enities.InfiwareUser
import ke.co.infiware.uaa.useradministration.exceptions.UserNotFoundException
import ke.co.infiware.uaa.useradministration.mappers.UserMapper
import ke.co.infiware.uaa.useradministration.repositories.InfiwareUserRepository
import ke.co.infiware.uaa.useradministration.services.UserService
import ke.co.infiware.uaa.utils.getI18Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
@Service
internal class UserServiceImpl(
        private val userRepository: InfiwareUserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val userMapper: UserMapper
) : UserService {

    override suspend fun saveUser(dto: UserDto): UserDto {
        require(dto.rawPassword != null) { getI18Message("ke.co.infiware.uaa.missingPassword") }
        require(dto.code == null) { getI18Message("ke.co.infiware.uaa.user.save.codeNotRequired") }

        // generate password
        val password = passwordEncoder.encode(dto.rawPassword)
        require(password != null) { getI18Message("ke.co.infiware.uaa.missingPassword") }

        // map the dto to user
        val user = userMapper.map(dto = dto)
                .copy(password = password, verified = false, blocked = false, deleted = false)

        // save the user
        val savedUser = internalSaveUser(user = user)

        // TODO: send notification for verification

        return userMapper.map(user = savedUser)
    }

    override suspend fun updateUser(dto: UserDto): UserDto {

        require(dto.code != null) { getI18Message("ke.co.infiware.uaa.user.save.codeRequired") }

        // find the user with code
        val existingUser = userRepository.findById(dto.code)
                .orElseThrow { UserNotFoundException(getI18Message("ke.co.infiware.uaa.userNotFound", *arrayOf(dto.code))) }

        TODO("Not yet implemented")
    }

    override suspend fun getUser(code: UUID): UserDto? {
        log.debug("Fetching user by {}", code)

        val user = userRepository.findById(code).orElse(null) ?: return null

        val userDto = userMapper.map(user = user)

        log.debug("Found user for {}", code)

        return userDto
    }


    private suspend fun internalSaveUser(user: InfiwareUser): InfiwareUser {
        log.debug("Saving user: {}", user)
        val savedUser = userRepository.save(user)
        log.debug("Successfully saved user: {}", savedUser)
        return savedUser
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }
}