plugins {
    id("konf.kotlin-conventions")
}

dependencies {
    api(project(":konf-core"))
    implementation("org.yaml", "snakeyaml", Versions.YAML)

    testImplementation(testFixtures(project(":konf-core")))
}
