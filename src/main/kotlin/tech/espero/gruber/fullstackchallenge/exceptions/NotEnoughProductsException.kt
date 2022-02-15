package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Thrown when not enough products are in stock.
 */
class NotEnoughProductsException(message: String? = null): StatusException(message, HttpStatus.BAD_REQUEST)
