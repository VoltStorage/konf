plugins {
    id("konf.kotlin-conventions")
}

dependencies {
    api(project(":konf-core"))
    implementation("com.moandjiezana.toml", "toml4j", Versions.TOML4J)

    testImplementation(testFixtures(project(":konf-core")))
}
