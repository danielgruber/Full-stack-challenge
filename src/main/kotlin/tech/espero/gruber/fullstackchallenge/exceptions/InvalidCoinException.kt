package tech.espero.gruber.fullstackchallenge.exceptions

/**
 * Thrown when an invalid coin inserted in the machine.
 */
class InvalidCoinException(message: String? = null): Exception(message)
