package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Thrown when costs of a product is invalid.
 */
class InvalidCostException(message: String? = null): StatusException(message, HttpStatus.BAD_REQUEST)
