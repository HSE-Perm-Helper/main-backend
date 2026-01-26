dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    implementation(project(":remote-timetable"))
    implementation(project(":main-module"))
}

tasks.bootJar {
    archiveFileName.set("app-standalone.jar")
}