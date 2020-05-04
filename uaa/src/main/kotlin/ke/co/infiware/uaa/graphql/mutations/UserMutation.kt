package ke.co.infiware.uaa.graphql.mutations

import graphql.kickstart.tools.GraphQLMutationResolver
import ke.co.infiware.uaa.useradministration.dtos.UserDto
import ke.co.infiware.uaa.useradministration.services.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 *
 * @author Denis Gitonga
 */
@Component
class UserMutation(
        private val userService: UserService
) : GraphQLMutationResolver {

    /**
     * Create an user
     */
    suspend fun createUser(user: UserDto): UserDto {
        log.debug("Creating user")

        return userService.saveUser(dto = user)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserMutation::class.java)
    }
}