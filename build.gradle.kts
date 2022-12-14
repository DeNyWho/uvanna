import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.0.14.RELEASE"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

springBoot {
    mainClass.set("com.example.uvanna.UvannaApplicationKt")
}
tasks.getByName<Jar>("jar") {
    enabled = false
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {

    // KotlinX Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-data-rest:1.6.12")
    implementation("org.springdoc:springdoc-openapi-ui:1.6.11")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.12")

    // Beautiful Logger
    implementation("com.google.code.gson:gson:2.10")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Selenium WebDrive
    implementation("org.seleniumhq.selenium:selenium-java:4.2.1")

    // Ktor - Client
    implementation ("io.ktor:ktor-client-core:2.1.3")
    implementation ("io.ktor:ktor-client-json-jvm:2.1.3")
    implementation ("io.ktor:ktor-client-okhttp:2.1.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.1.3")
    implementation("io.ktor:ktor-serialization-kotlinx-cbor:2.1.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.1.3")
    implementation ("io.ktor:ktor-client-serialization:2.1.3")
    implementation ("io.ktor:ktor-client-logging:2.1.3")
    implementation ("ch.qos.logback:logback-classic:1.2.10")
    implementation("it.skrape:skrapeit:1.2.2")

    // Chrome Driver for Selenium
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.2.1")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.thymeleaf:thymeleaf:3.0.15.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:2.1.3.RELEASE")
//    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "18"
    }
    kotlinOptions.jvmTarget = "18"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
