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

println("🚀 Debug: Root Project Version is [${rootProject.version}]")
subprojects {
    group = "io.lackstudio.omnifeed"
    version = rootProject.version
    afterEvaluate {
        println("   👉 Subproject [${name}] version: $version")
    }
}

tasks.register("setBuildVersion") {
    group = "versioning"
    description = "Updates the version and build number in gradle.properties and iosApp Config.xcconfig"

    val pNewVersion = project.providers.gradleProperty("newVersion").orElse("")
    val pBuildNumber = project.providers.gradleProperty("buildNumber").orElse("")

    val gradlePropertiesFile = layout.projectDirectory.file("gradle.properties")

    doLast {
        val newVersion = pNewVersion.get()
        val newBuildNumber = pBuildNumber.get()
        val taskLogger = this.logger

        if (newVersion.isBlank() && newBuildNumber.isBlank()) {
            return@doLast
        }

        // Update gradle.properties
        val propertiesFile = gradlePropertiesFile.asFile
        if (propertiesFile.exists()) {
            val lines = propertiesFile.readLines()
            val newLines = lines.map { line ->
                val trimmedLine = line.trim()
                when {
                    newVersion.isNotBlank() && trimmedLine.startsWith("version=") -> "version=$newVersion"
                    newBuildNumber.isNotBlank() && trimmedLine.startsWith("buildNumber=") -> "buildNumber=$newBuildNumber"
                    else -> line
                }
            }
            propertiesFile.writeText(newLines.joinToString("\n"))
            taskLogger.lifecycle("Updated gradle.properties -> version: $newVersion, code: $newBuildNumber")
        }
    }
}
