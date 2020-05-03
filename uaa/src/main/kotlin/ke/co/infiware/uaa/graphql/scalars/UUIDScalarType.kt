package ke.co.infiware.uaa.graphql.scalars

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import java.util.*

/**
 *
 * @author Denis Gitonga
 */
object UUIDScalarType {
    fun build() = GraphQLScalarType.newScalar()
            .name("UUID")
            .description("java.util.UUID")
            .coercing(uuidCoercing)
            .build()

    private val uuidCoercing = object : Coercing<UUID?, String?> {
        override fun parseValue(input: Any?): UUID? {
            if (input == null)
                return null

            return when (input) {
                is String -> UUID.fromString(input)
                else -> throw IllegalArgumentException("Unable to serialize " + input
                        + " as UUID to String. Wrong Type. Expected String.class, Got " + input::class.java
                )
            }
        }

        override fun parseLiteral(input: Any?): UUID? {
            if (input == null)
                return null

            return when (input) {
                is StringValue -> UUID.fromString(input.value)
                else -> throw  IllegalArgumentException("Unable to serialize " + input
                        + " as UUID to String. Wrong Type. Expected StringValue.class, Got " + input::class.java
                )
            }
        }

        override fun serialize(dataFetcherResult: Any?): String? {

            if (dataFetcherResult == null)
                return null

            return when (dataFetcherResult) {
                is UUID -> dataFetcherResult.toString()
                else -> throw IllegalArgumentException("Unable to serialize " + dataFetcherResult +
                        " as UUID to String. Wrong Type. Expected UUID.class, Got " + dataFetcherResult::class.java)
            }
        }

    }
}