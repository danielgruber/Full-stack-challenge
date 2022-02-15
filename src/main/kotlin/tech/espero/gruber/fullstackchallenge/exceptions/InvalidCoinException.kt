package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Thrown when an invalid coin inserted in the machine.
 */
class InvalidCoinException(message: String? = null): StatusException(message, HttpStatus.BAD_REQUEST)
