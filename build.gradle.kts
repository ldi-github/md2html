plugins {
    kotlin("jvm") version "1.8.20"
}

group = "io.github.ldi-github"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // org.json
    implementation("org.json:json:20230227")

    // flexmark-java
    implementation("com.vladsch.flexmark:flexmark-all:0.64.0")

    // jsoup
    implementation("org.jsoup:jsoup:1.15.4")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}