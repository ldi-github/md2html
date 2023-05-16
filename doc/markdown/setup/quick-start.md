# Quick Start

This document explains how to set up **md2html** using IntelliJ IDEA.

## Installation

### IntelliJ IDEA

If you have not installed, download Ultimate or COMMUNITY and install it.
(COMMUNITY is opensource product)

https://www.jetbrains.com/idea/

## Create sample project

1. Open IntelliJ IDEA, select `File > New > Project`.
1. In New Project window, select `New Project` tab.
1. Input fields.
    - `Name: sample1`
    - `Location: (Your location)`
    - `Language: Kotlin`
    - `Build system: Gradle`
    - `JDK: (Your choice)`
    - `Gradle DSL: Kotlin`
    - `Add sample code: OFF`
      ![](_images/new_project.png)
1. Click `Create`.
1. Wait for a while until background tasks finish. It may take minutes.

### build.gradle.kts (after created)

```kotlin
plugins {
    kotlin("jvm") version "1.8.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}
```

Append some lines as follows.

### build.gradle.kts (after edit)

```kotlin
plugins {
    kotlin("jvm") version "1.8.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    testImplementation(kotlin("test"))

    // md2html
    implementation("io.github.ldi-github:md2html:0.1.0-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

/**
 * md2html
 */
tasks.register<JavaExec>("md2html") {
    group = "md2html"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("md2html.Executor")
}
```

Click reload on Gradle pane.
![](_images/reload_gradle.png)

## Copy files

1. Copy files in md2html project and paste to sample1 project.

![](_images/copy_and_paste_files.png)

## Run md2html

1. Run md2html task.
   <br>![](_images/run_md2html_task.png)
1. You can see generated html file under `sample1/doc/out` directory.
   <br>![](_images/doc_out.png)
1. Open `index.html`.
   <br>![](_images/index_html.png)

### Link

- [index](../index.md)
