package tech.espero.gruber.fullstackchallenge.exceptions

import org.springframework.http.HttpStatus

/**
 * Base Exception for all exceptions here.
 */
open class StatusException(message: String? = null, val httpStatus: HttpStatus): Exception(message)
