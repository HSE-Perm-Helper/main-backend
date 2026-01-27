dependencies {
    implementation(project(":main-module"))
    implementation(project(":persistence"))
    implementation("org.mnode.ical4j:ical4j:4.0.0-beta9")
}

tasks.bootJar {
    enabled = false
}
