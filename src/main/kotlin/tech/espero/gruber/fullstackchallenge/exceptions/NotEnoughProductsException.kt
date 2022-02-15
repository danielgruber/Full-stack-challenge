package tech.espero.gruber.fullstackchallenge.exceptions

/**
 * Thrown when not enough products are in stock.
 */
class NotEnoughProductsException(message: String? = null): Exception(message)
