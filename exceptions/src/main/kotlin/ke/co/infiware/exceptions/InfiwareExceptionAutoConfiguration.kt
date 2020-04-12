package ke.co.infiware.exceptions

import ke.co.infiware.exceptions.handlers.AbstractExceptionHandler
import ke.co.infiware.exceptions.utils.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

/**
 *
 * @author Denis Gitonga
 */
@Configuration
@AutoConfigureBefore(ValidationAutoConfiguration::class)
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
            messageSource: MessageSource?,
            validator: LocalValidatorFactoryBean?,
            exceptionIdMaker: ExceptionIdMaker?
    ): ExceptionUtils {
        return ExceptionUtils(
                messageSource = messageSource,
                validator = validator,
                exceptionIdMaker = exceptionIdMaker
        )
    }

}