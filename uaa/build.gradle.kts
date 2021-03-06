
plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}



dependencies {
    implementation(project(":commons"))
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    /*implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")*/
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    /*runtimeOnly("dev.miku:r2dbc-mysql")*/
    runtimeOnly("mysql:mysql-connector-java")
    implementation("org.liquibase:liquibase-core")

    // map-struct
    implementation("org.mapstruct:mapstruct:1.3.1.Final")
    implementation("com.github.pozo:mapstruct-kotlin:1.3.1.2")
    kapt("org.mapstruct:mapstruct-processor:1.3.1.Final")
    kapt("com.github.pozo:mapstruct-kotlin-processor:1.3.1.2")
    //

    // google libphonenumber
    implementation("com.googlecode.libphonenumber:libphonenumber:8.12.1")
    implementation("commons-validator:commons-validator:1.6")
    //

    // graphql dependencies
    implementation("com.graphql-java-kickstart:graphql-kickstart-spring-boot-autoconfigure-webflux:7.0.1")
    implementation("com.graphql-java-kickstart:graphql-kickstart-spring-boot-starter-tools:7.0.1")
    // runtimeOnly("com.graphql-java-kickstart:graphiql-spring-boot-starter:7.0.1")
    testImplementation("com.graphql-java-kickstart:graphql-spring-boot-starter-test:7.0.1")
    //

    // swagger
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.3.0")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.3.0")

    // cloud dependencies
    implementation("org.springframework.cloud:spring-cloud-starter-consul-discovery")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    //

    kapt("org.springframework.boot:spring-boot-configuration-processor")


    // test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kapt {
    correctErrorTypes = true
}

