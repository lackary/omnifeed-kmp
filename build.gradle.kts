import org.gradle.process.ExecOperations
import javax.inject.Inject

// Define version filename
val versionFilename = "VERSION.txt"

// ---------------------------------------------------------------------------
// Define ValueSource (Resolves Git execution issues with Configuration Cache)
// ---------------------------------------------------------------------------
abstract class GitVersionValueSource : ValueSource<String, ValueSourceParameters.None> {
    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String {
        return try {
            val output = java.io.ByteArrayOutputStream()
            execOperations.exec {
                commandLine("git", "describe", "--tags")
                standardOutput = output
                isIgnoreExitValue = true
            }
            val version = output.toString().trim()
            if (version.isNotEmpty() && !version.contains("not a git repository")) {
                version.removePrefix("v")
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
}

// ---------------------------------------------------------------------------
// Helper function: Read version from file
// ---------------------------------------------------------------------------
fun getVersionFromFile(filename: String, projectDir: File): String {
    val versionFile = File(projectDir, filename)
    return if (versionFile.exists()) {
        versionFile.readText().trim()
    } else {
        "0.0.1"
    }
}

// ---------------------------------------------------------------------------
// Calculate projectVersion (Supports Lazy Evaluation)
// ---------------------------------------------------------------------------
val projectVersion: String by lazy {
    // 1. Prioritize reading from Gradle Property (Passed by CI)
    val pNewVersion = project.providers.gradleProperty("newVersion").orNull

    // 2. If no Property, try getting from Git (Use ValueSource to avoid breaking Cache)
    val gitVersionProvider = project.providers.of(GitVersionValueSource::class) {}
    val gitVer = gitVersionProvider.get()

    // 3. Determine Raw Version (Property > Git > File)
    val rawVersion = pNewVersion ?: gitVer.ifEmpty {
        getVersionFromFile(versionFilename, layout.projectDirectory.asFile)
    }

    // 4. CI Environment check (Remove Build Metadata)
    val isCi = System.getenv("CI") == "true"
    if (isCi) {
        rawVersion.substringBefore("+")
    } else {
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
    group = "io.lackstudio.omnifeed"
    version = projectVersion
}

// ---------------------------------------------------------------------------
// Define Task (Fixes Configuration Cache and variable issues)
// ---------------------------------------------------------------------------
tasks.register("setBuildVersion") {
    group = "versioning"
    description = "Writes the project version and build number to $versionFilename."

    // Save filename as a local variable to avoid closure capture errors
    val vFileName = versionFilename

    // Define input parameters (Inputs) - Use .orElse("") to avoid null errors
    val pNewVersion = project.providers.gradleProperty("newVersion").orElse("")
    val pBuildNumber = project.providers.gradleProperty("buildNumber").orElse("")

    // Define output file (Outputs)
    val outputFile = layout.projectDirectory.file(vFileName)
    outputs.file(outputFile)

    // Mark inputs to support Up-to-date checks
    inputs.property("newVersion", pNewVersion)
    inputs.property("buildNumber", pBuildNumber)

    doLast {
        val version = pNewVersion.get()
        val build = pBuildNumber.get()

        logger.lifecycle(">> newVersion (Semantic): $version")
        logger.lifecycle(">> buildNumber (CI): $build")

        // Local execution safeguard: If no version provided, just print warning and skip
        if (version.isBlank()) {
            logger.warn("Warning: -PnewVersion not provided. Skipping $vFileName update.")
            return@doLast
        }

        val internalVersion = if (build.isNotEmpty()) {
            "$version+$build"
        } else {
            version
        }

        logger.lifecycle(">> write INTERNAL version to ${outputFile.asFile}")
        outputFile.asFile.writeText(internalVersion)
        logger.lifecycle(">> Successfully set $vFileName to: $internalVersion")
    }
}
