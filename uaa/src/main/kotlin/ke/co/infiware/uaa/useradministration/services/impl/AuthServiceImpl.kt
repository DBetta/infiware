package ke.co.infiware.uaa.useradministration.services.impl

import ke.co.infiware.exceptions.utils.ExceptionUtils
import ke.co.infiware.uaa.configuration.UaaProperties
import ke.co.infiware.uaa.security.jwt.BlueTokenService
import ke.co.infiware.uaa.security.jwt.GreenTokenService
import ke.co.infiware.uaa.useradministration.dtos.*
import ke.co.infiware.uaa.useradministration.exceptions.UserNotFoundException
import ke.co.infiware.uaa.useradministration.mappers.UserMapper
import ke.co.infiware.uaa.useradministration.repositories.InfiwareUserRepository
import ke.co.infiware.uaa.useradministration.services.AuthService
import ke.co.infiware.uaa.utils.getI18Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDateTime

/**
 *
 * @author Denis Gitonga
 */
@Service
class AuthServiceImpl(
        private val userDetailsService: ReactiveUserDetailsService,
        private val passwordEncoder: PasswordEncoder,
        private val blueTokenService: BlueTokenService,
        private val uaaProperties: UaaProperties,
        private val userRepository: InfiwareUserRepository,
        private val userMapper: UserMapper,
        private val greenTokenService: GreenTokenService
) : AuthService {
    override suspend fun authenticate(dto: AuthDto): AuthTokenDto {
        log.debug("Authenticating {}", dto.username)

        val userDetails = userDetailsService.findByUsername(dto.username).awaitFirst()

        val passwordMatches = passwordEncoder.matches(dto.password, userDetails.password)

        if (passwordMatches.not()) {
            val message = getI18Message(messageKey = "ke.co.infiware.uaa.BadCredentials")
            throw BadCredentialsException(message)
        }

        log.debug("Generating token for {}", userDetails.username)
        val token = blueTokenService.createToken(
                aud = BlueTokenService.AUTH_AUDIENCE,
                sub = userDetails.username,
                expirationMillis = uaaProperties.jwt.shortLivedMillis)

        return AuthTokenDto(
                tokenType = "Bearer",
                accessToken = token
        )
    }

    override suspend fun changePassword(dto: AuthChangePasswordDto): UserDto {
        log.debug("Changing password for: {}", dto.userCode)

        val user = userRepository.findById(dto.userCode)
                .orElseThrow { UserNotFoundException(getI18Message("ke.co.infiware.uaa.userNotFound", *arrayOf(dto.userCode))) }

        val oldPasswordMatches = passwordEncoder.matches(dto.oldPassword, user.password)
        if (oldPasswordMatches.not()) {
            val message = getI18Message(messageKey = "ke.co.infiware.uaa.wrong.password")
            throw BadCredentialsException(message)
        }

        val newPassword = passwordEncoder.encode(dto.newPassword)

        val toSaveUser = user.copy(password = newPassword, credentialsUpdatedMillis = LocalDateTime.now())

        val savedUser = withContext(Dispatchers.IO) {
            userRepository.save(toSaveUser)
        }

        log.debug("Changed password for: {}", user.emailAddress)

        return userMapper.map(user = savedUser)
    }

    override suspend fun verifyAccount(dto: AuthVerifyAccountDto): UserDto {

        log.debug("Verifying account ... ")

        val user = userRepository.findById(dto.userCode)
                .orElseThrow { UserNotFoundException(getI18Message("ke.co.infiware.uaa.userNotFound", *arrayOf(dto.userCode))) }

        // ensure account not verified
        ExceptionUtils.validate(user.verified.not(), messageKey = "ke.co.infiware.uaa.alreadyVerified")

        val claims = greenTokenService.parseToken(
                token = dto.token,
                aud = BlueTokenService.VERIFY_AUDIENCE,
                issuedAfter = Timestamp.valueOf(user.credentialsUpdatedMillis).time
        )

        val claimEmailAddressMatches = claims.getClaim("emailAddress").toString().equals(user.emailAddress, ignoreCase = true)

        val claimUserCodeMatches = claims.subject == user.code.toString()

        ExceptionUtils.validate(claimUserCodeMatches.not() && claimEmailAddressMatches.not(),
                messageKey = "ke.co.infiware.uaa.verification.code")

        val toSaveUser = user.copy(verified = true, credentialsUpdatedMillis = LocalDateTime.now())

        val savedUser = withContext(Dispatchers.IO) {
            userRepository.save(toSaveUser)
        }

        log.debug("Verified user account: {}", savedUser.emailAddress)

        return userMapper.map(user = savedUser)
    }

    override suspend fun forgotPassword(dto: AuthForgotPasswordRequestDto): UserDto {
        log.debug("Processing forgot password for email: {}", dto.emailAddress)

        val user = userRepository.findByEmailAddress(dto.emailAddress)
                ?: throw UserNotFoundException(getI18Message("ke.co.infiware.uaa.userNotFound", *arrayOf(dto.emailAddress)))

        // TODO: implement sending email

        return userMapper.map(user = user)
    }

    override suspend fun resetPassword(dto: AuthResetPasswordRequestDto): UserDto {

        log.debug("Reseting password ...")

        val user = userRepository.findById(dto.userCode)
                .orElseThrow { UserNotFoundException(getI18Message("ke.co.infiware.uaa.userNotFound", *arrayOf(dto.userCode))) }

        val claims = blueTokenService.parseToken(
                token = dto.token,
                aud = BlueTokenService.FORGOT_PASSWORD_AUDIENCE,
                issuedAfter = Timestamp.valueOf(user.credentialsUpdatedMillis).time
        )

        val claimEmailAddressMatches = claims.getClaim("emailAddress").toString().equals(user.emailAddress, ignoreCase = true)

        val claimUserCodeMatches = claims.subject == user.code.toString()

        ExceptionUtils.validate(claimUserCodeMatches.not() && claimEmailAddressMatches.not(),
                messageKey = "ke.co.infiware.uaa.reset.password.code")

        val newPassword = passwordEncoder.encode(dto.newPassword)

        val toSaveUser = user.copy(password = newPassword, credentialsUpdatedMillis = LocalDateTime.now())

        val savedUser = withContext(Dispatchers.IO) {
            userRepository.save(toSaveUser)
        }

        log.debug("changed password for: {}", savedUser.emailAddress)

        return userMapper.map(user = savedUser)
    }


    companion object {
        private val log: Logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)
    }
}