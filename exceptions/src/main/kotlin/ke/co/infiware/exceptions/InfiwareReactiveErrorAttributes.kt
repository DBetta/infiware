package ke.co.infiware.exceptions

import ke.co.infiware.exceptions.utils.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.web.reactive.function.server.ServerRequest

/**
 *
 * @author Denis Gitonga
 */
class InfiwareReactiveErrorAttributes<T : Throwable> internal constructor(
        /**
         * Component that actually builds the error response
         */
        private val errorResponseComposer: ErrorResponseComposer<T>?
) : DefaultErrorAttributes() {

    init {
        log.info("Created")
    }

    override fun getErrorAttributes(request: ServerRequest?, includeStackTrace: Boolean): MutableMap<String, Any> {
        val errorAttributes = super.getErrorAttributes(request, includeStackTrace)
        addInfiwareErrorAttributes(errorAttributes = errorAttributes, request = request)

        return errorAttributes
    }

    /**
     * Handles exceptions
     */
    private fun addInfiwareErrorAttributes(errorAttributes: MutableMap<String, Any?>,
                                           request: ServerRequest?
    ) {
        val ex = getError(request)

        @Suppress("UNCHECKED_CAST")
        val errorResponse = errorResponseComposer?.compose(ex as? T)


        // check for nulls - errorResponse may have left something for the DefaultErrorAttributes
        if (errorResponse?.exceptionId != null)
            errorAttributes["exceptionId"] = errorResponse.exceptionId

        if (errorResponse?.message != null)
            errorAttributes["message"] = errorResponse.message

        val status = errorResponse?.status

        if (status != null) {
            errorAttributes["status"] = status
            errorAttributes["error"] = errorResponse.error
        }

        if (errorResponse?.errors?.isNotEmpty() == true)
            errorAttributes["errors"] = errorResponse.errors

        if (errorAttributes["exceptionId"] == null)
            errorAttributes["exceptionId"] = ExceptionUtils.getExceptionId(ex = ex)

    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(InfiwareReactiveErrorAttributes::class.java)
    }
}