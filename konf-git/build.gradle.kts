dependencies {
    api(project(":konf-core"))
    api("org.eclipse.jgit", "org.eclipse.jgit", Versions.JGIT)

    testImplementation(testFixtures(project(":konf-core")))
}
