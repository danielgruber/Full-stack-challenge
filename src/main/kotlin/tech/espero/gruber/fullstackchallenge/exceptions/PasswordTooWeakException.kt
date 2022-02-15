package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Thrown if password does not match minimum criteria.
 */
class PasswordTooWeakException(message: String? = null): StatusException(message, HttpStatus.BAD_REQUEST)
