val kotlinLoggingVersion = "7.0.3"
val springDocStarterVersion = "2.2.0"

dependencies {
    api("io.github.oshai:kotlin-logging-jvm:${kotlinLoggingVersion}")

    compileOnly("org.springframework.boot:spring-boot-starter-webmvc")

    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocStarterVersion}")
}