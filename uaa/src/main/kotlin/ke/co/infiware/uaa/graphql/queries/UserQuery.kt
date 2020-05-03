package ke.co.infiware.uaa.graphql.queries

import graphql.kickstart.tools.GraphQLQueryResolver
import ke.co.infiware.uaa.useradministration.dtos.UserDto
import org.springframework.stereotype.Component
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
@Component
class UserQuery : GraphQLQueryResolver {

    suspend fun profile(code: UUID): UserDto {
        return UserDto(firstName = "Denis")
    }

}