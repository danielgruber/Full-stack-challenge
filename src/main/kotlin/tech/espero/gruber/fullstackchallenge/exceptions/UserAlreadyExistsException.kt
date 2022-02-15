package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Thrown when user with same username already exists.
 */
class UserAlreadyExistsException(message: String? = null): StatusException(message, HttpStatus.CONFLICT)
