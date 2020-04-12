package ke.co.infiware.exceptions

import org.springframework.http.HttpStatus

/**
 * Error DTO, to be sent as response body
 * in case of errors
 *
 * @author Denis Gitonga
 */
data class ErrorResponse(
        /**
         * Error id
         */
        val exceptionId: String? = null,

        /**
         * Return the reason phrase of this status code from [HttpStatus]
         */
        val error: String? = null,

        /**
         * The error message
         */
        val message: String? = null,

        /**
         * The [HttpStatus] code
         */
        val status: Int? = null,

        /**
         * List [InfiwareFieldError]s
         */
        val errors: List<InfiwareFieldError> = emptyList()
) {

    /**
     * A complete [ErrorResponse] is considered to have either status or message otherwise not complete.
     */
    fun incomplete(): Boolean = status == null || message == null
}