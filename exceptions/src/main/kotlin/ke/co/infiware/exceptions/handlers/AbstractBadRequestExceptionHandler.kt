package ke.co.infiware.exceptions.handlers

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus

/**
 * Extend this for any exception handler that should return a 400 response
 */
@Order(Ordered.LOWEST_PRECEDENCE)
abstract class AbstractBadRequestExceptionHandler<T : Throwable>(exceptionClass: Class<*>)
    : AbstractExceptionHandler<T>(exceptionClass) {

    override fun getStatus(ex: T): HttpStatus? {
        return HttpStatus.BAD_REQUEST
    }
}