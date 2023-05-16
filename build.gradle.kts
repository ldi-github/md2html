plugins {
    val kotlin_version = "1.6.21"
    kotlin("jvm") version kotlin_version
    id("idea")
    id("maven-publish")
    id("signing")
    id("java")
}

group = "io.github.ldi-github"
version = "0.1.0"

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

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "15"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "15"
    }
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

/**
 * publishing
 */
publishing {
    repositories {
        maven {
            name = "local"
            url = uri("$buildDir/repository")
        }
        maven {
            name = "ossrh"
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().contains("-")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("SHIRATES_CORE_OSSRH_USERNAME")
                password = System.getenv("SHIRATES_CORE_OSSRH_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("binaryAndSources") {
            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            groupId = "${project.group}"
            artifactId = project.name
            version = "${project.version}"
            pom {
                name.set("md2html")
                description.set("This is simple converter. Just converts documents in markdown(.md) to html.")
                url.set("https://github.com/ldi-github/md2html")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("wave1008")
                        name.set("Nobuhiro Senba")
                    }
                }
                scm {
                    connection.set("https://github.com/ldi-github/md2html.git")
                    developerConnection.set("git@github.com:md2html.git")
                    url.set("https://github.com/ldi-github/md2html")
                }
            }
        }

        register<MavenPublication>("gpr") {
            from(components["kotlin"])
            artifact(tasks["sourcesJar"])
            groupId = "${project.group}"
            artifactId = project.name
            version = "${project.version}"
        }
    }
}

signing {
    sign(publishing.publications["binaryAndSources"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    onlyIf {
        val r =
            (repository == publishing.repositories["local"] && publication == publishing.publications["binaryAndSources"]) ||
                    (repository == publishing.repositories["ossrh"] && publication == publishing.publications["binaryAndSources"])
        r
    }
}
tasks.withType<PublishToMavenLocal>().configureEach {
    onlyIf {
        val r = publication == publishing.publications["binaryAndSources"]
        r
    }
}

tasks.register("publishToLocalRepository") {
    group = "publishing"
    description = "Publishes to local"
    dependsOn(tasks.withType<PublishToMavenRepository>().matching {
        it.repository == publishing.repositories["local"]
    })
}

tasks.register("publishToExternalRepository") {
    group = "publishing"
    description = "Publishes to external repository"
    dependsOn(tasks.withType<PublishToMavenRepository>().matching {
        it.repository == publishing.repositories["ossrh"]
    })
}

/**
 * md2html
 */
tasks.register<JavaExec>("md2html") {
    group = "md2html"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("md2html.Executor")
}
