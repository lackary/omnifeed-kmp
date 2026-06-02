import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

val modulePackageName = "io.lackstudio.omnifeed.auth"

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    id("maven-publish")
}

base {
    archivesName.set("omnifeed-auth")
}

kotlin {
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

    val xcf = XCFramework()
    listOf(iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "OmniFeedAuth"
            xcf.add(this)
            isStatic = true
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
            api(libs.koin.core)
        }
        
        val nonWasmMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                api(libs.gitlive.firebase.auth)
            }
        }
        
        androidMain.get().dependsOn(nonWasmMain)

        /**
         * Note: The error "Expected omnifeedAuthModule has no actual declaration in module <commonMain> for Native"
         * occurs during the OmniHub iOS build because KMP cannot find the path to nonWasmMain when compiling 
         * metadata for the iOS intermediate layer (iosMain).
         * 
         * To simplify the configuration using iosMain, ensure that applyDefaultHierarchyTemplate() is enabled
         * and use the following code to maintain the hierarchy link:
         * 
         * // Force apply the default hierarchy template to ensure iosMain is automatically created
         * applyDefaultHierarchyTemplate() 
         * 
         * val iosMain by getting {
         *    dependsOn(nonWasmMain)
         * }
         * 
         * This way, the child source sets iosArm64Main and iosSimulatorArm64Main won't need manual dependsOn(nonWasmMain).
         */
        iosArm64Main.get().dependsOn(nonWasmMain)
        iosSimulatorArm64Main.get().dependsOn(nonWasmMain)
        jvmMain.get().dependsOn(nonWasmMain)
        jsMain.get().dependsOn(nonWasmMain)
        
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.google.firebase.bom))
            implementation(libs.google.firebase.auth)
            implementation(libs.google.firebase.common)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
