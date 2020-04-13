package ke.co.infiware.exceptions

import org.apache.commons.lang3.StringUtils
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.support.WebExchangeBindException
import javax.validation.ConstraintViolation

/**
 *
 * @author Denis Gitonga
 */
class InfiwareFieldError internal constructor(
        /**
         * Name of the field. Null in case of form error
         */
        val field: String?,

        /**
         * Error code. Typically the I18n message-code.
         */
        val code: String?,

        /**
         * Error message
         */
        val message: String?
) {

    companion object {
        private fun of(constraintViolations: ConstraintViolation<*>): InfiwareFieldError {
            // Get the field name by removing the first part of the propertyPath.
            // (The first part would be the service method name)
            val field = StringUtils.substringAfter(constraintViolations.propertyPath.toString(), ".")

            return InfiwareFieldError(
                    field = field,
                    message = constraintViolations.message,
                    code = constraintViolations.messageTemplate
            )
        }

        private fun of(fieldError: FieldError): InfiwareFieldError =
                InfiwareFieldError(
                        field = fieldError.objectName + "." + fieldError.field,
                        message = fieldError.defaultMessage,
                        code = fieldError.code
                )

        private fun of(objectError: ObjectError): InfiwareFieldError =
                InfiwareFieldError(
                        field = objectError.objectName,
                        message = objectError.defaultMessage,
                        code = objectError.code
                )

        /**
         * Converts a set of ConstraintViolations
         * to a list of FieldErrors
         *
         * @param constraintViolations
         */
        @JvmStatic
        fun getErrors(constraintViolations: Set<ConstraintViolation<*>>): List<InfiwareFieldError> {
            return constraintViolations.map { of(it) }
        }

        /**
         * Get the list of errors from [WebExchangeBindException]
         */
        @JvmStatic
        fun getErrors(ex: WebExchangeBindException?): List<InfiwareFieldError> {
            val errors = ex?.fieldErrors?.map { of(it) }?.toMutableList() ?: mutableListOf()
            val globalErrors = ex?.globalErrors?.map { of(it) } ?: emptyList()
            errors += globalErrors
            return errors
        }
    }

}