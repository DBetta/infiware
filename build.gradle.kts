import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.0.M4"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.spring") version "1.3.71"
}

allprojects {
    group = "ke.co.infiware"
    version = "0.0.1-SNAPSHOT"

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

subprojects {
    apply(plugin = "java")

    configure<JavaPluginExtension> {
        java.sourceCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        create("integrationTest") {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output
        }
    }

    val integrationTestImplementation: Configuration by configurations.getting {
        extendsFrom(configurations.implementation.get())
    }
    val integrationTestRuntimeOnly: Configuration by configurations.getting {
        extendsFrom(configurations.runtimeOnly.get())
    }

    val integrationTest = tasks.register<Test>("integrationTest") {
        description = "Run Integration test"
        group = "verification"

        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        shouldRunAfter("test")

    }

    tasks.check {
        dependsOn(integrationTest)
    }

}


