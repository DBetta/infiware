package ke.co.infiware.commons

import ke.co.infiware.commons.utils.I18nUtils
import ke.co.infiware.exceptions.ExceptionMessageParser
import ke.co.infiware.exceptions.InfiwareExceptionAutoConfiguration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *
 * @author Denis Gitonga
 */
@Configuration
@AutoConfigureBefore(InfiwareExceptionAutoConfiguration::class)
class InfiwareCommonsAutoConfiguration {

    private val log: Logger = LoggerFactory.getLogger(InfiwareCommonsAutoConfiguration::class.java)

    /**
     * Configures ExceptionMessageParser
     */
    @Bean
    @ConditionalOnMissingBean(ExceptionMessageParser::class)
    fun exceptionMessageParser(): ExceptionMessageParser {
        log.debug("Configuring DefaultExceptionMessageParser")
        return DefaultExceptionMessageParser()
    }

    @Bean
    fun i18Utils(
         messageSource: MessageSource
    ): I18nUtils {
        log.debug("Configuring I18Utils")
        return I18nUtils(messageSource)
    }
}