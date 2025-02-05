import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.jpa") version "1.8.22"
    id("jacoco")
}

val springCloudVersion by extra("2022.0.4")
val apachePoiVersion = "5.2.3"
val caffeineVersion = "3.2.0"
val postgresVersion = "42.6.0"
val slf4jVersion = "2.0.0"
val springDocStarterVersion = "2.2.0"
val springDocKotlinVersion = "1.7.0"
val mockitoVersion = "5.11.0"
val mockitoKotlinVersion = "5.4.0"
val jsoupVersion = "1.16.1"

group = "com.melowetty"
version = "1.03.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

dependencies {
    implementation("com.github.ben-manes.caffeine:caffeine")

    implementation("org.liquibase:liquibase-core")
    implementation("org.postgresql:postgresql:${postgresVersion}")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.apache.poi:poi:${apachePoiVersion}")
    implementation("org.apache.poi:poi-ooxml:${apachePoiVersion}")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    implementation("org.slf4j:slf4j-api:${slf4jVersion}")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocStarterVersion}")

    implementation("org.jsoup:jsoup:${jsoupVersion}")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    runtimeOnly("org.springdoc:springdoc-openapi-kotlin:${springDocKotlinVersion}")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${mockitoKotlinVersion}")
}
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    archiveFileName.set("app.jar")
}

tasks.bootJar {
    archiveFileName.set("app-standalone.jar")
}

tasks.test {
    testLogging {
        events("passed", "failed", "skipped")
    }
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.9"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(false)
        csv.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}