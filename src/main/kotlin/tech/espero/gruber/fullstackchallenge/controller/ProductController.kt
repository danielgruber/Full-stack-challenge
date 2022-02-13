package tech.espero.gruber.fullstackchallenge.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import tech.espero.gruber.fullstackchallenge.model.Product
import tech.espero.gruber.fullstackchallenge.service.ProductService

/**
 * Implements all CRUD methods for products.
 */
@RestController
class ProductController {
    @Autowired
    private lateinit var productService: ProductService

    @GetMapping("/products")
    fun getAll(): ResponseEntity<List<Product>> {
        return ResponseEntity(productService.getAll(), HttpStatus.OK)
    }
}
