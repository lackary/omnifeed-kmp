import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

// Define the Package Name for this module
val modulePackageName = "io.lackstudio.omnifeed.ui"

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    id("maven-publish")
}

kotlin {
    androidLibrary {
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

    val xcf = XCFramework()
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "OmniFeedUi"
            xcf.add(this)
            isStatic = true
        }
    }

    jvm()

//    js {
//        browser()
//        binaries.executable()
//    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.ktor.client.core)
            implementation(libs.kmp.androidx.lifecycle.viewmodelCompose)
            implementation(libs.kevinnzou.composeWebviewMultiplatform)
            implementation(projects.core)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine.test)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        jvmTest.dependencies {
        }

        androidMain.dependencies {
        }

        androidUnitTest.dependencies {
        }

        iosMain.dependencies {
        }
        iosTest.dependencies {
        }
    }
}

publishing {
    publications {
        // withType<MavenPublication>().all {
        //     artifactId = "testing" // or your artifact name
        // }
//        create<MavenPublication>("maven") {
//            artifactId = "omnifeed-ui"
//            from(components["kotlin"])
//        }
        withType<MavenPublication> {
            artifactId = artifactId.replace("ui", "omnifeed-ui")
        }
    }
    repositories {
        mavenLocal()
    }
}

// Force exclusion of unstable JogAmp dependencies from the test Runtime Classpath
// This will not affect the app's production execution, only prevent tests from attempting to download it.
configurations.matching { it.name.contains("Test") }.configureEach {
    exclude(group = "org.jogamp.gluegen")
    exclude(group = "org.jogamp.jogl")
}
