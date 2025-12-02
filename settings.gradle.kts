enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()

        maven("https://jogamp.org/deployment/maven/")
    }
    plugins {
        id("com.codingfeline.buildkonfig") version "0.17.1" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        maven("https://jogamp.org/deployment/maven/")
    }
}

rootProject.name = "omnifeed"

include(":composeApp")
include(":core")
include(":ui")
include("integrations:unsplash")
