package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Thrown when product already exists with same id.
 */
class ProductAlreadyExistsException(message: String? = null): StatusException(message, HttpStatus.CONFLICT)

