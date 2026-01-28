plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.compose.hot.reload) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
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

subprojects {
    // Listen: Whenever a subproject applies the 'maven-publish' plugin, automatically inject POM settings for it
    plugins.withId("maven-publish") {

        // Configure publishing
        extensions.configure<PublishingExtension> {
            publications.withType<MavenPublication> {
                pom {
                    // Configure shared information (same for all modules)
                    url.set("https://github.com/lackary/omnifeed-kmp")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("lackary")
                            name.set("Henry Huang")
                            email.set("lackary@gmail.com")
                        }
                    }

                    // Configure dynamic information (automatically fetched for each module)
                    // If the subproject does not have a description, use the project name instead
                    if (!description.isPresent) {
                        description.set("Library module: ${project.name}")
                    }
                    name.set(project.name) // Set POM name to the module name
                }
            }
        }
    }
}
