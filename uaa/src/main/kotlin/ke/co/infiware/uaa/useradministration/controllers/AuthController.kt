package ke.co.infiware.uaa.useradministration.controllers

import ke.co.infiware.uaa.useradministration.dtos.AuthDto
import ke.co.infiware.uaa.useradministration.services.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * @author Denis Gitonga
 */
@RestController
@RequestMapping
class AuthController(
        private val authService: AuthService
) {

    @PostMapping(path = ["authenticate"])
    suspend fun authenticate(@RequestBody dto: AuthDto) =
            authService.authenticate(dto = dto)

}
