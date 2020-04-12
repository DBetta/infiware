package ke.co.infiware.exceptions.handlers

import ke.co.infiware.exceptions.ErrorResponse
import ke.co.infiware.exceptions.InfiwareFieldError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus

/**
 * To be extended by any class that want's to code exception handling
 *
 * @author Denis Gitonga
 */
abstract class AbstractExceptionHandler<T : Throwable>(
        /**
         * The exception thrown
         */
        val exceptionClass: Class<*>
) {
    protected val log: Logger = LoggerFactory.getLogger(AbstractExceptionHandler::class.java)


    /**
     * Get the unique id to the exception
     */
    open fun getExceptionId(ex: T): String? {
        TODO()
    }

    /**
     * Get the message
     */
    open fun getMessage(ex: T): String? = ex.message

    /**
     * Get the status from the exception
     */
    open fun getStatus(ex: T): HttpStatus? {
        return null
    }

    /**
     * Get the list of [InfiwareFieldError]s
     */
    open fun getErrors(ex: T): List<InfiwareFieldError> {
        return emptyList()
    }

    /**
     * Get's an instance of [ErrorResponse] from the exception.
     */
    fun getErrorResponse(ex: T): ErrorResponse {
        val errors = getErrors(ex = ex)
        val exceptionId = getExceptionId(ex = ex)
        val message = getMessage(ex = ex)

        val httpStatus = getStatus(ex = ex)
        val status = httpStatus?.value()
        val error = httpStatus?.reasonPhrase

        return ErrorResponse(
                exceptionId = exceptionId,
                message = message,
                errors = errors,
                status = status,
                error = error
        )
    }
}