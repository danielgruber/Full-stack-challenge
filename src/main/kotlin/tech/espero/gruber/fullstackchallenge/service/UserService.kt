package tech.espero.gruber.fullstackchallenge.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import tech.espero.gruber.fullstackchallenge.exceptions.PermissionException
import tech.espero.gruber.fullstackchallenge.exceptions.UserAlreadyExistsException
import tech.espero.gruber.fullstackchallenge.model.User
import tech.espero.gruber.fullstackchallenge.repository.UserRepository
import java.util.*

@Service
class UserService {
    @Autowired
    private lateinit var userRepo: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    /**
     * Ensures current user matches given username or throws [PermissionException]
     */
    fun ensureCurrentUserOrThrow(username: String) {
        if (username != getCurrentLoggedIn()?.username) {
            throw PermissionException("Only the current user can access this.")
        }
    }

    /**
     * Gets user by username.
     */
    fun getByUsername(username: String): User? {
        return userRepo.getByUsername(username)
    }

    /**
     * Gets user by uuid.
     */
    fun getById(uuid: UUID): User? {
        return userRepo.findByIdOrNull(uuid)
    }

    /**
     * Gets user by uuid.
     */
    fun getAll(uuid: UUID): User? {
        return userRepo.findByIdOrNull(uuid)
    }

    /**
     * Gets user currently logged in user if existing.
     */
    fun getCurrentLoggedIn(): User? {
        val auth = SecurityContextHolder.getContext().authentication
        val principal = auth.principal as UserDetails
        return getByUsername(principal.username)
    }

    /**
     * Create a user given the data.
     */
    fun createUser(
        username: String,
        password: String,
        role: User.UserRole
    ): User {
        if (userRepo.getByUsername(username) != null) {
            throw UserAlreadyExistsException()
        }

        val user = User(
            UUID.randomUUID(),
            username,
            passwordEncoder.encode(password),
            0,
            role
        )
        userRepo.save(user)

        return user
    }

    /**
     * Updates a given user.
     */
    fun updateUser(
        username: String,
        password: String? = null,
        role: User.UserRole? = null
    ): User {
        val user = userRepo.getByUsername(username) ?: throw UsernameNotFoundException("User $username was not found for update")

        if (password != null) {
            user.password = passwordEncoder.encode(password)
        }

        if (role != null) {
            user.role = role
        }

        userRepo.save(user)

        return user
    }

    /**
     * deletes a given user.
     */
    fun deleteUser(
        username: String,
        password: String
    ) {
        val user = userRepo.getByUsername(username) ?:  throw UsernameNotFoundException("User $username was not found for update")

        if (!passwordEncoder.matches(password, user.password)) {
            throw PermissionException("You need to know the password of a user to delete it.")
        }

        userRepo.delete(user)
    }
}
