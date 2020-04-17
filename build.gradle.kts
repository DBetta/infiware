import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.6.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.9.RELEASE" apply false
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.spring") version "1.3.71" apply false
    kotlin("kapt") version "1.3.71" apply false
}

extra["springCloudVersion"] = "Hoxton.SR3"

allprojects {
    group = "ke.co.infiware"
    version = "0.0.1-SNAPSHOT"

    apply<IdeaPlugin>()
    apply<EclipsePlugin>()

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    repositories {
        mavenCentral()
        /*maven { url = uri("https://repo.spring.io/milestone") }*/
    }
}

subprojects {
    apply(plugin = "java")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")


        implementation("org.apache.commons:commons-lang3")
    }

    configure<JavaPluginExtension> {
        java.sourceCompatibility = JavaVersion.VERSION_11
        java.targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        create("integrationTest") {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output
        }
    }

    // integration test setup
    val integrationTestImplementation: Configuration by configurations.getting {
        extendsFrom(configurations.implementation.get())
        extendsFrom(configurations.testImplementation.get())
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

    /*tasks.check {
        dependsOn(integrationTest)
    }*/
    // end integration test setup

}


