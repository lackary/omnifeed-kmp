# OmniFeed Project Map & Context (GEMINI.md)

This document provides high-level context for Gemini to navigate the OmniFeed KMP repository.

## 1. Repository Structure
- `omnifeed-core/`: Core SDK infrastructure.
    - `src/commonMain/kotlin/.../network/`: Ktor configuration and OAuth logic.
    - `src/commonMain/kotlin/.../domain/usecase/`: Base `UseCase` and `UseCaseHandler` logic.
- `omnifeed-unsplash/`: Unsplash integration logic.
    - `src/commonMain/`: Domain models and repository interfaces.
    - `src/androidMain/`, `src/iosMain/`, etc.: Platform-specific API keys or client configurations.
- `omnifeed-ui/`: Shared UI library.
    - `src/commonMain/`: Compose Multiplatform components like `OAuthWebViewBottomSheet`.
- `sample/`: Reference implementations for Android, iOS, and Desktop.

## 2. Tech Stack & Versions
- **KMP:** Targeting Android, iOS (CocoaPods), JVM (Desktop), and WasmJs (Web).
- **Network:** Ktor 3.x with ContentNegotiation (Serialization).
- **DI:** Koin 4.x.
- **Logging:** Kermit.
- **Image Loading:** Coil 3 (Multiplatform version).

## 3. Key Logic Context
- **OAuth Flow:** Handled via `OAuthWebViewBottomSheet` in `omnifeed-ui`. It intercepts redirect URLs to capture the `code`.
- **State Management:** Uses `BaseViewModel` in `omnifeed-ui` which leverages `StateFlow` and `SharedFlow` for MVI patterns.
- **SDK Entry:** Users initialize the library via `OmniFeedConfig` or Koin modules.

## 4. Current Focus
- Ensuring **WasmJs** compatibility for all networking modules.
- Implementing **Live Streaming** data structures in `omnifeed-core`.
