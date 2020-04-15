package ke.co.infiware.uaa.useradministration.enums

import com.fasterxml.jackson.annotation.JsonValue

/**
 *
 * @author Denis Gitonga
 */
enum class YesNoType(
        @JsonValue val value: String
) {
    YES(value = "Y"),
    NO(value = "N");

    companion object {
        /**
         *
         */
        fun fromValue(rawValue: String?): YesNoType {
            for (value in values()) {
                if (value.value.equals(rawValue, ignoreCase = true)) {
                    return value
                }
            }

            throw IllegalArgumentException("Invalid YesNo type: ${rawValue}. Allowed values are: ${values().map { it.value }}")
        }
    }
}