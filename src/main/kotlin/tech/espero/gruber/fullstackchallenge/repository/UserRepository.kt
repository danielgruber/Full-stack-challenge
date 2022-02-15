package tech.espero.gruber.fullstackchallenge.repository

import org.springframework.data.repository.CrudRepository
import tech.espero.gruber.fullstackchallenge.model.User
import java.util.*

interface UserRepository: CrudRepository<User, UUID> {
    /**
     * Returns user by username.
     */
    fun getByUsernameIgnoreCase(username: String): User?
}
