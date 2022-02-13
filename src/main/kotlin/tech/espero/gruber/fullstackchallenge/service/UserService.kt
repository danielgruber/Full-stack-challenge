package tech.espero.gruber.fullstackchallenge.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
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
     * Gets user by username.
     */
    fun getByUsername(username: String): User? {
        return userRepo.getByUsername(username)
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
}
