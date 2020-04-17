package ke.co.infiware.exceptions

import ke.co.infiware.exceptions.handlers.AbstractExceptionHandler
import ke.co.infiware.exceptions.utils.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 *
 * @author Denis Gitonga
 */
@Configuration
@AutoConfigureBefore(ValidationAutoConfiguration::class,
        WebFluxAutoConfiguration::class,
        ErrorWebFluxAutoConfiguration::class)
@ComponentScan(basePackageClasses = [AbstractExceptionHandler::class])
class InfiwareExceptionAutoConfiguration {

    private val log: Logger = LoggerFactory.getLogger(InfiwareExceptionAutoConfiguration::class.java)

    /**
     * Configures ErrorResponseComposer if missing
     */
    @Bean
    @ConditionalOnMissingBean(ErrorResponseComposer::class)
    fun <T : Throwable> errorResponseComposer(handlers: List<AbstractExceptionHandler<T>>?): ErrorResponseComposer<T> {
        log.info("Configuring ErrorResponseComposer")
        return ErrorResponseComposer(handlers)
    }

    /**
     * Configures an Error Attributes if missing
     */
    @Bean
    @ConditionalOnMissingBean(ErrorAttributes::class)
    fun <T : Throwable> errorAttributes(errorResponseComposer: ErrorResponseComposer<T>?): ErrorAttributes {
        log.info("Configuring InfiwareReactiveErrorAttributes")
        return InfiwareReactiveErrorAttributes<T>(errorResponseComposer = errorResponseComposer)
    }


    /**
     * Configures ExceptionCodeMaker if missing
     */
    @Bean
    @ConditionalOnMissingBean(ExceptionIdMaker::class)
    fun exceptionIdMaker(): ExceptionIdMaker {
        log.info("Configuring ExceptionIdMaker")
        return object : ExceptionIdMaker {
            override fun make(t: Throwable?): String? = t?.javaClass?.simpleName

        }
    }

    /**
     * Configure [ExceptionUtils]
     */
    @Bean
    fun exceptionUtils(
            exceptionIdMaker: ExceptionIdMaker?,
            exceptionMessageParser: ObjectProvider<ExceptionMessageParser>
    ): ExceptionUtils {
        return ExceptionUtils(
                exceptionIdMaker = exceptionIdMaker,
                exceptionMessageParser = exceptionMessageParser.ifAvailable
        )
    }

}