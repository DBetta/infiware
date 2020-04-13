package ke.co.infiware.exceptions.handlers

import ke.co.infiware.exceptions.InfiwareFieldError
import ke.co.infiware.exceptions.MultiErrorException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class MultiErrorExceptionHandler
    : AbstractExceptionHandler<MultiErrorException>(MultiErrorException::class.java) {
    init {
        log.info("Created")
    }

    override fun getExceptionId(ex: MultiErrorException?): String? {
        return ex?.exceptionId ?: super.getExceptionId(ex)
    }

    override fun getMessage(ex: MultiErrorException?): String? {
        return ex?.message
    }

    override fun getStatus(ex: MultiErrorException?): HttpStatus? {
        return ex?.status
    }

    override fun getErrors(ex: MultiErrorException?): List<InfiwareFieldError> {
        return ex?.errors ?: emptyList()
    }

}