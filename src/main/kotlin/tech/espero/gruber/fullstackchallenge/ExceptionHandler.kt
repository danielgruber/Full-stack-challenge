package tech.espero.gruber.fullstackchallenge

import com.fasterxml.jackson.core.JsonProcessingException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
import tech.espero.gruber.fullstackchallenge.exceptions.StatusException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Generic Error Handler for application.
 * Can be easily extended by adding handleException methods annotated with @ExceptionHandler(CustomException::class).
 * Then the error can be treated in a special way.
 *
 * By default, this application uses [StatusException] in order to allow an exception to define its own HTTP status.
 */
@ControllerAdvice
class ExceptionHandler
{
    private val logger: Logger = LoggerFactory.getLogger(ExceptionHandler::class.java)

    @ExceptionHandler(UsernameNotFoundException::class)
    @ResponseBody
    fun handleException(
        e: UsernameNotFoundException,
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ): ResponseEntity<*> {
        logger.warn("Username not found: $e", e)

        // Override if the exception is known by spring
        val defaultHandlerExceptionResolver = DefaultHandlerExceptionResolver()
        defaultHandlerExceptionResolver.resolveException(request!!, response!!, this, e)

        // Standard
        val map = createResponse(e, HttpStatus.NOT_FOUND, request)
        return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, map)
    }

    @ExceptionHandler(Exception::class)
    @ResponseBody
    fun handleException(
        e: Exception,
        request: HttpServletRequest?,
        response: HttpServletResponse?
    ): ResponseEntity<*> {
        logger.warn("Generic Exception: $e", e)

        // Override if the exception is known by spring
        val defaultHandlerExceptionResolver = DefaultHandlerExceptionResolver()
        defaultHandlerExceptionResolver.resolveException(request!!, response!!, this, e)

        val status = when (e) {
            is StatusException -> {
                e.httpStatus
            }
            is HttpRequestMethodNotSupportedException -> {
                HttpStatus.BAD_REQUEST
            }
            else -> {
                HttpStatus.INTERNAL_SERVER_ERROR
            }
        }

        // Standard
        val map = createResponse(e, status, request)
        return toResponse(status, map)
    }

    private fun createResponse(
        e: java.lang.Exception,
        httpStatus: HttpStatus,
        request: HttpServletRequest
    ): Map<String, Any> {
        // Standard
        val response = mutableMapOf<String, Any>()
        response["timestamp"] = Date()
        response["status"] = httpStatus.value()
        response["error"] = httpStatus.reasonPhrase
        response["exception"] = e.javaClass.simpleName
        response["message"] = e.message ?: ""
        response["path"] = request.requestURI
        return response
    }

    @Throws(JsonProcessingException::class)
    private fun toResponse(statusCode: HttpStatus, map: Map<String, Any>): ResponseEntity<*> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        return ResponseEntity(map, headers, statusCode)
    }
}
