import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.*

val repoUserToken by extra { getPrivateProperty("repoUserToken") }
val repoUserPassword by extra { getPrivateProperty("repoUserPassword") }
val signPublications by extra { getPrivateProperty("signPublications") }
//val signingKeyId by extra { getPrivateProperty("signingKeyId") }
//val signingKey by extra { getPrivateProperty("signingKey") }
//val signingPassword by extra { getPrivateProperty("signingPassword") }

println("repoUserToken: $repoUserToken")

tasks.named<Wrapper>("wrapper") {
    group = "help"
    gradleVersion = "8.13"
    distributionType = Wrapper.DistributionType.ALL
}

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    `java-test-fixtures`
    jacoco
    `maven-publish`
    signing
    kotlin("jvm") version Versions.KOTLIN
    kotlin("plugin.allopen") version Versions.KOTLIN
    id("com.dorongold.task-tree") version Versions.TASK_TREE
    id("me.champeau.jmh") version Versions.JMH_PLUGIN
    id("com.diffplug.spotless") version Versions.SPOTLESS
    id("com.github.ben-manes.versions") version Versions.DEPENDENCY_UPDATE
    id("org.jetbrains.dokka") version Versions.DOKKA
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-test-fixtures")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "kotlin-allopen")
    apply(plugin = "com.dorongold.task-tree")
    apply(plugin = "me.champeau.jmh")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "org.jetbrains.dokka")

    group = "com.voltstorage"
    version = "1.0.0"

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
}

subprojects {
    configurations.testFixturesImplementation.get().extendsFrom(configurations.implementation.get())
    configurations.testImplementation.get().extendsFrom(configurations.testFixturesImplementation.get())

    dependencies {
        api(kotlin("stdlib-jdk8", Versions.KOTLIN))
        api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", Versions.COROUTINES)
        implementation(kotlin("reflect", Versions.KOTLIN))
        implementation("org.reflections", "reflections", Versions.REFLECTIONS)
        implementation("org.apache.commons", "commons-text", Versions.COMMONS_TEXT)
        arrayOf("core", "annotations", "databind").forEach { name ->
            api(jacksonCore(name, Versions.JACKSON))
        }
        implementation(jackson("module", "kotlin", Versions.JACKSON))
        implementation(jackson("datatype", "jsr310", Versions.JACKSON))

        testFixturesImplementation(kotlin("test", Versions.KOTLIN))
        testFixturesImplementation("com.natpryce", "hamkrest", Versions.HAMKREST)
        testFixturesImplementation("org.hamcrest", "hamcrest-all", Versions.HAMCREST)
        testImplementation(junit("jupiter", "api", Versions.JUNIT))
        testImplementation("com.sparkjava", "spark-core", Versions.SPARK)
        arrayOf("api", "data-driven-extension", "subject-extension").forEach { name ->
            testFixturesImplementation(spek(name, Versions.SPEK))
        }

        testRuntimeOnly(junit("platform", "launcher", Versions.JUNIT_PLATFORM))
        testRuntimeOnly(junit("jupiter", "engine", Versions.JUNIT))
        testRuntimeOnly(spek("junit-platform-engine", Versions.SPEK))
        testRuntimeOnly("org.slf4j", "slf4j-simple", Versions.SLF4J)
    }

    val test by tasks.existing(Test::class)
    test {
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
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    kotlin {
        jvmToolchain(Versions.JAVA)
    }

    allOpen {
        annotation("org.openjdk.jmh.annotations.BenchmarkMode")
        annotation("org.openjdk.jmh.annotations.State")
    }

    jmh {
        // jvmArgs = ["-Djmh.separateClasspathJAR=true"]
        iterations = 10 // Number of measurement iterations to do.
        // benchmarkMode = ["thrpt"] // Benchmark mode. Available modes are: [Throughput/thrpt, AverageTime/avgt, SampleTime/sample, SingleShotTime/ss, All/all]
        batchSize = 1
        // Batch size: number of benchmark method calls per operation. (some benchmark modes can ignore this setting)
        fork = 1 // How many times to fork a single benchmark. Use 0 to disable forking altogether
        // operationsPerInvocation = 1 // Operations per invocation.
        timeOnIteration = "1s" // Time to spend on each measurement iteration.
        threads = 4 // Number of worker threads to run with.
        jmhTimeout = "10s" // Timeout for benchmark iteration.
        // timeUnit = "ns" // Output time unit. Available time units are: [m, s, ms, us, ns].
        verbosity = "NORMAL" // Verbosity mode. Available modes are: [SILENT, NORMAL, EXTRA]
        warmup = "1s" // Time to spend at each warmup iteration.
        warmupBatchSize = 1 // Warmup batch size: number of benchmark method calls per operation.
        // warmupForks = 0 // How many warmup forks to make for a single benchmark. 0 to disable warmup forks.
        warmupIterations = 10 // Number of warmup iterations to do.
        zip64 = false // Use ZIP64 format for bigger archives
        jmhVersion = Versions.JMH // Specifies JMH version
    }

    spotless {
        java {
            googleJavaFormat(Versions.GOOGLE_JAVA_FORMAT)
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(rootProject.file("config/spotless/apache-license-2.0.java"))
        }
        kotlin {
            ktlint(Versions.KTLINT)
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(rootProject.file("config/spotless/apache-license-2.0.kt"))
        }
    }

    jacoco {
        toolVersion = Versions.JACOCO
    }

    val jacocoTestReport by tasks.existing(JacocoReport::class) {
        reports {
            // xml.isEnabled = true
            xml.required.set(true)
            // html.isEnabled = true
            html.required.set(true)
        }
    }

    val check by tasks.existing {
        dependsOn(jacocoTestReport)
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

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        from(tasks.dokkaGeneratePublicationHtml)
    }

    val projectDescription =
        "A type-safe cascading configuration library for Kotlin/Java, " +
            "supporting most configuration formats"
    val projectGroup = project.group as String
    val projectName = if (project.name == "konf-all") "konf" else project.name
    val projectVersion = project.version as String
    val projectUrl = "https://github.com/voltstorage/konf"

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                artifact(sourcesJar.get())
                artifact(javadocJar.get())

                groupId = projectGroup
                artifactId = projectName
                version = projectVersion

                suppressPomMetadataWarningsFor("testFixturesApiElements")
                suppressPomMetadataWarningsFor("testFixturesRuntimeElements")
                pom {
                    name.set(rootProject.name)
                    description.set(projectDescription)
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
            }
        }
        repositories {
            mavenCentral {
                credentials {
                    username = repoUserToken
                    password = repoUserPassword
                }
            }
        }
    }

    signing {
        val signingKeyId: String? by project
        val signingKey: String? by project
        val signingPassword: String? by project

        println("signingKeyId: $signingKeyId")

        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        setRequired({ signPublications == "true" })
        sign(publishing.publications["maven"])
    }

    tasks {
        val install by registering
        afterEvaluate {
            val publishToMavenLocal by existing
            val publish by existing
            install.configure { dependsOn(publishToMavenLocal) }
            publish { dependsOn(check, install) }
        }
    }
}
