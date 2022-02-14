package tech.espero.gruber.fullstackchallenge.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.espero.gruber.fullstackchallenge.model.Product
import tech.espero.gruber.fullstackchallenge.service.ProductService
import java.util.*

/**
 * Implements all CRUD methods for /product.
 */
@RestController
class ProductController {
    @Autowired
    private lateinit var productService: ProductService

    data class CreateOrUpdateProductRequest(
        val name: String? = null,
        val cost: Int? = null,
        val amountAvailable: Int? = null
    )

    /**
     * Lists all products.
     */
    @Operation(summary = "Gets a list of all products.")
    @GetMapping("/product")
    fun getAll(): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getAll())
    }

    /**
     * Creates a new product.
     */
    @Operation(summary = "Create a new product.")
    @PostMapping("/product")
    fun create(@RequestBody createRequest: CreateOrUpdateProductRequest): ResponseEntity<Product> {
        return ResponseEntity.ok(productService.create(
            UUID.randomUUID(),
            createRequest.name!!,
            createRequest.amountAvailable!!,
            createRequest.cost!!
        ))
    }

    /**
     * Gets a product by uuid.
     */
    @Operation(summary = "Gets a product by UUID.")
    @GetMapping("/product/{uuid}")
    fun getOne(@PathVariable uuid: UUID): ResponseEntity<Product?> {
        return productService.getById(uuid)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    /**
     * Creates or updates a product.
     */
    @Operation(summary = "Creates or updates a product.")
    @PutMapping("/product/{uuid}")
    fun createOrUpdate(@PathVariable uuid: UUID, @RequestBody createRequest: CreateOrUpdateProductRequest): ResponseEntity<Product> {
        return ResponseEntity.ok(productService.createOrUpdate(
            uuid,
            createRequest.name,
            createRequest.amountAvailable,
            createRequest.cost
        ))
    }

    /**
     * Deletes a product.
     */
    @Operation(summary = "Deletes a product.")
    @DeleteMapping("/product/{uuid}")
    fun delete(@PathVariable uuid: UUID): ResponseEntity<Unit> {
        return ResponseEntity.ok(productService.deleteByUUID(uuid))
    }
}
