package ke.co.infiware.uaa.utils

import ke.co.infiware.commons.utils.I18nUtils

/**
 *
 * @author Denis Gitonga
 */
const val TOKEN_PREFIX = "Bearer "
const val TOKEN_PREFIX_LENGTH = 7
const val TOKEN_RESPONSE_HEADER_NAME = "Infiware-Authorization"

fun getI18Message(messageKey: String, vararg args: Any): String? {
    return I18nUtils.getMessage(messageKey, args)
}