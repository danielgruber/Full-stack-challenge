package tech.espero.gruber.fullstackchallenge.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import tech.espero.gruber.fullstackchallenge.model.Product
import tech.espero.gruber.fullstackchallenge.service.ProductService

/**
 * Implements all CRUD methods for /product.
 */
@RestController
class ProductController {
    @Autowired
    private lateinit var productService: ProductService

    /**
     * Lists all products.
     */
    @Operation(summary = "Gets a list of all products.")
    @GetMapping("/product")
    fun getAll(): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getAll())
    }
}
