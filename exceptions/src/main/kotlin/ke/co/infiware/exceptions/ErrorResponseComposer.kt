package ke.co.infiware.exceptions

import ke.co.infiware.exceptions.handlers.AbstractExceptionHandler
import org.springframework.core.annotation.AnnotationAwareOrderComparator
import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.stream.Collectors

/**
 * Given an exception, builds a response.
 *
 * @author Denis Gitonga
 */
class ErrorResponseComposer<T : Throwable> internal constructor(
        handlers: List<AbstractExceptionHandler<T>>?
) {

    private val handlers = (handlers ?: emptyList()).stream()
            .collect(Collectors.toMap(
                    Function { it.exceptionClass },
                    Function.identity(),
                    BinaryOperator { handler1, handler2 ->
                        if (AnnotationAwareOrderComparator.INSTANCE.compare(handler1, handler2) < 0) handler1
                        else handler2
                    }))

    /**
     * Given an exception, finds a handler for
     * building the response and uses that to build and return the response
     */
    fun compose(ex: T): ErrorResponse? {
        var handler: AbstractExceptionHandler<T>? = null
        var tempEx: T? = ex

        // find a handler for the exception
        // if no handler is found,
        // loop into for its cause (ex.getCause())
        while (tempEx != null) {
            handler = handlers[ex.javaClass]

            if (handler != null) // found handler
                break

            @Suppress("UNCHECKED_CAST")
            tempEx = tempEx.cause as? T
        }

        return handler?.getErrorResponse(ex)
    }

}