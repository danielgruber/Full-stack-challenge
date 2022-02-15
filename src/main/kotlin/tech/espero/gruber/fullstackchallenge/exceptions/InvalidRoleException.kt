package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Thrown when doing a action with wrong user role.
 */
class InvalidRoleException(message: String? = null): StatusException(message, HttpStatus.FORBIDDEN)
