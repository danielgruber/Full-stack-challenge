package tech.espero.gruber.fullstackchallenge.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.espero.gruber.fullstackchallenge.model.User
import tech.espero.gruber.fullstackchallenge.service.UserService
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

/**
 * Provides CRUD endpoints for /user.
 */
@RestController
class UserController {

    @Autowired
    private lateinit var userService: UserService

    data class CreateUserRequest(
        @NotBlank(message = "username is mandatory")
        val username: String,
        val password: String,
        val userRole: User.UserRole
    )

    data class UpdateUserRequest(
        val password: String? = null
    )

    data class DeleteUserRequest(
        val password: String
    )

    /**
     * Creates a new user given username, password and role.
     */
    @Operation(summary = "Creates a new user.")
    @PostMapping("/user")
    fun createUser(@Valid @RequestBody userRequest: CreateUserRequest): ResponseEntity<User> {
        return ResponseEntity(userService.createUser(
            userRequest.username,
            userRequest.password,
            userRequest.userRole
        ), HttpStatus.CREATED)
    }

    /**
     * Gets currently logged-in user or 404.
     */
    @Operation(summary = "Gets current logged in user model.")
    @GetMapping("/user")
    fun getCurrentUser(): ResponseEntity<User> {
        return userService.getCurrentLoggedIn()?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    /**
     * Gets user by username.
     */
    @Operation(summary = "Gets user by username.")
    @GetMapping("/user/{username}")
    fun getUser(@PathVariable username: String): ResponseEntity<User> {
       userService.ensureCurrentUserOrThrow(username)

        return userService.getByUsername(username)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    /**
     * updates by username.
     */
    @Operation(summary = "Updates user by username.")
    @PutMapping("/user/{username}")
    fun updateUser(@PathVariable username: String, @RequestBody updateUserRequest: UpdateUserRequest): ResponseEntity<User> {
        userService.ensureCurrentUserOrThrow(username)

        val updatedUser = userService.updateUser(username, updateUserRequest.password)
        return ResponseEntity.ok(updatedUser)
    }

    /**
     * deletes by username.
     */
    @Operation(summary = "Deleted user by username and password.")
    @DeleteMapping("/user/{username}")
    fun deleteUser(@PathVariable username: String, @RequestBody deleteUserRequest: DeleteUserRequest): ResponseEntity<Unit> {
        userService.deleteUser(username, deleteUserRequest.password)

        return ResponseEntity(HttpStatus.OK)
    }
}
