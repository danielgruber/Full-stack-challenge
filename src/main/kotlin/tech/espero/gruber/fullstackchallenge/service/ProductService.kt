package tech.espero.gruber.fullstackchallenge.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import tech.espero.gruber.fullstackchallenge.exceptions.PermissionException
import tech.espero.gruber.fullstackchallenge.exceptions.ProductAlreadyExistsException
import tech.espero.gruber.fullstackchallenge.exceptions.ProductNotFoundException
import tech.espero.gruber.fullstackchallenge.model.Product
import tech.espero.gruber.fullstackchallenge.model.User
import tech.espero.gruber.fullstackchallenge.repository.ProductRepository
import java.util.*

/**
 * Handles all business logic related to product.
 */
@Service
class ProductService {
    @Autowired
    private lateinit var productRepo: ProductRepository

    @Autowired
    private lateinit var userService: UserService

    /**
     * Gets all products.
     */
    fun getAll(): List<Product> {
        return productRepo.findAll().toList()
    }

    /**
     * Gets product by uuid.
     */
    fun getById(uuid: UUID): Product? {
        return productRepo.findByIdOrNull(uuid)
    }

    /**
     * Creates product with UUID.
     * The seller will be automatically set to the current user.
     */
    fun create(uuid: UUID, name: String, amountAvailable: Int, cost: Int): Product {
        if (getById(uuid) != null) {
            throw ProductAlreadyExistsException()
        }

        if (userService.getCurrentLoggedIn()!!.role != User.UserRole.SELLER) {
            throw PermissionException("Can't create user as buyer.")
        }

        val product = Product(
            uuid,
            amountAvailable,
            cost,
            name,
            userService.getCurrentLoggedIn()!!
        )
        productRepo.save(product)

        return product
    }

    /**
     * Creates or updates a product.
     */
    fun createOrUpdate(uuid: UUID, name: String?, amountAvailable: Int?, cost: Int?): Product {
        return if (productRepo.existsById(uuid)) {
            updateByUUID(uuid, name, amountAvailable, cost)
        } else {
            create(uuid, name!!, amountAvailable!!, cost!!)
        }
    }

    /**
     * Updates product by uuid.
     */
    fun updateByUUID(uuid: UUID, name: String?, amountAvailable: Int?, cost: Int?): Product {
        val product = productRepo.findByIdOrNull(uuid) ?: throw ProductNotFoundException()

        if (product.seller != userService.getCurrentLoggedIn()) {
            throw PermissionException("Can't update product of other seller.")
        }

        if (name != null) {
            product.productName = name
        }

        if (amountAvailable != null) {
            product.amountAvailable = amountAvailable
        }

        if (cost != null) {
            product.cost = cost
        }

        productRepo.save(product)

        return product
    }

    /**
     * Deletes product by uuid.
     */
    fun deleteByUUID(uuid: UUID) {
        val product = productRepo.findByIdOrNull(uuid) ?: throw ProductNotFoundException()

        if (product.seller != userService.getCurrentLoggedIn()) {
            throw PermissionException("Can't delete product of other seller.")
        }

        productRepo.delete(product)
    }
}
