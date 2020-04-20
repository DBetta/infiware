package ke.co.infiware.uaa

import ke.co.infiware.uaa.configuration.UaaProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(UaaProperties::class, LiquibaseProperties::class)
class UaaApplication

fun main(args: Array<String>) {
    runApplication<UaaApplication>(*args)
}
