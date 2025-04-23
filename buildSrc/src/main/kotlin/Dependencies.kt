object Versions {
    const val JAVA = 21
    const val COMMONS_TEXT = "1.13.1"
    const val COROUTINES = "1.10.2"
    const val DEPENDENCY_UPDATE = "0.52.0"
    const val DOKKA = "2.0.0"
    const val DOM4J = "2.1.4"
    const val GRAAL = "24.2.1"
    const val HAMCREST = "1.3"
    const val HAMKREST = "1.8.0.1"
    const val TYPESAFE_CONFIG = "1.4.3"
    const val JACKSON_MINOR = "2.18"
    const val JACKSON = "$JACKSON_MINOR.3"
    const val JACOCO = "0.8.13"
    const val JAXEN = "2.0.0"
    const val JGIT = "7.2.0.202503040940-r"
    const val JMH = "1.37"
    const val JMH_PLUGIN = "0.7.3"
    const val JUNIT = "5.12.2"
    const val JUNIT_PLATFORM = "1.12.2"
    const val KOTLIN = "2.1.20"
    const val REFLECTIONS = "0.10.2"
    const val SLF4J = "2.0.17"
    const val SPARK = "2.9.4"
    const val SPEK = "1.1.5"
    const val SPOTLESS = "7.0.3"
    const val TASK_TREE = "4.0.1"
    const val TOML4J = "0.7.2"
    const val YAML = "2.4"

    // Since 1.8, the minimum supported runtime version is JDK 11.
    const val GOOGLE_JAVA_FORMAT = "1.26.0"
    const val KTLINT = "1.5.0"
}

fun String?.withColon() = this?.let { ":$this" } ?: ""

fun kotlin(
    module: String,
    version: String? = null,
) = "org.jetbrains.kotlin:kotlin-$module${version.withColon()}"

fun spek(
    module: String,
    version: String? = null,
) = "org.jetbrains.spek:spek-$module${version.withColon()}"

fun jackson(
    scope: String,
    module: String,
    version: String? = null,
) = "com.fasterxml.jackson.$scope:jackson-$scope-$module${version.withColon()}"

fun jacksonCore(
    module: String = "core",
    version: String? = null,
) = "com.fasterxml.jackson.core:jackson-$module${version.withColon()}"

fun junit(
    scope: String,
    module: String,
    version: String? = null,
) = "org.junit.$scope:junit-$scope-$module${version.withColon()}"
