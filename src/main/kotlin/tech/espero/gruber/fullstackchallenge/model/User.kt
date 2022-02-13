package tech.espero.gruber.fullstackchallenge.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.Pattern

/**
 * Represents a user for vending machine challenge.
 * Each user is either a seller or buyer.
 * Sellers can have many products, buyer buy products.
 */
@Entity
class User(
    @Id @Column(nullable = false)
    val id: UUID? = null,

    @Column(nullable = false)
    val username: String,

    @Column(nullable = false)
    @JsonIgnore
    var password: String,

    @Column(nullable = false)
    var depositCents: Int,

    @Column(nullable = false)
    var role: UserRole
) {
    /**
     * Represents the user role.
     */
    enum class UserRole {
        BUYER,
        SELLER
    }

    override fun toString(): String {
        return "User(id=$id, username='$username', password='$password', depositCents=$depositCents, role=$role)"
    }
}
