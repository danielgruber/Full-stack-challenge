package tech.espero.gruber.fullstackchallenge.exceptions

/**
 * Thrown when one does not have access to resource.
 */
class PermissionException(message: String? = null): Exception(message)
