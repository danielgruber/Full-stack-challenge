package tech.espero.gruber.fullstackchallenge.exceptions

/**
 * Thrown if not enough coins were deposited.
 */
class NotEnoughMoneyException(message: String? = null): Exception(message)
