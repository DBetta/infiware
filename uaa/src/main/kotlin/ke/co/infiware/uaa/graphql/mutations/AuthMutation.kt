package ke.co.infiware.uaa.graphql.mutations

import graphql.kickstart.tools.GraphQLMutationResolver
import ke.co.infiware.uaa.useradministration.dtos.*
import ke.co.infiware.uaa.useradministration.services.AuthService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 *
 * @author Denis Gitonga
 */
@Component
class AuthMutation(
        private val authService: AuthService
) : GraphQLMutationResolver {

    suspend fun authenticate(auth: AuthDto): AuthTokenDto {

        log.debug("authenticating {}", auth.username)
        return authService.authenticate(dto = auth)
    }

    suspend fun changePassword(dto: AuthChangePasswordDto): UserDto {
        log.debug("Changing password...")
        return authService.changePassword(dto = dto)
    }

    suspend fun verifyAccount(dto: AuthVerifyAccountDto): UserDto {
        log.debug("Verifying account...")
        return authService.verifyAccount(dto = dto)
    }

    suspend fun forgotPassword(dto: AuthForgotPasswordRequestDto): UserDto {
        log.debug("Forgot password...")
        return authService.forgotPassword(dto = dto)
    }

    suspend fun resetPassword(dto: AuthResetPasswordRequestDto): UserDto {
        log.debug("Reset password...")
        return authService.resetPassword(dto = dto)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AuthMutation::class.java)
    }
}
