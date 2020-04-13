package ke.co.infiware.exceptions.handlers

import ke.co.infiware.exceptions.InfiwareFieldError
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebExchangeBindException

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
class WebExchangeBindExceptionHandler
    : AbstractValidationExceptionHandler<WebExchangeBindException>(WebExchangeBindException::class.java) {

    init {
        log.info("Created")
    }

    override fun getErrors(ex: WebExchangeBindException?): List<InfiwareFieldError> {
        return InfiwareFieldError.getErrors(ex)
    }
}