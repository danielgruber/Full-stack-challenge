package tech.espero.gruber.fullstackchallenge.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import tech.espero.gruber.fullstackchallenge.exceptions.*
import tech.espero.gruber.fullstackchallenge.model.Product
import tech.espero.gruber.fullstackchallenge.model.User
import tech.espero.gruber.fullstackchallenge.repository.UserRepository
import java.util.UUID
import javax.transaction.Transactional

/**
 * Handles the logic of depositing coins, buying products and getting back coins.
 */
@Service
class CommerceService {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var productService: ProductService

    /**
     * allowed coins.
     */
    val allowedCoins = setOf(5, 10, 20, 50, 100)

    data class BuyFeedback(
        val total: Int,
        val product: Product,
        val numberOfProduct: Int,
        val change: List<Int>
    )

    /**
     * Deposits coins to the vending machine.
     */
    @Transactional
    fun deposit(coins: List<Int>): User {
        val user = getBuyerUserOrThrow()

        coins.forEach {
            if (!allowedCoins.contains(it)) {
                throw InvalidCoinException("Coin $it is not valid")
            }
        }

        return userService.updateUserBalance(
            user.username,
            user.depositCents + coins.fold(0) { acc, coin ->
                acc + coin
            }
        )
    }

    @Transactional
    fun buy(productId: UUID, amountOfProducts: Int): BuyFeedback {
        val user = getBuyerUserOrThrow()
        val product = productService.getById(productId) ?: throw ProductNotFoundException()
        val total = product.cost * amountOfProducts

        if (user.depositCents < total) {
            throw NotEnoughMoneyException()
        }

        val change = user.depositCents - total
        productService.buyProduct(productId, amountOfProducts)

        val changeCoins = mutableListOf<Int>()
        var remainingChange = change
        while (remainingChange > 0) {
            val coin = allowedCoins.filter {
                it < remainingChange
            }.maxOrNull()!!
            changeCoins.add(coin)
            remainingChange -= coin
        }

        return BuyFeedback(
            total,
            product,
            amountOfProducts,
            changeCoins
        )
    }

    fun reset(): User {
        val user = getBuyerUserOrThrow()
        userService.updateUserBalance(user.username, 0)

        return user
    }

    private fun getBuyerUserOrThrow(): User {
        val user = userService.getCurrentLoggedIn() ?: throw PermissionException("No user logged in")

        if (user.role != User.UserRole.BUYER) {
            throw InvalidRoleException("User must be a buyer to deposit money.")
        }

        return user
    }
}
