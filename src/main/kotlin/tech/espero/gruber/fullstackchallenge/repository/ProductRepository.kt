package tech.espero.gruber.fullstackchallenge.repository

import org.springframework.data.repository.CrudRepository
import tech.espero.gruber.fullstackchallenge.model.Product
import java.util.*

interface ProductRepository: CrudRepository<Product, UUID> {
}
