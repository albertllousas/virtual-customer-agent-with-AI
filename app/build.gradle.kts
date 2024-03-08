import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    application
    kotlin("plugin.spring") version "1.4.31"
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
}

object Versions {
    const val JUNIT = "5.10.1"
    const val MOCKK = "1.13.9"
    const val SPRING_MOCKK = "3.0.1"
    const val ASSERTJ = "3.25.2"
    const val ARROW = "1.2.1"
    const val KOTEST_ASSERTIONS = "5.8.0"
    const val SPRING_AI_OPENAI = "0.8.0"
    const val TESTCONTAINERS = "1.19.4"
    const val REST_ASSURED = "5.3.0"
}

repositories {
    mavenCentral()
    maven(url = "https://repo.spring.io/milestone")
    maven(url = "https://repo.spring.io/snapshot")
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//    implementation("io.arrow-kt:arrow-core:${Versions.ARROW}")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.2.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter:${Versions.SPRING_AI_OPENAI}")

    testImplementation(group = "io.rest-assured", name = "rest-assured", version = Versions.REST_ASSURED)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:${Versions.MOCKK}")
    testImplementation(group = "com.ninja-squad", name = "springmockk", version = Versions.SPRING_MOCKK)
    testImplementation("org.junit.jupiter:junit-jupiter:${Versions.JUNIT}")
    testImplementation(group = "org.assertj", name = "assertj-core", version = Versions.ASSERTJ)
    testImplementation(group = "io.kotest", name = "kotest-assertions-core-jvm", version = Versions.KOTEST_ASSERTIONS)
    testImplementation(group =  "org.testcontainers", name = "testcontainers", version = Versions.TESTCONTAINERS)
    testImplementation("com.redis:testcontainers-redis:2.0.1")
}

application {
    mainClass.set("pets.AppKt")
}

tasks.apply {
    test {
        maxParallelForks = 1
        enableAssertions = true
        useJUnitPlatform {}
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xinline-classes", "-Xcontext-receivers")
        }
    }
}
