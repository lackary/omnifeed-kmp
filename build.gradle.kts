// Create a readable version number variable in the root directory
// The default value is "0.0.1", the actual version number will be set by the CI/CD process
val versionFilename = "VERSION.txt"
fun getVersionFromFile(): String {
    val versionFile = File(versionFilename)
    return if (versionFile.exists()) {
        versionFile.readText().trim()
    } else {
        "0.0.1"
    }
}

fun getGitVersion(): String {
    return try {
        // execute git describe --tags to get the latest tag (e.g., v1.0.0 or v1.0.0-2-gda23...)
        val process = ProcessBuilder("git", "describe", "--tags").start()
        val version = process.inputStream.bufferedReader().readText().trim()

        process.waitFor()

        // Check execution result: exit code must be 0 and content must exist
        if (process.exitValue() == 0 && version.isNotEmpty()) {
            // Remove leading 'v' (if present), e.g., v1.0.0 -> 1.0.0
            version.removePrefix("v")
        } else {
            println("version is empty")
            "" // If git exists but there are no tags
        }
    } catch (e: Exception) {
        println("getGitVersion exception: ${e.message}")
        "" // If there is no git environment (e.g., certain CI stages or a simple zip download)
    }
}

val projectVersion: String by lazy {
    // Priority try to get from Gradle Property (Passed by Semantic Release)
    val pNewVersion = project.providers.gradleProperty("newVersion").orNull

    // If no Property, read from file or Git (for local development)
    // Note: VERSION.txt might contain +88, so we need to handle it
    val rawVersion = pNewVersion ?: getGitVersion().ifEmpty { getVersionFromFile() }

    // Check if running in CI environment
    val isCi = System.getenv("CI") == "true"

    if (isCi) {
        // Critical Logic: When publishing Artifacts in CI, strip the Build Metadata after '+'
        // This ensures the published Maven version is a clean 1.0.0, not 1.0.0+88
        rawVersion.substringBefore("+")
    } else {
        // Keep original logic for local environment for easier debugging
        rawVersion
    }
}

plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.hotReload) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.devtool.ksp) apply false
    alias(libs.plugins.gms.google.services) apply false
    alias(libs.plugins.kotlin.native.cocoapods) apply false
}

subprojects {
    //trick: for the same plugin versions in all sub-modules
    group = "io.lackstudio.omnifeed"
    version = projectVersion
}


// Inherit from DefaultTask or a more suitable abstract class
// Use @get:Input to mark these properties as task inputs
abstract class SetBuildVersionTask : DefaultTask() {

    // Use Property<String> to hold the version and build numbers, ensuring configuration cache compatibility
    @get:Input
    abstract val newVersion: Property<String>

    @get:Input
    abstract val buildNumber: Property<String>

    @get:OutputFile
    val versionFile = project.layout.projectDirectory.file(versionFilename)

    // Use @TaskAction to annotate the task's execution logic
    @TaskAction
    fun execute() {
        // Use .get() to retrieve the actual String value.
        val version = newVersion.get()
        val build = buildNumber.get()

        logger.lifecycle(">> newVersion (Semantic): $version")
        logger.lifecycle(">> buildNumber (CI): $build")

        // Use '+' to append Build Number (SemVer standard)
        // This allows the SDK to see 1.0.0+88 when reading VERSION.txt internally
        val internalVersion = if (build.isNotEmpty()) {
            "$version+$build"
        } else {
            version
        }

        logger.lifecycle(">> write INTERNAL version to ${versionFile.asFile}")
        versionFile.asFile.writeText(internalVersion)

        logger.lifecycle(">> Successfully set $versionFilename to: $internalVersion")
    }
}

// Register and configure the new task
tasks.register<SetBuildVersionTask>("setBuildVersion") {
    // Use providers.gradleProperty to safely get -P parameters
    // If the property doesn't exist, it returns an unset Property.
    val pNewVersion = project.providers.gradleProperty("newVersion")
    val pBuildNumber = project.providers.gradleProperty("buildNumber")

    // Use .set() to configure task properties.
    newVersion.set(pNewVersion)
    buildNumber.set(pBuildNumber)

    // Configure group and description
    group = "versioning"
    description = "Writes the project version and build number to $versionFilename."
}
