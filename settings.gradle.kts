enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()

        maven("https://jogamp.org/deployment/maven/")
    }
    plugins {
        id("com.codingfeline.buildkonfig") version "0.22.0" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        maven("https://jogamp.org/deployment/maven/")
    }
}

rootProject.name = "omnifeed-kmp"

include(":sampleApp:shared")
include(":sampleApp:androidApp")
include(":sampleApp:desktopApp")
include(":omnifeed")
include(":omnifeed-core")
include(":omnifeed-ui")
include(":omnifeed-unsplash")
include(":omnifeed-auth")
