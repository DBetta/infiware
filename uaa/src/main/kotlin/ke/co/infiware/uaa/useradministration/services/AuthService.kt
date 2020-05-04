package ke.co.infiware.uaa.useradministration.services

import ke.co.infiware.uaa.useradministration.dtos.*

/**
 *
 * @author Denis Gitonga
 */

interface AuthService {

    /**
     * Authenticates a user by [AuthDto]
     * @return [AuthTokenDto]
     */
    suspend fun authenticate(dto: AuthDto): AuthTokenDto

    /**
     * Changes user password.
     * @return [UserDto]
     */
    suspend fun changePassword(dto: AuthChangePasswordDto): UserDto

    /**
     * Verifies user account
     * @return [UserDto]
     */
    suspend fun verifyAccount(dto: AuthVerifyAccountDto): UserDto

    /**
     * Initiates password reset
     * @return [UserDto]
     */
    suspend fun forgotPassword(dto: AuthForgotPasswordRequestDto): UserDto

    /**
     * Completes password reset process
     * @return [UserDto]
     */
    suspend fun resetPassword(dto: AuthResetPasswordRequestDto): UserDto

}