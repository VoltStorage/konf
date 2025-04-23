dependencies {
    api(project(":konf-core"))
    implementation("org.graalvm.sdk", "graal-sdk", Versions.GRAAL)
    implementation("org.graalvm.js", "js", Versions.GRAAL)

    testImplementation(testFixtures(project(":konf-core")))
}
