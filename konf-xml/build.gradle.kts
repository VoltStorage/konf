plugins {
    id("konf.kotlin-conventions")
}

dependencies {
    api(project(":konf-core"))
    implementation("org.dom4j", "dom4j", Versions.DOM4J)
    implementation("jaxen", "jaxen", Versions.JAXEN)

    testImplementation(testFixtures(project(":konf-core")))
}
