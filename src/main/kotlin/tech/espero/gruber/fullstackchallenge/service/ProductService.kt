package tech.espero.gruber.fullstackchallenge.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import tech.espero.gruber.fullstackchallenge.model.Product
import tech.espero.gruber.fullstackchallenge.repository.ProductRepository

/**
 * Handles all business logic related to product.
 */
@Service
class ProductService {
    @Autowired
    private lateinit var productRepo: ProductRepository

    fun getAll(): List<Product> {
        return productRepo.findAll().toList()
    }
}

