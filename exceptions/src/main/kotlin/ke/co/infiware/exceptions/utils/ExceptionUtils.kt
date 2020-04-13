package ke.co.infiware.exceptions.utils

import ke.co.infiware.exceptions.ExceptionIdMaker
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
        messageSource: MessageSource?,
        validator: LocalValidatorFactoryBean?,
        exceptionIdMaker: ExceptionIdMaker?
) {

    init {
        Companion.messageSource = messageSource
        Companion.validator = validator
        Companion.exceptionIdMaker = exceptionIdMaker
    }

    @PostConstruct
    fun postConstruct() {
        NOT_FOUND_EXCEPTION
                .httpStatus(HttpStatus.NOT_FOUND)
                .validate(false, "ke.co.infiware.notFound")
        log.info("NOT_FOUND_EXCEPTION built")
    }

    companion object {
        private var messageSource: MessageSource? = null
        private var validator: LocalValidatorFactoryBean? = null
        private var exceptionIdMaker: ExceptionIdMaker? = null

        private var log: Logger = LoggerFactory.getLogger(ExceptionUtils::class.java)
        private var NOT_FOUND_EXCEPTION = MultiErrorException()


        /**
         * Gets a message from messages.properties
         */
        fun getMessage(messageKey: String, vararg args: Any): String? {
            return if (messageSource == null)
                "ApplicationContext unavailable, probably unit test going on"
            // http://stackoverflow.com/questions/10792551/how-to-obtain-a-current-user-locale-from-spring-without-passing-it-as-a-paramete
            else messageSource?.getMessage(messageKey, args, LocaleContextHolder.getLocale())
        }

        /**
         * Creates a MultiErrorException out of the given parameters
         */
        fun validate(
                valid: Boolean,
                messageKey: String,
                vararg args: Any?
        ): MultiErrorException {
            return validateField(null, valid, messageKey, *args)
        }


        /**
         * Creates a MultiErrorException out of the given parameters
         */
        fun validateField(
                fieldName: String?,
                valid: Boolean,
                messageKey: String,
                vararg args: Any?
        ): MultiErrorException {
            return MultiErrorException().validateField(fieldName, valid, messageKey, args)
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