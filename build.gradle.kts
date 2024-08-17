import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.jpa") version "1.8.22"
    id("jacoco")
}
group = "com.melowetty"
version = "1.02.0-beta"

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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.postgresql:postgresql:42.6.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.1.2")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.slf4j:slf4j-api:2.0.0")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core:5.11.0")
    /* [Excel Parsing Module] */
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    /* [Swagger] */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    runtimeOnly("org.springdoc:springdoc-openapi-kotlin:1.7.0")
    // jsoup HTML parser library @ https://jsoup.org/
    implementation("org.jsoup:jsoup:1.16.1")
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