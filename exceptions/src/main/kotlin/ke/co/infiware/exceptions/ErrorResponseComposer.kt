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
    init {
        handlers!!
    }

    @Suppress("RemoveExplicitTypeArguments")
    private val handlers: Map<Class<*>, AbstractExceptionHandler<T>> = (handlers ?: emptyList()).stream()
            .collect(
                    Collectors.toMap<AbstractExceptionHandler<T>, Class<*>, AbstractExceptionHandler<T>>(
                            Function { it.exceptionClass },
                            Function.identity(),
                            BinaryOperator { handler1, handler2 ->
                                if (AnnotationAwareOrderComparator.INSTANCE.compare(handler1, handler2) < 0) handler1
                                else handler2
                            })
            )

    /**
     * Given an exception, finds a handler for
     * building the response and uses that to build and return the response
     */
    fun compose(ex: T?): ErrorResponse? {
        var handler: AbstractExceptionHandler<T>? = null
        var tempEx = ex

        // find a handler for the exception
        // if no handler is found,
        // loop into for its cause (ex.getCause())
        while (tempEx != null) {
            handler = if (ex?.javaClass != null)
                handlers[ex.javaClass]
            else null

            if (handler != null) // found handler
                break

            @Suppress("UNCHECKED_CAST")
            tempEx = tempEx.cause as? T
        }

        return handler?.getErrorResponse(ex)
    }

}