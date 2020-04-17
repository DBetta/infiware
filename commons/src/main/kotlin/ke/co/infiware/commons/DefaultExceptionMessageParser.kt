package ke.co.infiware.commons

import ke.co.infiware.commons.utils.I18nUtils
import ke.co.infiware.exceptions.ExceptionMessageParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Denis Gitonga
 */
class DefaultExceptionMessageParser : ExceptionMessageParser {
    private val log: Logger = LoggerFactory.getLogger(DefaultExceptionMessageParser::class.java)

    override fun parseMessage(messageKey: String, vararg args: Any): String? {
        log.debug("Registering exception message parser")

        return I18nUtils.getMessage(messageKey, args)
    }
}