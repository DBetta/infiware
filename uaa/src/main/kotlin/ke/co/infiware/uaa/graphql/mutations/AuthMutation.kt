package ke.co.infiware.uaa.graphql.mutations

import graphql.kickstart.tools.GraphQLMutationResolver
import ke.co.infiware.uaa.useradministration.dtos.AuthDto
import ke.co.infiware.uaa.useradministration.dtos.AuthTokenDto
import ke.co.infiware.uaa.useradministration.services.AuthService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 *
 * @author Denis Gitonga
 */
@Component
class AuthMutation (
        private val authService: AuthService
): GraphQLMutationResolver {

    suspend fun authenticate(auth: AuthDto): AuthTokenDto {
        log.debug("authenticating {}", auth.username)
        return authService.authenticate(dto = auth)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AuthMutation::class.java)
    }
}
