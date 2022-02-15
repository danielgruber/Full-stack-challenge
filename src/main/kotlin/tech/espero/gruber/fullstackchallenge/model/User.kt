package tech.espero.gruber.fullstackchallenge.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
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
    @NotEmpty
    private val username: String,

    @Column(nullable = false)
    @JsonIgnore
    @NotEmpty
    private var password: String,

    @Column(nullable = false)
    @Min(0)
    var depositCents: Int,

    @Column(nullable = false)
    val role: UserRole
): UserDetails {
    /**
     * Represents the user role.
     */
    enum class UserRole: GrantedAuthority {
        BUYER,
        SELLER;

        override fun getAuthority(): String {
            return this.name
        }
    }

    // this is just needed to automatically remove all products if user is removed.
    @JsonIgnore
    @OneToMany(mappedBy = "seller", cascade = [CascadeType.REMOVE])
    private lateinit var products: List<Product> ;


    override fun toString(): String {
        return "User(id=$id, username='$username', password='$password', depositCents=$depositCents, role=$role)"
    }

    @JsonIgnore
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(role)
    }

    @JsonIgnore
    override fun getPassword(): String {
        return this.password
    }

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getUsername(): String {
        return this.username
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        return other is User && other.id == id
    }
}
