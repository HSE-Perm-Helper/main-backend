plugins {
    kotlin("plugin.jpa") version "2.2.21"
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

val postgresVersion = "42.6.0"

dependencies {
    api(project(":common"))

    implementation("org.liquibase:liquibase-core")
    implementation("org.postgresql:postgresql:${postgresVersion}")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jackson")
}