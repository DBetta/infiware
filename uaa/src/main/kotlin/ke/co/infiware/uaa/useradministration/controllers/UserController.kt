package ke.co.infiware.uaa.useradministration.controllers

import ke.co.infiware.uaa.useradministration.dtos.UserDto
import ke.co.infiware.uaa.useradministration.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 *
 * @author Denis Gitonga
 */
@RestController
@RequestMapping("/users")
class UserController(
        private val userService: UserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun saveUser(@RequestBody dto: UserDto): UserDto =
            userService.saveUser(dto = dto)

}