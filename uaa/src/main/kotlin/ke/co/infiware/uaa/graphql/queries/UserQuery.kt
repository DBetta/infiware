package ke.co.infiware.uaa.graphql.queries

import graphql.kickstart.tools.GraphQLQueryResolver
import ke.co.infiware.uaa.useradministration.dtos.UserDto
import ke.co.infiware.uaa.useradministration.services.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
@Component
class UserQuery (
        private val userService: UserService
) : GraphQLQueryResolver {

    private val log: Logger = LoggerFactory.getLogger(UserQuery::class.java)

    suspend fun profile(code: UUID): UserDto? {
        log.debug("Fetching profile for user with code: {}", code)
        return userService.getUser(code = code)
    }

}