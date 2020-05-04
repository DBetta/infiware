package ke.co.infiware.uaa.useradministration.services

import ke.co.infiware.uaa.useradministration.dtos.UserDto
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
interface UserService {

    /**
     * Saves a user
     * @param dto the user to save
     */
    suspend fun saveUser(dto: UserDto): UserDto

    /**
     * Updates a user
     * @param dto the user to save
     */
    suspend fun updateUser(dto: UserDto): UserDto

    /**
     *
     */
    suspend fun getUser(code: UUID): UserDto?
}