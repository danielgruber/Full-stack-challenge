package tech.espero.gruber.fullstackchallenge.model

import java.util.*
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty

/**
 * Represents a product to be bought.
 * Each product is sold by a given seller.
 */
@Entity
class Product(
    @Id
    @Column(nullable = false)
    val id: UUID,

    @Column(nullable = false)
    @Min(0)
    var amountAvailable: Int,

    @Column(nullable = false)
    @Min(1)
    var cost: Int,

    @Column(nullable = false)
    @NotEmpty
    var productName: String,

    @ManyToOne(optional = false)
    var seller: User
)
