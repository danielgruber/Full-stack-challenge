package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Thrown if not enough coins were deposited.
 */
class NotEnoughMoneyException(message: String? = null): StatusException(message, HttpStatus.PAYMENT_REQUIRED)
