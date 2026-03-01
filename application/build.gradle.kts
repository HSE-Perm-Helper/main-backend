dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    implementation(project(":api"))
    implementation(project(":remote-timetable"))
    implementation(project(":common"))
    implementation(project(":main-module"))

    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.bootJar {
    archiveFileName.set("app-standalone.jar")
}

tasks.bootBuildImage {
    imageName = "main-backend"
    val env = mapOf(
        "BP_HEALTH_CHECKER_ENABLED" to "true",
        "JAVA_TOOL_OPTIONS" to
                "-Xmx1024m " +
                "-XX:MaxMetaspaceSize=256m " +
                "-XX:+UseG1GC "
    )
    environment.set(env)
    //buildpacks.addAll("urn:cnb:builder:paketo-buildpacks/java", "docker.io/paketobuildpacks/health-checker:2.10.2")
}