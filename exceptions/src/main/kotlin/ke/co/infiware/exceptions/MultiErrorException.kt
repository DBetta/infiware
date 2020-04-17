package ke.co.infiware.exceptions

import ke.co.infiware.exceptions.utils.ExceptionUtils
import org.springframework.http.HttpStatus

/**
 *
 * @author Denis Gitonga
 */
class MultiErrorException internal constructor(
        /**
         * The http status to be returned
         */
        var status: HttpStatus? = HttpStatus.UNPROCESSABLE_ENTITY,

        /**
         * Set this if you need to customize exceptionId
         */
        var exceptionId: String? = null
) : RuntimeException() {
    // list of errors
    val errors = ArrayList<InfiwareFieldError>(10)

    override val message: String?
        get() {
            if (errors.isEmpty())
                return null

            return errors.firstOrNull()?.message
        }

    /**
     * Set the [HttpStatus]
     */
    fun httpStatus(status: HttpStatus): MultiErrorException {
        this.status = status;
        return this
    }

    /**
     * Set the exceptionId
     */
    fun exceptionId(exceptionId: String): MultiErrorException {
        this.exceptionId = exceptionId
        return this
    }


    /**
     * Adds a field-error if the given condition isn't true
     */
    fun validateField(fieldName: String?,
                      expression: Boolean,
                      messageKey: String,
                      vararg args: Any): MultiErrorException {
        if (expression.not())
            errors.add(
                    InfiwareFieldError(
                            field = fieldName,
                            code = messageKey,
                            message = ExceptionUtils.getMessage(messageKey = messageKey, args = *arrayOf(args))
                    )
            )
        return this
    }

    /**
     * Adds a global-error if the given condition isn't true
     */
    fun validate(
            expression: Boolean,
            messageKey: String,
            vararg args: Any): MultiErrorException? {

        // delegate
        return validateField(null, expression, messageKey, args)
    }

    /**
     * Throws the exception, if there are accumulated errors
     */
    fun go() {
        if (errors.isNotEmpty()) throw this
    }

}