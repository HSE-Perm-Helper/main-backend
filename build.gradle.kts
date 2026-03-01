plugins {
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("jacoco")
    //id("se.solrike.sonarlint") version "2.2.0"
}

val springCloudVersion by extra("2025.1.0-RC1")

group = "com.melowetty"
version = "1.4.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":application"))
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "jacoco")

    repositories {
        mavenCentral()
    }

    kotlin {
        jvmToolchain(21)
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        }
    }

    java.sourceCompatibility = JavaVersion.VERSION_21

    tasks.withType<Test> {
        useJUnitPlatform()
    }
//
//    tasks.jar {
//        enabled = false
//    }

    tasks.test {
        finalizedBy(tasks.jacocoTestReport)
//    finalizedBy(tasks.sonarlintMain)
    }

    jacoco {
        toolVersion = "0.8.14"
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            html.required.set(false)
        }
    }
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.jar {
    enabled = false
}

tasks.bootJar {
    enabled = false
}

//sonarlint {
//    maxIssues = 0
//    dependencies {
//        sonarlintPlugins("org.sonarsource.kotlin:sonar-kotlin-plugin:2.13.0.2116")
//    }
//}
//
//tasks.sonarlintMain {
//    ignoreFailures.set(true)
//    reports {
//        create("xml") {
//            enabled.set(true)
//        }
//    }
//}
//
//tasks.sonarlintTest {
//    ignoreFailures.set(true)
//}