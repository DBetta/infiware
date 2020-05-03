package ke.co.infiware.uaa.configuration

import graphql.schema.GraphQLScalarType
import ke.co.infiware.uaa.graphql.scalars.UUIDScalarType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *
 * @author Denis Gitonga
 */
@Configuration
class GraphQLConfiguration {

    @Bean
    fun uuidScalarType(): GraphQLScalarType = UUIDScalarType.build()
}