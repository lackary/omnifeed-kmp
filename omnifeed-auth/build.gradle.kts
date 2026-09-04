import com.android.build.api.withAndroid
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.util.Properties

val modulePackageName = "io.lackstudio.omnifeed.auth"

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

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.buildkonfig)
    id("maven-publish")
}

base {
    archivesName.set("omnifeed-auth")
}

buildkonfig {
    packageName = "$modulePackageName.config"

    val firebaseWebBase64 = resolveConfigValue("FIREBASE_WEB_BASE64", project) ?: ""

    defaultConfigs {
        buildConfigField(STRING, "FIREBASE_WEB_BASE64", firebaseWebBase64)
    }
}

val generateJsResources by tasks.registering {
    val jsFile = project.file("src/webMain/resources/google-auth-bridge.js")
    val outputDir = project.layout.buildDirectory.dir("generated/kotlin/jsResources")
    val outputFile = outputDir.map { it.file("io/lackstudio/omnifeed/auth/GoogleAuthBridgeJs.kt") }

    inputs.file(jsFile)
    outputs.dir(outputDir)

    doLast {
        val jsContent = jsFile.readText()
            .replace("$", "\${'$'}") // Escape $ for Kotlin string templates
            .replace("\"\"\"", "\\\"\\\"\\\"") // Escape triple quotes

        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText("""
            package io.lackstudio.omnifeed.auth

            /**
             * Automatically generated JavaScript resource constants. Do not modify manually.
             * Source file: src/webMain/resources/google-auth-bridge.js
             */
            internal object GoogleAuthBridgeJs {
                val CONTENT = ""${"\""}
                $jsContent
                ""${"\""}.trimIndent()
            }
        """.trimIndent())
    }
}

kotlin {

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("nonWasm") {
                // Include all platforms that require Firebase support in this group (exclude Wasm)
                withAndroid()
                withJvm()
                withIos()
                withJs()
            }
        }
    }

    android {
        namespace = modulePackageName
        compileSdk = 36
        minSdk = 30
        withJava()
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder { sourceSetTreeName = "test" }
        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
        androidResources { enable = true }
    }

    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = project.version.toString()
        summary = "OmniFeed Authentication Module"
        homepage = "https://github.com/lackary/omnifeed-kmp"
        ios.deploymentTarget = "18.5"
        framework {
            baseName = "OmniFeedAuth"
            isStatic = true
        }
        pod("GoogleSignIn") {
            version = "~> 9.0.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseCore") {
            version = "~> 12.14.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseAuth") {
            version = "~> 12.14.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("FirebaseFirestore") {
            version = "~> 12.14.0"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.omnifeedCore)
            implementation(libs.ktor.client.core)
            api(libs.koin.core)
            api(libs.anifantakis.ksafe)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.turbine.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        
        androidMain.dependencies {
            api(project.dependencies.platform(libs.google.firebase.bom))
            implementation(libs.google.firebase.auth)
            implementation(libs.google.firebase.common)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.google.gms.play.service.auth)
            implementation(libs.google.googleid)
            implementation(libs.androidx.core.ktx)
            implementation(libs.koin.android)
        }

        val androidHostTest by getting {
            dependencies {
                implementation(libs.mockk)
            }
        }

        jvmTest.dependencies {
            implementation(libs.mockk)
            implementation(libs.koin.test.junit4)
        }

        iosMain.dependencies {
            implementation(libs.gitlive.firebase.common)
            implementation(libs.gitlive.firebase.auth)
        }

        webMain.dependencies {
            implementation(libs.kotlin.wrappers.browser)
        }
        webMain.get().kotlin.srcDirs(generateJsResources)

        val nonWasmMain by getting {
            dependencies {
                api(libs.gitlive.firebase.common)
                api(libs.gitlive.firebase.auth)
                api(libs.gitlive.firebase.firestore)
            }
        }
    }
}
