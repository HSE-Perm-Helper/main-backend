dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation(project(":main-module"))
    implementation(project(":common"))
    implementation(project(":remote-timetable"))
}

tasks.bootJar {
    enabled = false
}