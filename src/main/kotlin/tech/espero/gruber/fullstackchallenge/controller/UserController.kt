package tech.espero.gruber.fullstackchallenge.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tech.espero.gruber.fullstackchallenge.model.User
import tech.espero.gruber.fullstackchallenge.service.UserService

@RestController
class UserController {

    @Autowired
    private lateinit var userService: UserService

    data class CreateUserRequest(
        val username: String,
        val password: String,
        val userRole: User.UserRole
    )

    /**
     * Creates a new user given username, password and role.
     */
    @PostMapping("/users")
    fun createUser(@RequestBody userRequest: CreateUserRequest): ResponseEntity<User> {
        return ResponseEntity(userService.createUser(
            userRequest.username,
            userRequest.password,
            userRequest.userRole
        ), HttpStatus.CREATED)
    }
}
