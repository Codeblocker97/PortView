PortView – Holdings Portfolio (Clean MVVM, Compose)

A modern Android app that displays a holdings portfolio with an expandable summary, offline cache, and pull-to-refresh. Built with Clean Architecture + MVVM, Jetpack Compose, Kotlin Coroutines/Flow, Retrofit with Kotlinx Serialization, Room, and Hilt DI. The UI follows Material 3 and formats portfolio P&L with proper INR conventions.

Key features

Holdings list with symbol, LTP, Net Qty, and per-item P&L, color-coded for profit/loss.

Expandable/collapsible portfolio summary showing Total P&L and percentage; details include Current Value, Total Investment, and Today’s P&L.

Pull-to-refresh using Material 3 PullToRefreshBox, plus graceful loading and error states.

Offline-first behavior with Room cache; fetches from network when available, falls back to local data on errors.

Clean MVVM with domain use cases and repository abstraction; DI via Hilt.

Architecture

Presentation: Jetpack Compose screens + ViewModels (StateFlow), unidirectional data flow, no business logic in UI.

Domain: Pure Kotlin models and use cases (e.g., ComputePortfolioSummaryUseCase).

Data: Retrofit + Kotlinx Serialization for network, Room for persistence, Repository coordinates sources.

Dependency Injection: Hilt modules provide API, DB/DAO, Repository, and UseCases.

Tech stack

Language: Kotlin, Coroutines, Flow.

UI: Jetpack Compose (Material 3), PullToRefreshBox.

Networking: Retrofit + Kotlinx Serialization converter, OkHttp logging.

Persistence: Room (KSP).

DI: Hilt (KSP), hilt-navigation-compose.

Testing: JUnit, Mockito for unit tests; Espresso + Compose UI tests.

Data flow

ViewModel requests holdings via GetHoldingsUseCase. Repository tries network; on success, saves to Room and emits domain models; on failure, falls back to Room cache. ViewModel maps domain → UI models (HoldingUiModel, PortfolioSummaryUiModel) and exposes state to Compose.

Project structure

app/

di/ Hilt modules (Network, Database, Repository, UseCases)

ui/ Compose screens, components, and format utilities (INR)

domain/ models, repository interface, use cases

data/ remote (API, DTOs, mappers), local (Room DB/DAO/entities, mappers), repository impl

Setup

Prerequisites

Android Studio Narwhal (2025.1.1)

Android SDK 36 (Android 15)

JDK 11

Build config

compileSdk = 36, targetSdk = 36, minSdk = 26

Kotlin = 2.2.10, AGP = 8.11.1

KSP enabled; no kapt

Run

Open in Android Studio, sync Gradle, and Run.

If using adaptive icons, minSdk 26 or ensure legacy icons exist for pre-26 devices.

Testing

Unit: Run tests under test/ for use cases, repository, and ViewModel with Mockito.

UI: Run androidTest/ for Compose UI tests and Espresso at the activity level.

Why this approach

Clean + MVVM improves testability and scalability; each layer has a single responsibility and depends on abstractions (SOLID).

Kotlinx Serialization integrates cleanly with Retrofit and Compose-first codebases.

Room + Flow enable offline-first UX and reactive updates.

Material 3 pull-to-refresh and Compose docs for interaction patterns.

Clean Architecture with Compose, Hilt, Retrofit, Room patterns.
