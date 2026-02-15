val kotlinLoggingVersion = "7.0.3"
val springDocStarterVersion = "2.2.0"

dependencies {
    api("io.github.oshai:kotlin-logging-jvm:${kotlinLoggingVersion}")

    api("org.springframework.boot:spring-boot-starter-webmvc")

    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocStarterVersion}")
}

tasks.bootJar {
    enabled = false
}