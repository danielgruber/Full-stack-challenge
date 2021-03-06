package tech.espero.gruber.fullstackchallenge.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import tech.espero.gruber.fullstackchallenge.exceptions.PasswordTooWeakException
import tech.espero.gruber.fullstackchallenge.exceptions.PermissionException
import tech.espero.gruber.fullstackchallenge.exceptions.UserAlreadyExistsException
import tech.espero.gruber.fullstackchallenge.model.User
import tech.espero.gruber.fullstackchallenge.repository.UserRepository
import java.util.*

/**
 * Handles all business logic for users.
 */
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
        return userRepo.getByUsernameIgnoreCase(username)
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
        if (userRepo.getByUsernameIgnoreCase(username) != null) {
            throw UserAlreadyExistsException("The username $username is already taken")
        }

        validatePasswordStrengthOrThrow(password)

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
     * Updates a given user's password.
     * Does not validate old password.
     */
    fun updateUser(
        username: String,
        password: String? = null
    ): User {
        val user = userRepo.getByUsernameIgnoreCase(username) ?: throw UsernameNotFoundException("User $username was not found for update")

        if (password != null) {
            validatePasswordStrengthOrThrow(password)
            user.password = passwordEncoder.encode(password)
        }

        userRepo.save(user)

        return user
    }

    /**
     * Updates the balance of a given user. Does not validate permissions.
     */
    fun updateUserBalance(
        username: String,
        deposit: Int
    ): User {
        val user = userRepo.getByUsernameIgnoreCase(username) ?: throw UsernameNotFoundException("User $username was not found for update")

        user.depositCents = deposit

        userRepo.save(user)

        return user
    }

    /**
     * deletes a given user. Validates for permission by validating the password.
     */
    fun deleteUser(
        username: String,
        password: String
    ) {
        val user = userRepo.getByUsernameIgnoreCase(username) ?:  throw UsernameNotFoundException("User $username was not found for update")

        if (!passwordEncoder.matches(password, user.password)) {
            throw PermissionException("You need to know the password of a user to delete it.")
        }

        userRepo.delete(user)
    }

    /**
     * Validates password strength. It required at least
     * - one lowercase char
     * - one uppercase char
     * - one number
     */
    private fun validatePasswordStrengthOrThrow(password: String) {
        if (!Regex(".*[a-z]+.*").matches(password) || !Regex(".*[A-Z]+.*").matches(password) || !Regex(".*[0-9]+.*").matches(password) || password.length < 8) {
            throw PasswordTooWeakException("Password must be at least eight characters long and must contain at least one lowercase, one uppercase character and one number.")
        }
    }
}
