plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.9.1")
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:2.1.20")
    implementation("org.jetbrains.kotlin.plugin.allopen:org.jetbrains.kotlin.plugin.allopen.gradle.plugin:2.1.21")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:2.0.0")
    implementation("org.jmailen.kotlinter:org.jmailen.kotlinter.gradle.plugin:5.0.1")
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.30.0")
    implementation("com.github.ben-manes.versions:com.github.ben-manes.versions.gradle.plugin:0.52.0")
    implementation("com.dorongold.task-tree:com.dorongold.task-tree.gradle.plugin:4.0.1")
}
