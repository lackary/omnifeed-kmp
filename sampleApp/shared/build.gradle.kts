import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeLink
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

val modulePackageName = "io.lackstudio.omnifeed.shared"
val unsplashAccessKeyName = "UNSPLASH_ACCESS_KEY"
val unsplashSecretKeyName = "UNSPLASH_SECRET_KEY"
val googleServicesWebClientIdName = "GOOGLE_SERVICES_WEB_CLIENT_ID"

fun getFromPropertiesFile(fileName: String, key: String, project: Project): String? {
    val file = project.rootProject.file(fileName)
    if (!file.exists()) return null

    val properties = Properties()
    file.inputStream().use { properties.load(it) }
    return properties.getProperty(key)
}

fun resolveConfigValue(key: String, project: Project): String? {
    // Priority: .secrets -> local.properties -> Environment variables
    return getFromPropertiesFile(".secrets", key, project)
        ?: getFromPropertiesFile("local.properties", key, project)
        ?: System.getenv(key)
}

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
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.compose.hot.reload)
    alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.buildkonfig)
}

    val unsplashAccessKey = resolveConfigValue(unsplashAccessKeyName, project) ?: ""
    val unsplashSecretKey = resolveConfigValue(unsplashSecretKeyName, project) ?: ""
    val googleServicesWebClientId = resolveConfigValue(googleServicesWebClientIdName, project) ?: ""

buildkonfig {
    packageName = "$modulePackageName.config"

    defaultConfigs {
        buildConfigField(STRING, "UNSPLASH_ACCESS_KEY", unsplashAccessKey)
        buildConfigField(STRING, "UNSPLASH_SECRET_KEY", unsplashSecretKey)
        buildConfigField(STRING, "GOOGLE_SERVICES_WEB_CLIENT_ID", googleServicesWebClientId)
    }
}

kotlin {
    android {
        namespace = modulePackageName
        compileSdk = 36
        minSdk = 30

        withJava() //  Opt-in to enable Java source compilation
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        // Set the Kotlin compilation target version
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }

        // Enable Android resource processing, default is false
        androidResources {
            enable = true
        }
    }

    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        name = "Shared"
        version = project.version.toString()
        summary = "Shared Module for OmniFeed"
        homepage = "https://github.com/lackary/omnifeed-kmp"
        ios.deploymentTarget = "18.2"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "Shared"
            isStatic = true
        }

        /**
         * The necessary Firebase pods are already managed by the 'libs.mirzemehdi.kmpauthFirebase' library.
         * Do NOT uncomment the pod definitions below.
         *
         * Doing so will cause a "symbol multiply defined" build error after a clean build,
         * because the dependencies would be included twice.
         *
         * However, these pods are still required for the native iOS app to function correctly.
         * You MUST manually add them to your `iosApp/Podfile`.
         *
         * Example for your Podfile:
         * pod 'FirebaseCore', '~> 12.14.0'
         * pod 'FirebaseAuth', '~> 12.14.0'
         * pod 'FirebaseFirestore', '~> 12.14.0'
         * pod 'GoogleSignIn', '~> 9.0.0'
         */
        pod("FirebaseCore") {
            version = "~> 12.14.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseAuth") {
            version = "~> 12.14.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("GoogleSignIn") {
            version = "~> 9.0.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseFirestore") {
            version = "~> 12.14.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

    }

    // desktop
    jvm()

    sourceSets {

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.koin.compose)
            implementation(libs.mirzemehdi.kmpauth.google)
            implementation(libs.mirzemehdi.kmpauth.firebase)
            implementation(libs.mirzemehdi.kmpauth.uihelper)
            implementation(libs.touchlab.kermit)
            implementation(libs.gitlive.firebase.auth)
            implementation(projects.omnifeed)
            implementation(project(":omnifeed-core"))
            implementation(project(":omnifeed-ui"))
            implementation(project(":omnifeed-unsplash"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.koin.test)
            implementation(libs.compose.ui.test.core)
        }

        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.google.firebase.bom))
            implementation(libs.google.firebase.auth)
            implementation(libs.google.firebase.common)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.ui.tooling)
        }
        androidUnitTest.dependencies {  }
        androidInstrumentedTest.dependencies {  }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        iosTest.dependencies {  }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.cio)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.compose.ui.test.junit4)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
}

// Skip Android Lint tasks (to avoid running Lint like generateDebugAndroidTestLintModel)
tasks.matching { it.name.contains(Regex("lint", RegexOption.IGNORE_CASE))}.configureEach {
    onlyIf {
        if (project.hasProperty("skip.lint")) {
            println("Skipping lint task ($name) because -Pskip.lint=true")
            false
        } else true
    }
}

// Skip JVM / Android tests
tasks.withType<Test>().configureEach {
    if (project.hasProperty("skip.tests")) {
        println("Disabling JVM/Android unit test ($name) because -Pskip.tests is set.")
        enabled = false
    }
}

// Skip Kotlin/Native tests
tasks.withType<KotlinNativeTest>().configureEach {
    if (project.hasProperty("skip.native.tests")) {
        println("Skipping Kotlin/Native test ($name) because -Pskip.native.tests=true")
        enabled = false
    }
}

// Important: Skip Kotlin/Native link tasks (to avoid test linking like linkDebugTestIosSimulatorArm64)
tasks.withType<KotlinNativeLink>().configureEach {
    if (project.hasProperty("skip.native.tests")) {
        println("Skipping Kotlin/Native link task ($name) because -Pskip.native.tests=true")
        enabled = false
    }
}

//// For special test tasks on the Android platform (e.g., connectedAndroidTest/instrumentation tests),
//// they might not be of the `Test` type, but rather `DeviceProviderInstrumentTestTask` or other specific types.
//// To ensure more comprehensive coverage, we need to find them and apply the same logic.
//// Although they are not necessarily all of type Test, it's safer to cover them using `withName`:
//tasks.configureEach {
//    if (name.contains("AndroidTest", ignoreCase = true) ||
//        name.contains("Test", ignoreCase = true)
//    ) {
//        // Apply the skipping logic using onlyIf
//        onlyIf { !project.hasProperty("skip.tests") }
//    }
//}

// Force exclusion of unstable JogAmp dependencies from the test Runtime Classpath
// This will not affect the app's production execution, only prevent tests from attempting to download it.
configurations.matching { it.name.contains("Test") }.configureEach {
    exclude(group = "org.jogamp.gluegen")
    exclude(group = "org.jogamp.jogl")
}
