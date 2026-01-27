dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    implementation(project(":api"))
    implementation(project(":remote-timetable"))
    implementation(project(":main-module"))
}

tasks.bootJar {
    archiveFileName.set("app-standalone.jar")
}

tasks.bootBuildImage {
    imageName = "main-backend"
    //val env = mapOf("BP_HEALTH_CHECKER_ENABLED" to "true")
    //environment.set(env)
    //buildpacks.addAll("urn:cnb:builder:paketo-buildpacks/java", "docker.io/paketobuildpacks/health-checker:2.10.2")
}