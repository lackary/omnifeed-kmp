import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

// Define the Package Name for this module
val modulePackageName = "io.lackstudio.omnifeed.unsplash"

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.buildkonfig.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.devtool.ksp)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
}

kotlin {
    androidTarget {
        // This is necessary to publish the Android library variant.
        publishLibraryVariants("release")
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }
        }
    }

    val xcf = XCFramework()
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "OmniFeedUnsplash"
            xcf.add(this)
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(project(":core"))
            implementation(kotlin("stdlib-common"))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.touchlab.kermit)
        }

        jvmMain.dependencies {

        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.cio)
            implementation(libs.koin.test.junit4)
        }

        androidMain.dependencies {

        }
        androidUnitTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.android)
        }

        iosMain.dependencies {

        }
        iosTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.darwin)
        }
    }
}

buildkonfig {
    packageName = "$modulePackageName.config"
    val localProps = Properties()
    val localPropsFile = rootProject.file("local.properties")

    if (localPropsFile.exists()) {
        localProps.load(localPropsFile.inputStream())
    }

    val errorMessage = "not found. Please set it as an environment variable or in local.properties."

    val unsplashAccessKey = System.getenv("UNSPLASH_ACCESS_KEY")
        ?: localProps.getProperty("UNSPLASH_ACCESS_KEY")
        ?: error("UNSPLASH_ACCESS_KEY $errorMessage")

    val unsplashSecretKey = System.getenv("UNSPLASH_SECRET_KEY")
        ?: localProps.getProperty("UNSPLASH_SECRET_KEY")
        ?: error("UNSPLASH_SECRET_KEY $errorMessage")

    defaultConfigs {
        buildConfigField(STRING, "UNSPLASH_ACCESS_KEY", unsplashAccessKey)
        buildConfigField(STRING, "UNSPLASH_SECRET_KEY", unsplashSecretKey)
    }
}

android {
    namespace = modulePackageName
    compileSdk = 36
    defaultConfig {
        minSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

publishing {
    publications {
        // withType<MavenPublication>().all {
        //     artifactId = "testing" // or your artifact name
        // }
//        create<MavenPublication>("maven") {
//            artifactId = "omnifeed-unsplash"
//            from(components["kotlin"])
//        }
        withType<MavenPublication> {
            artifactId = artifactId.replace("unsplash", "omnifeed-unsplash")
        }
    }
    repositories {
        mavenLocal()
    }
}

/**
 * Generate the JSON Mock data
 */

// Define the path for generated files
val generatedMockDir = layout.buildDirectory.dir("generated/mock/kotlin")

// Register a Task to generate Kotlin code
// Register a Task to generate Kotlin code
val generateMockData by tasks.registering {
    // Define input directory (location of your JSON files)
    val inputDir = file("src/commonTest/resources/mock")
    // Define output directory
    val outputDir = generatedMockDir.get().asFile

    // Capture the script-level variable as a local variable for the Task here
    // This allows doLast to safely access this variable during execution without relying on the script instance
    val targetPackageName = modulePackageName

    // Tell Gradle these are inputs and outputs for caching (Up-to-date checks)
    inputs.dir(inputDir)
    outputs.dir(outputDir)

    // Recommendation: Declare this variable as an Input so Gradle knows to re-run the Task when the Package Name changes
    inputs.property("packageName", targetPackageName)

    doLast {
        if (!inputDir.exists()) return@doLast

        // Dynamically generate the path using the modulePackageName defined above
        val packagePath = targetPackageName.replace(".", "/")
        val outputFile = File(outputDir, "$packagePath/test/MockData.kt")
        outputFile.parentFile.mkdirs()

        // Start writing Kotlin file content
        val content = buildString {
            // Dynamically generate the package declaration using the modulePackageName defined above
            appendLine("package $targetPackageName.test")
            // --- End of modification ---

            appendLine()
            appendLine("// Auto-generated by Gradle. Do not modify.")
            appendLine("object MockData {")

            // Iterate over all json files in the mock directory
            inputDir.listFiles { _, name -> name.endsWith(".json") }?.forEach { jsonFile ->
                // Convert filename to variable name (e.g., user_profile.json -> USER_PROFILE)
                val variableName = jsonFile.nameWithoutExtension.uppercase().replace("-", "_")
                // Read content and handle escape characters (using Kotlin Raw String)

                // Read raw JSON
                val rawJson = jsonFile.readText()

                // Replace all "$" with Kotlin-safe literal "${'$'}"
                // This way the generated code becomes: """ ... ${'$'}100 ... """
                val escapedJson = rawJson.replace("$", "\${'$'}")

                appendLine("    val $variableName = \"\"\"")
                appendLine(escapedJson)
                appendLine("    \"\"\".trimIndent()")
            }
            appendLine("}")
        }
        outputFile.writeText(content)
    }
}

// Add generated code to sourceSet
kotlin {
    sourceSets {
        commonTest {
            kotlin.srcDir(generatedMockDir)
        }
    }
}

// Ensure the generation Task runs before compiling Kotlin
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(generateMockData)
}
// For Native (iOS) compilation tasks
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile>().configureEach {
    dependsOn(generateMockData)
}
