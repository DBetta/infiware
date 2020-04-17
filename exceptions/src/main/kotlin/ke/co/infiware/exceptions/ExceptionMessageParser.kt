package ke.co.infiware.exceptions

/**
 *
 * @author Denis Gitonga
 */
interface ExceptionMessageParser {
    /**
     *  Parse messageKey from messages.properties
     */
    fun parseMessage(messageKey: String, vararg args: Any): String?
}