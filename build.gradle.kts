plugins {
    id("java")
    id("io.freefair.lombok") version "8.11"
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    dependsOn(":native-library:linkDebug")
    systemProperty("java.library.path", file("${project(":native-library").buildDir}/lib/main/debug").absolutePath)
    useJUnitPlatform()
}