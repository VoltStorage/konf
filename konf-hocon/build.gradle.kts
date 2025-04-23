dependencies {
    api(project(":konf-core"))
    implementation("com.typesafe", "config", Versions.TYPESAFE_CONFIG)

    testImplementation(testFixtures(project(":konf-core")))
}
