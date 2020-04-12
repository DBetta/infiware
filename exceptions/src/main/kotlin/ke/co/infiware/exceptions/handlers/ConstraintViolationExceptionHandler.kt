package ke.co.infiware.exceptions.handlers

import ke.co.infiware.exceptions.InfiwareFieldError
import ke.co.infiware.exceptions.InfiwareFieldError.Companion.getErrors
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.validation.ConstraintViolationException

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class ConstraintViolationExceptionHandler : AbstractValidationExceptionHandler<ConstraintViolationException>(
        ConstraintViolationException::class.java
) {
    init {
        log.info("Created")
    }

    override fun getErrors(ex: ConstraintViolationException): List<InfiwareFieldError> {
        return getErrors(ex.constraintViolations)
    }
}