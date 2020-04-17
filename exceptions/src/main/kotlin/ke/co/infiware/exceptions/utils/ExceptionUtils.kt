package ke.co.infiware.exceptions.utils

import ke.co.infiware.exceptions.ExceptionIdMaker
import ke.co.infiware.exceptions.ExceptionMessageParser
import ke.co.infiware.exceptions.MultiErrorException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import javax.annotation.PostConstruct

/**
 *
 * @author Denis Gitonga
 */
class ExceptionUtils internal constructor(
        exceptionIdMaker: ExceptionIdMaker?,
        exceptionMessageParser: ExceptionMessageParser?
) {

    init {
        Companion.exceptionIdMaker = exceptionIdMaker
        Companion.exceptionMessageParser = exceptionMessageParser
    }

    @PostConstruct
    fun postConstruct() {
        NOT_FOUND_EXCEPTION
                .httpStatus(HttpStatus.NOT_FOUND)
                .validate(false, "ke.co.infiware.notFound")
        log.info("NOT_FOUND_EXCEPTION built")
    }

    companion object {
        private var exceptionIdMaker: ExceptionIdMaker? = null
        private var exceptionMessageParser: ExceptionMessageParser? = null

        private var log: Logger = LoggerFactory.getLogger(ExceptionUtils::class.java)
        private var NOT_FOUND_EXCEPTION = MultiErrorException()


        /**
         * Gets a message from messages.properties
         */
        internal fun getMessage(messageKey: String, vararg args: Any): String? =
                exceptionMessageParser?.parseMessage(messageKey = messageKey, args = *arrayOf(args))

        /**
         * Creates a MultiErrorException out of the given parameters
         */
        fun validate(
                expression: Boolean,
                messageKey: String,
                vararg args: Any?
        ): MultiErrorException {
            return validateField(null, expression, messageKey, *args)
        }


        /**
         * Creates a MultiErrorException out of the given parameters
         */
        fun validateField(
                fieldName: String?,
                expression: Boolean,
                messageKey: String,
                vararg args: Any?
        ): MultiErrorException {
            return MultiErrorException().validateField(fieldName, expression, messageKey, args)
        }

        fun <T : Throwable> getExceptionId(ex: T?): String? {
            val root = getRootException(ex = ex)
            return exceptionIdMaker?.make(t = root)
        }

        private fun <T : Throwable> getRootException(ex: T?): T? {
            if (ex == null)
                return null

            var tempEx: T? = ex
            while (tempEx?.cause != null) {
                @Suppress("UNCHECKED_CAST")
                tempEx = tempEx.cause as? T
            }

            return tempEx
        }
    }

}