plugins {
    kotlin("plugin.jpa") version "2.2.21"
}

val apachePoiVersion = "5.2.3"
val caffeineVersion = "3.2.0"
val postgresVersion = "42.6.0"
val springDocStarterVersion = "2.2.0"
val springDocKotlinVersion = "2.2.0"
val jsoupVersion = "1.16.1"
val kotlinLoggingVersion = "7.0.3"

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

dependencies {
    implementation(libs.quartz)
    implementation(libs.spring.quartz)
    implementation(libs.spring.tx)

    implementation(libs.logback.encoder)

    implementation("com.github.ben-manes.caffeine:caffeine")

    implementation("org.liquibase:liquibase-core")
    implementation("org.postgresql:postgresql:${postgresVersion}")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.apache.poi:poi:${apachePoiVersion}")
    implementation("org.apache.poi:poi-ooxml:${apachePoiVersion}")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(libs.jackson.datatype.jsr310)
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-jackson")

    implementation("io.github.oshai:kotlin-logging-jvm:${kotlinLoggingVersion}")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocStarterVersion}")

    implementation("org.jsoup:jsoup:${jsoupVersion}")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
        exclude(group = "org.mockito", module = "mockito-junit-jupiter")
    }
    testImplementation("org.springframework.kafka:spring-kafka-test")

    testImplementation(libs.mockK)
    testImplementation(libs.spring.mockk)
}

tasks.bootJar {
    enabled = false
}

