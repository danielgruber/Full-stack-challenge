package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Thrown when one does not have access to resource.
 */
class PermissionException(message: String? = null): StatusException(message, HttpStatus.FORBIDDEN)
