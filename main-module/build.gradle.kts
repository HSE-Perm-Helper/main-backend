val apachePoiVersion = "5.2.3"
val caffeineVersion = "3.2.0"
val jsoupVersion = "1.16.1"

dependencies {
    api(project(":persistence"))

    implementation(libs.quartz)
    implementation(libs.spring.quartz)
    implementation(libs.spring.tx)

    implementation(libs.logback.encoder)

    implementation("com.github.ben-manes.caffeine:caffeine")

    implementation("org.apache.poi:poi:${apachePoiVersion}")
    implementation("org.apache.poi:poi-ooxml:${apachePoiVersion}")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation(libs.jackson.datatype.jsr310)
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-jackson")

    implementation("org.jsoup:jsoup:${jsoupVersion}")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
        exclude(group = "org.mockito", module = "mockito-junit-jupiter")
    }

    testImplementation(libs.mockK)
    testImplementation(libs.spring.mockk)
}

tasks.bootJar {
    enabled = false
}

