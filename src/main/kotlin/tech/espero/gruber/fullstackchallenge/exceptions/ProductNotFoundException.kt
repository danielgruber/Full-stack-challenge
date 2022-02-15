package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Thrown when product was not found.
 */
class ProductNotFoundException(message: String? = null): StatusException(message, HttpStatus.NOT_FOUND)
