import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.vanniktech.maven.publish.SonatypeHost
import java.util.*

plugins {
    java
    `java-test-fixtures`
    id("org.jetbrains.kotlinx.kover")
    id("org.jetbrains.kotlin.jvm")
    id("kotlin-allopen")
    id("com.dorongold.task-tree")
    id("org.jmailen.kotlinter")
    id("com.github.ben-manes.versions")
    id("org.jetbrains.dokka")
    id("com.vanniktech.maven.publish")
}

group = "com.voltstorage"

version = System.getenv("LIBRARY_VERSION") ?: project.findProperty("localLibraryVersion") ?: "-.-.-"

repositories {
    mavenCentral()
}

val dependencyUpdates by tasks.existing(DependencyUpdatesTask::class)
dependencyUpdates {
    revision = "release"
    outputFormatter = "plain"
    resolutionStrategy {
        componentSelection {
            all {
                val rejected =
                    listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea", "eap", "pr", "dev", "mt")
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-+]*") }
                        .any { it.matches(candidate.version) }
                if (rejected) {
                    reject("Release candidate")
                }
            }
        }
    }
}

dependencies {
    api(kotlin("stdlib-jdk8", Versions.KOTLIN))
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", Versions.COROUTINES)
    implementation(kotlin("reflect", Versions.KOTLIN))
    implementation("org.reflections", "reflections", Versions.REFLECTIONS)
    implementation("org.apache.commons", "commons-text", Versions.COMMONS_TEXT)
    api("com.fasterxml.jackson.core", "jackson-core", Versions.JACKSON)
    api("com.fasterxml.jackson.core", "jackson-annotations", Versions.JACKSON)
    api("com.fasterxml.jackson.core", "jackson-databind", Versions.JACKSON)
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", Versions.JACKSON)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", Versions.JACKSON)

    testImplementation(kotlin("test", Versions.KOTLIN))
    testImplementation("org.hamcrest", "hamcrest-all", Versions.HAMCREST)
    testImplementation("org.junit.jupiter", "junit-jupiter-api", Versions.JUNIT)
    testImplementation("com.sparkjava", "spark-core", Versions.SPARK)

    testFixturesImplementation("org.jetbrains.spek", "spek-data-driven-extension", Versions.SPEK)
    testFixturesImplementation("org.jetbrains.spek", "spek-subject-extension", Versions.SPEK)
    testFixturesImplementation("com.natpryce", "hamkrest", Versions.HAMKREST)

    testRuntimeOnly("org.junit.platform", "junit-platform-launcher", Versions.JUNIT_PLATFORM)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", Versions.JUNIT)
    testRuntimeOnly("org.jetbrains.spek", "spek-junit-platform-engine", Versions.SPEK)
    testRuntimeOnly("org.slf4j", "slf4j-simple", Versions.SLF4J)
}

configurations.testImplementation.get().extendsFrom(configurations.testFixturesImplementation.get())

tasks.test {
    useJUnitPlatform()
    testLogging.apply {
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
    systemProperties["org.slf4j.simpleLogger.defaultLogLevel"] = "warn"
    systemProperties["junit.jupiter.execution.parallel.enabled"] = true
    systemProperties["junit.jupiter.execution.parallel.mode.default"] = "concurrent"
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    val properties = Properties()
    properties.load(
        rootProject.file("konf-core/src/test/kotlin/com/voltstorage/konf/source/env/env.properties").inputStream(),
    )
    properties.forEach { key, value ->
        environment(key as String, value)
    }

    finalizedBy("koverHtmlReport")
}

kotlin {
    jvmToolchain(Versions.JAVA)
}

kotlinter {
    ktlintVersion = Versions.KTLINT
}

kover {
    reports {

        total {
            // configure HTML report
            html {
                // custom header in HTML reports, project path by default
                title = "Konf"

                // custom charset in HTML report files, used return value of `Charset.defaultCharset()` for Kover or UTF-8 for JaCoCo by default.
                charset = "UTF-8"

                //  generate an HTML report when running the `check` task
                onCheck = true
            }

            xml {
                // custom header in XML reports, project path by default
                title = "Konf"
            }

            // configure verification task
            verify {
                //  verify coverage when running the `check` task
                onCheck = true
            }
        }
    }

    useJacoco()
}

dokka {
    dokkaSourceSets.configureEach {
        reportUndocumented.set(false)
        sourceLink {
            localDirectory.set(file("./"))
            remoteUrl("https://github.com/voltstorage/konf/blob/v${project.version}/")
            remoteLineSuffix.set("#L")
        }
    }

    dokkaPublications.html {
        outputDirectory.set(tasks.javadoc.get().destinationDir)
    }
}

val projectDescription =
    "A type-safe cascading configuration library for Kotlin/Java, " +
        "supporting most configuration formats"
val projectGroup = project.group as String
val projectName = if (project.name == "konf-all") "konf" else project.name
val projectVersion = project.version as String
val projectUrl = "https://github.com/voltstorage/konf"

mavenPublishing {
    coordinates(
        groupId = projectGroup,
        artifactId = projectName,
        version = projectVersion,
    )

    pom {
        name.set(rootProject.name)
        description.set(projectDescription)
        inceptionYear.set("2025")
        url.set(projectUrl)

        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("voltstorage")
                name.set("The Voltstorage developers")
                email.set("softwaredevelopment@voltstorage.com")
            }
        }

        scm {
            url.set(projectUrl)
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}
