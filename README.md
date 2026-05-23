# OmniFeed SDK

[![Continuous Integration](https://github.com/lackary/omnifeed-kmp/actions/workflows/ci.yml/badge.svg)](https://github.com/lackary/omnifeed-kmp/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/lackary/omnifeed-kmp?include_prereleases&label=latest)](https://github.com/lackary/omnifeed-kmp/releases)
[![License](https://img.shields.io/github/license/lackary/omnifeed-kmp)](https://github.com/lackary/omnifeed-kmp/blob/main/LICENSE)
![Platform](https://img.shields.io/badge/platform-Android%20|%20iOS%20|%20Desktop|%20Web-blue)
![Kotlin](https://img.shields.io/badge/kotlin-2.3.0-purple?logo=kotlin)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-v1.10.0-blue)

> **A Modular, Production-Grade Kotlin Multiplatform SDK for Open APIs Integration.**
> *Designed with Clean Architecture, MVI patterns, and strict separation of concerns.*

## Project Overview
**OmniFeed** is a comprehensive Kotlin Multiplatform (KMP) library suite designed to provide seamless integration with the Unsplash API.

Unlike monolithic libraries, OmniFeed is architected as a **suite of composable modules**. Consumers can choose to import only the raw data layer (`omnifeed-unsplash`), or leverage the pre-built view models and state management (`omnifeed-ui`) for rapid development.

This project serves as a reference implementation for building **scalable, testable, and publishable KMP SDKs**.

## Modular Architecture
The library is structured into three distinct layers to enforce separation of concerns:

### 1. `omnifeed-core` (The Foundation)
- Provides the essential infrastructure for networking and logging.
- **Tech Stack**: Ktor (Network), Kermit (Logging), Kotlin Serialization.
- **Design Pattern**: Implements the **Bridge Pattern** for platform-specific loggers and HTTP engines.

### 2. `omnifeed-ui` (The Presentation Layer)
- Provides reusable, platform-agnostic **ViewModels** and **MVI State Holders**.
- **MVI Pattern**: Exposes strict `StateFlow` outputs and processes `Intent` inputs, ensuring unidirectional data flow.
- **Headless UI**: Designed to be UI-framework agnostic (compatible with Jetpack Compose, SwiftUI, or HTML DOM).

### 3. `omnifeed-unsplash` (The Domain & Data Layer)
- Encapsulates the specific business logic for the Unsplash API.
- **Clean Architecture**: Strictly separates `Domain` (Use Cases, Entities) from `Data` (Repositories, DTOs, Data Sources).
- **Features**:
    - OAuth 2.0 Authorization Flow.
    - Photo Searching, Listing, and User Profile management.
    - Offline-first capable caching strategy (Interface ready).

## Key Technical Highlights

### Factory & Facade Patterns
The SDK utilizes the **Factory Pattern** (e.g., `KtorClientFactory`) to instantiate platform-specific dependencies (Android/iOS/Wasm/Desktop engines) while keeping the consumer API uniform.

### Dependency Injection Ready
While the library uses [Koin](https://insert-koin.io/) internally for testability, it exposes its components in a way that allows consumers to easily integrate with their own DI solutions (Hilt, Dagger, or manual DI).

### Comprehensive Testing Strategy
- **Unit Tests**: Domain logic and Use Cases are heavily tested in `commonTest`.
- **Mocking**: Includes a custom `MockEngine` for Ktor to simulate API responses, ensuring tests are flaky-free and do not hit the real network.


## Kotlin Multiplatform Library Usage Guide

This project is a Kotlin Multiplatform Library. Below are instructions for importing this library into another app project.

---

### Method 1: Use includeBuild() to Import Source Directly

#### Steps (includeBuild method)

1. **Add to your app project's `settings.gradle.kts`:**

    ```kotlin
    includeBuild("/absolute/path/to/this/project")
    ```

    Example:

    ```kotlin
    includeBuild("../testing-kmmp")
    ```

2. **Add to your app project's `build.gradle.kts` dependencies:**

    ```kotlin
    implementation("com.lackstudio.module.kmp.testing:testing")
    ```

3. **Sync Gradle**

> Advantage: Any changes in the library source will be immediately reflected in the app without republishing.

---

### Method 2: Publish to Local Maven (mavenLocal)

#### Steps (publish to mavenLocal)

1. **Configure your library module's `build.gradle.kts`:**

    ```kotlin
    plugins {
        id("org.jetbrains.kotlin.multiplatform")
        id("com.android.library")
        id("maven-publish")
    }

    group = "com.lackstudio.module.kmp.testing"
    version = "0.0.1"

    kotlin {
        androidTarget()
        // ... other targets ...
    }

    android {
        namespace = "com.lackstudio.module.kmp.testing"
        compileSdk = 34
        defaultConfig { minSdk = 21 }
    }

    publishing {
        repositories {
            mavenLocal()
        }
    }
    ```

2. **In the library project directory, run:**

    ```sh
    ./gradlew publishToMavenLocal
    ```

    This will publish the library to `~/.m2/repository`.

3. **Add `mavenLocal` to your app project's `settings.gradle.kts`:**

    ```kotlin
    dependencyResolutionManagement {
        repositories {
            mavenLocal()
            google()
            mavenCentral()
        }
    }
    ```

4. **Add to your app project's `build.gradle.kts` dependencies and `libs.versions.toml`:**

    In `libs.versions.toml`:

    ```toml
    [libraries]
    omnifeed-xxxx = { group = "com.lackstudio.omnifeed", name = "omni-xxxx", version = "0.0.1" }
    ```

    In your app's `build.gradle.kts`:

    ```kotlin
    implementation(libs.omnifeed.xxxx)
    ```

    Or, without version catalog:

    ```kotlin
    implementation("com.lackstudio.module.kmp.testing:testing:0.0.1")
    ```

5. **Sync Gradle**

> Advantage: Allows sharing the library across projects/teams without exposing source code.

---

### Notes

- If you change the library source, you must re-run `publishToMavenLocal` to update the local maven repository.
- Adjust groupId, artifactId, and version according to your actual project settings.

## KMPAuth Integration Guide (iOS - Google Sign-In)

This guide explains how to set up Google Sign-In on iOS using KMPAuth.

### 1. Prerequisites

Before you begin, follow the KMPAuth documentation to set up your Firebase project and download the necessary configuration files:

- `GoogleService-Info.plist` (for the iOS app)

Place `GoogleService-Info.plist` in the `iosApp/` directory of your project.

### 2. Create Secret Configuration Files

To manage your API keys and client IDs securely, create two configuration files in the `iosApp/Configuration/` directory.

**`Secrets-Debug.xcconfig`**

```xcconfig
GOOGLE_CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID"
REVERSED_CLIENT_ID = "YOUR_REVERSED_CLIENT_ID"
```

**`Secrets-Release.xcconfig`**

```xcconfig
GOOGLE_CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID"
REVERSED_CLIENT_ID = "YOUR_REVERSED_CLIENT_ID"
```

> **Note:** Replace the placeholder values with your actual credentials. You can find `GOOGLE_CLIENT_ID` and `REVERSED_CLIENT_ID` inside your `GoogleService-Info.plist` file. `GOOGLE_SERVER_CLIENT_ID` is the web client ID, used for backend authentication.

### 3. Configure Xcode Project

Follow these steps to link the `.xcconfig` files and make the values available in your `Info.plist`.

#### Link xcconfig Files to Build Configurations

1. In Xcode, select your project file (`iosApp`) in the Project Navigator to open the project settings.
2. In the central editor pane, ensure you select the **PROJECT** (`iosApp`), not a target from the `TARGETS` list.
3. Navigate to the **Info** tab.
4. Under the **Configurations** section, you will see `Debug` and `Release`.
5. For the `Debug` configuration row, choose `Secrets-Debug` from the dropdown menu under the "Based on Configuration File" column.
6. For the `Release` configuration row, choose `Secrets-Release`. Targets will inherit these settings.

#### Read Values in Info.plist

1. Select the `iosApp` **target**.
2. Go to the **Info** tab.
3. Expand **URL Types**.
4. Click the `+` button to add a new URL Type.
5. In the **URL Schemes** field for the new type, enter `$(REVERSED_CLIENT_ID)`. The `$` notation tells Xcode to substitute the value from your `.xcconfig` file at build time.

This setup ensures that your sensitive keys are kept out of source control and are correctly configured for both debug and release builds.

---

## Kotlin CocoaPods Dependency Manager Setup Guide

This guide outlines the steps to configure a Kotlin Multiplatform project with CocoaPods and how to set up the iOS environment using our automation script.

### 1. Add Kotlin CocoaPods Dependency and Sync Gradle

First, ensure the Kotlin CocoaPods plugin is configured in your shared module's `build.gradle.kts`.

```kotlin
kotlin{
   iosArm64()
   iosSimulatorArm64()

   cocoapods {
      name = "Shared"
      version = "1.0.0" // Or any valid version number
      summary = "Some description for the Shared Module"
      homepage = "Link to the Shared Module homepage"
      ios.deploymentTarget = "18.5" // Specify your iOS deployment target
      podfile = project.file("../iosApp/Podfile") // Adjust path if needed
      framework {
         baseName = "Shared"
         isStatic = true
      }
   }
}
```

### 2. Initialize Pods

Define your dependencies in the `Podfile` located in your iOS project directory (`sampleApp/iosApp/Podfile`).

```sh
# Uncomment the next line to define a global platform for your project
platform :ios, '18.5'

target 'iosApp' do
  # Comment the next line if you don't want to use dynamic frameworks
  use_frameworks!

  # Pods for iosApp
  pod 'Shared', :path => '../Shared'
  pod '${dependency_you_need}'

end
```

### 3. One-Step Setup (Run the Script)

Instead of manually generating dummy frameworks and running pod install commands, we use a script to handle the initialization automatically. This solves common issues like missing resources (`[CP] Copy Pods Resources`) or framework errors.

Run the following command from the **project root directory**:

```shell
./setup_ios.sh
```

**What this script does:**
1. **Cleans** old Gradle builds to ensure a fresh state.
2. **Creates a dummy resource directory** to ensure CocoaPods generates the resource copy script.
3. **Generates a dummy framework** for CocoaPods detection.
4. **Runs `pod install`** with clean settings to ensure dependencies are linked correctly.

### 4. Open Project

Once the script finishes successfully:

1. Open `sampleApp/iosApp/iosApp.xcworkspace` in Xcode.
2. Select the `iosApp` target.
3. **Run** the app (Cmd + R).

**Note:** If you encounter any issues (e.g., missing resources), try running `./setup_ios.sh` again to reset the environment.

### 5. Generate the Real Framework

Finally, you can generate the real framework by building the project.

---

## SDK Architecture: Singleton Facade & Bridge Pattern

OmniFeed uses a **Singleton Facade** (`OmniFeed` object) combined with the **Bridge Pattern** to provide a clean, isolated API surface. The internal implementation uses Koin for dependency injection, but this is hidden from the consumer.

### Initialization

Initialize the SDK once (e.g., in `Application.onCreate` or at app startup):

```kotlin
OmniFeed.initialize(
    OmniFeedConfig(
        unsplash = UnsplashConfig(
            token = "YOUR_UNSPLASH_ACCESS_KEY"
        )
    )
)
```

### Integration Examples

Depending on your project's DI (Dependency Injection) choice, you can integrate OmniFeed as follows:

#### 1. Manual / Direct Access (No DI Framework)
Simply access the UseCases directly from the `OmniFeed` singleton.

```kotlin
class MyViewModel {
    fun fetchPhotos() {
        viewModelScope.launch {
            val photos = OmniFeed.getPhotosUseCase(page = 1)
            // handle results
        }
    }
}
```

#### 2. Koin Integration
If your project already uses Koin, you can bridge the `OmniFeed` UseCases into your own modules.

```kotlin
val appModule = module {
    // Bridge OmniFeed UseCases into Koin
    single { OmniFeed.getPhotosUseCase }
    single { OmniFeed.getSearchPhotosUseCase }

    factory { MyViewModel(get()) }
}

class MyViewModel(private val getPhotosUseCase: GetPhotosUseCase) : ViewModel() { 
    // TODO: ViewModel Logic
}
```

#### 3. Hilt Integration (Android)
For Hilt users, create a Module to provide the dependencies from the `OmniFeed` facade.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object OmniFeedModule {

    @Provides
    @Singleton
    fun provideGetPhotosUseCase(): GetPhotosUseCase = OmniFeed.getPhotosUseCase

    @Provides
    @Singleton
    fun provideSearchPhotosUseCase(): SearchPhotosUseCase = OmniFeed.getSearchPhotosUseCase
}

@HiltViewModel
class MyViewModel @Inject constructor(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() { 
    // TODO: ViewModel Logic
}
```

### Logging Integration (Kermit)

OmniFeed uses [Kermit](https://github.com/touchlab/Kermit) for logging. You can pass your existing `Logger` instance into `OmniFeedConfig` to unify the logs between your app and the SDK.

```kotlin
// In your App's initialization
val myLogger = Logger(
    config = StaticConfig(logWriterList = listOf(platformLogWriter())), 
    tag = "MyApp"
)

OmniFeed.initialize(
    OmniFeedConfig(
        appLogger = myLogger, // Pass your logger here
        unsplash = UnsplashConfig(token = "...")
    )
)
```

By doing this, all internal SDK logs will be routed through your configured `myLogger`, respecting your custom log writers and settings.
