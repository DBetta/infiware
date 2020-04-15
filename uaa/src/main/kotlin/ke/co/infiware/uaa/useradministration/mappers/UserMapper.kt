package ke.co.infiware.uaa.useradministration.mappers

import ke.co.infiware.uaa.useradministration.dtos.UserDto
import ke.co.infiware.uaa.useradministration.enities.InfiwareUser
import org.mapstruct.Mapper

/**
 *
 * @author Denis Gitonga
 */
@Mapper(componentModel = "spring")
interface UserMapper {

    fun map(dto: UserDto): InfiwareUser

    fun map(user: InfiwareUser): UserDto

}