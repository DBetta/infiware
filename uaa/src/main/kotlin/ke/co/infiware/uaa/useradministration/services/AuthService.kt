package ke.co.infiware.uaa.useradministration.services

import ke.co.infiware.uaa.useradministration.dtos.AuthDto
import ke.co.infiware.uaa.useradministration.dtos.AuthTokenDto

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

}