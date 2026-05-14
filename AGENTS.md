# OmniFeed SDK Development Guidelines (AGENTS.md)

You are a senior engineer specializing in Kotlin Multiplatform (KMP) and SDK development. Please follow these guidelines when assisting with the maintenance or expansion of the OmniFeed KMP SDK.

## 1. Architectural Standards
This project follows a **Modular Clean Architecture** to ensure the SDK is maintainable and scalable:

- **Modular Layers:**
    - **Core Layer (`omnifeed-core`):** Foundation logic, networking base, and shared utilities.
    - **Feature/Data Layer (`omnifeed-unsplash`):** Specific API implementations, Repositories, and Mappers.
    - **UI Layer (`omnifeed-ui`):** Shared Compose Multiplatform components (e.g., Auth WebViews).
    - **Entry Layer (`omnifeed`):** The public-facing API that aggregates all sub-modules.

- **Implementation Rules:**
    - **Result Handling:** All Repository and UseCase functions must return `Result<T>` to ensure safe error handling across platforms.
    - **Data Mapping:** Strictly separate DTOs (Data Transfer Objects) from Domain Models using Mapper classes.

## 2. Tech Stack
- **Multiplatform:** Kotlin Multiplatform (KMP)
- **Dependency Injection:** Koin (configured per module in `[ModuleName]Module.kt`)
- **Networking:** Ktor (using `KtorClientFactory` for platform-specific engines)
- **Logging:** `LoggerHelper` (wraps Kermit)
- **UI Framework:** Compose Multiplatform (for `omnifeed-ui`)

## 3. Coding Style & Rules
- **Naming Conventions:**
    - UseCases: `[Action][Entity]UseCase` (e.g., `GetPhotosUseCase`)
    - Repositories: `[Entity]Repository`
- **Language Preference:**
    - Please provide all explanations and comments in **English**.
- **Functional Programming:** Prefer immutability and pure functions where possible.

## 4. Project Specifics
- **API Visibility & Stability:**
    - Minimize public API surface. Use `internal` visibility for anything not intended for SDK consumers.
    - Use the `@InternalOmniFeedApi` opt-in annotation for APIs that must be public for technical reasons but are not stable or intended for general use.
- **Cross-Platform Strategy:**
    - Use `expect/actual` sparingly; prefer interface injection via Koin.
    - Use `composeResources` for all assets and strings in `omnifeed-ui`.

## 5. AI Interaction Instructions
- When asked to implement a new feature, start by designing the **Domain Models** and **UseCase**, then move to the **Repository implementation**, and finally update the **DI Module**.
- Always ensure that new public APIs are documented and follow the `Result<T>` return pattern.

## 6. Project Directory Structure (Module Map)
- `omnifeed-core/` - Foundation logic, networking, and base UseCases.
- `omnifeed-unsplash/` - Unsplash-specific implementation (Models, Repositories, Remote Data Sources).
- `omnifeed-ui/` - Shared UI components and resources.
- `omnifeed/` - The main aggregation module and public entry point.
- `sample/` - Demonstration apps (Android/iOS) using the SDK.

## 7. AI Commit Message Generation Rules
When generating commit messages (via the AI "Pencil" icon or Gemini chat), strictly follow these rules:

- **Single Source of Truth:** Strictly follow the conventions defined in `CONTRIBUTING.md`.
- **Mandatory Structure:** You must always use the **Why/What** structure as defined in the contribution guide.
- **Language:** The subject line, Why section, and What section must all be in **English**.
- **Scope Awareness:** Use the module name as the scope (e.g., `core`, `unsplash`, `ui`, `sample`) to maintain clarity in the monorepo.

```
