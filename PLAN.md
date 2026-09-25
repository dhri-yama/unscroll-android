# Unscroll — Android session scroll-reckoning app

## Goal

A sideloaded Kotlin + Jetpack Compose Android app that monitors your usage of
user-selected apps (Instagram / YouTube / TikTok / anything you pick).

Every 10 minutes while you are actively using the phone, a **full-screen
blocking overlay** covers the screen showing:

- Top current timestamp (e.g., `Current time is 14:58`)
- Sequentially rotated insult from a local set of 50 curated insults about wasting time and potential
- Session stats breakdown (time wasted, scrolls count, scroll distance, estimated Reels/Shorts)
- Prominent countdown timer / duration display
- Glassmorphic action buttons (`>> Skip` and `🔒 Lock Screen`)

Either response button dismisses the overlay, as does locking the device. Tapping
outside the controls does nothing.

## Confirmed decisions

- **Target apps:** user picks from installed apps (settings screen)
- **Overlay behavior:** full blocking screen styled after modern ambient design (dark blurred backdrop with warm glow); only the two response buttons dismiss it, or locking the device. Tapping outside the controls does nothing.
- **Insult system:** 50 curated insults stored locally in code, chosen sequentially (0..49 round-robin, persisted index in DataStore) — no remote API calls
- **Distribution:** sideload APK only (Play Store policy blocks this pattern)
- **Tech stack:** Kotlin + Jetpack Compose (required for system APIs & custom overlay layout)
- **Session definition:** one combined session across all tracked apps;
  session = continuous screen-on/unlocked usage of tracked apps, resets on lock

## Architecture, SOLID & Low-Level Design (LLD) Principles

The project strictly follows **Clean Architecture** and **SOLID** principles:

1. **Single Responsibility Principle (SRP):**
   - `ScrollTrackerAccessibilityService`: Captures raw UI gesture events only.
   - `CalculateReelHeuristicsUseCase`: Evaluates gesture deltas into Reels/Shorts counts.
   - `OverlayManagerImpl`: Handles Android `WindowManager` lifecycle & view attachments.
   - `AmbientOverlayScreen`: Pure Compose UI declaration without direct business logic.
2. **Open/Closed Principle (OCP):**
   - Domain interfaces (`InsultRepository`, `SessionStatsRepository`, `OverlayController`) allow swapped or mocked implementations without altering business logic.
3. **Liskov Substitution Principle (LSP):**
   - Subtypes (e.g., `DataStoreSettingsRepositoryImpl`, `LocalInsultRepositoryImpl`) strictly adhere to domain repository contracts without breaking behavioral expectations.
4. **Interface Segregation Principle (ISP):**
   - Fine-grained interfaces (`ScrollEventListener`, `SessionStateListener`, `InsultProvider`) ensure modules depend only on methods they consume.
5. **Dependency Inversion Principle (DIP):**
   - High-level services (`SessionMonitorForegroundService`, `MainViewModel`) depend on domain UseCases and Repository interfaces rather than concrete singletons. Dependency injection is wired cleanly via `AppContainer` (Service Locator pattern).

---

## Memory & Workflow Strategy (`working.md`)

To maintain precise state awareness across execution steps, the project will maintain a living `working.md` memory file in the root directory:

- **State Tracking:** `working.md` tracks current build phase, component completion status, active decisions, and immediate next steps.
- **Section Structure:**
  1. `## Current Objective & Phase` — Active task in the build order.
  2. `## Architecture & Context State` — Key implementation patterns, singletons, and dependencies wired.
  3. `## File & Component Checklist` — Live progress tree of domain, data, service, and UI components.
  4. `## Known Issues, Blockers & Heuristics Notes` — Gesture thresholds, permission edge cases, and runtime notes.
  5. `## Immediate Next Steps` — Direct action plan for the next coding step.
- **Update Cadence:** `working.md` is updated continuously after completing each phase in the build order.

---


## Directory Structure & Planned Files

```
app/src/main/
├── AndroidManifest.xml
├── res/
│   ├── xml/
│   │   └── accessibility_service_config.xml
│   └── drawable/
└── java/com/unscroll/app/
    ├── UnscrollApplication.kt
    │
    ├── di/
    │   └── AppContainer.kt                        # Dependency Injection container / Service Locator
    │
    ├── domain/
    │   ├── model/
    │   │   ├── AppUsageInfo.kt                    # Data model for tracked app usage stats
    │   │   ├── Insult.kt                          # Data model for local insult items & templates
    │   │   ├── UserProfile.kt                     # Data model for user name & insult customization
    │   │   └── SessionStats.kt                    # Data model for active session statistics
    │   ├── repository/
    │   │   ├── InsultRepository.kt                # Contract for obtaining sequential insults
    │   │   ├── SessionStatsRepository.kt          # Contract for active session stats management
    │   │   └── SettingsRepository.kt              # Contract for target app selections, user name & config
    │   └── usecase/
    │       ├── CalculateReelHeuristicsUseCase.kt  # Computes Reels count from vertical scroll deltas
    │       ├── EvaluateOverlayTriggerUseCase.kt  # Evaluates 10-min boundary conditions for overlay
    │       ├── GetNextInsultUseCase.kt           # Fetches insult & formats dynamic user name placeholders
    │       └── TrackScrollEventUseCase.kt         # Updates active session with scroll metrics
    │
    ├── data/
    │   ├── source/
    │   │   ├── CuratedInsultsDataSource.kt        # Hardcoded array of 50 time-waste/potential insults with {name} placeholders
    │   │   └── UsageStatsDataSource.kt           # Android UsageStatsManager wrapper
    │   └── repository/
    │       ├── DataStoreSettingsRepositoryImpl.kt # DataStore implementation of SettingsRepository (name, apps, interval)
    │       ├── LocalInsultRepositoryImpl.kt       # DataStore + DataSource implementation of InsultRepository
    │       └── MemorySessionStatsRepositoryImpl.kt# Thread-safe in-memory + DataStore SessionStatsRepository
    │
    ├── service/
    │   ├── accessibility/
    │   │   └── ScrollTrackerAccessibilityService.kt # Listens to TYPE_VIEW_SCROLLED events
    │   ├── monitor/
    │   │   └── SessionMonitorForegroundService.kt  # Background loop (~30s) monitoring session state
    │   └── overlay/
    │       ├── OverlayController.kt              # Interface for controlling the full-screen overlay
    │       └── WindowManagerOverlayController.kt # WindowManager TYPE_APPLICATION_OVERLAY manager
    │
    └── ui/
        ├── theme/
        │   ├── Color.kt                           # Color palette (dark ambient + warm olive glow)
        │   ├── Theme.kt                           # Unscroll Compose Material3 Theme
        │   └── Type.kt                            # Typography setup for overlay headlines & tickers
        ├── components/
        │   ├── AmbientBackground.kt               # Blurred gradient background painter
        │   ├── GlassmorphicPillButton.kt          # Reusable glassmorphic button component
        │   └── StatCounter.kt                     # Stat display block component
        ├── overlay/
        │   ├── AmbientOverlayScreen.kt            # Compose full-screen layout (matching reference image)
        │   └── AmbientOverlayViewModel.kt         # ViewModel feeding stats & personalized insult to overlay UI
        ├── onboarding/
        │   ├── OnboardingFlowScreen.kt            # Multi-step onboarding carousel/pager host
        │   ├── OnboardingViewModel.kt             # Manages onboarding step transitions & initial setup completion state
        │   └── steps/
        │       ├── WelcomeStep.kt                 # Step 1: Ambient welcome & vision overview
        │       ├── NamePersonalizationStep.kt     # Step 2: Enter name for personalized insults
        │       ├── TargetAppPickerStep.kt         # Step 3: Quick select doomscrolling apps
        │       ├── PermissionsWizardStep.kt       # Step 4: Interactive permissions guide (Usage, Overlay, Accessibility)
        │       └── CadenceSetupStep.kt            # Step 5: Interruption interval setup & launch reckoning
        └── main/
            ├── MainActivity.kt                    # Main activity host with navigation drawer / bottom bar
            ├── MainViewModel.kt                   # ViewModel for setup wizard & settings management
            └── screens/
                ├── UserProfileSettingsScreen.kt   # Enter User Name for personalized insults & preferences
                ├── TargetAppsSettingsScreen.kt    # Checkbox list of installed apps (QUERY_ALL_PACKAGES)
                ├── CadenceSettingsScreen.kt       # Interruption timer interval picker (5m, 10m, 15m, 30m)
                └── PermissionsDashboardScreen.kt  # Active background service toggles & system permission status


```

---

## Key technical approach

| Concern | Mechanism |
| --- | --- |
| Time tracked per app | `UsageStatsManager` (needs *Usage Access* permission) |
| Scrolls counted | `AccessibilityService` with **no** package filter in XML (empty list = receive all apps), filtered in code by the user's selected apps; `TYPE_VIEW_SCROLLED` events tallied in real time |
| Reels / Shorts estimate | Heuristic: a vertical scroll with delta >= ~40% of screen height = one video advanced (estimate, not byte-exact) |
| Scroll length | Summed pixels converted to meters/cm via screen density |
| Full-screen overlay | `WindowManager`, `TYPE_APPLICATION_OVERLAY` (needs *Display over other apps*), blocking overlay with custom Compose layout |
| Visual Styling & Design Taste | Designed using `Leonxlnx/taste-skill` (`.agents/skills/` design skills: `high-end-visual-design`, `minimalist-ui`, `image-to-code`, `imagegen-frontend-mobile`). Ambient blurred gradient backdrop (dark tones + warm olive glow), bold title typography, subtle horizontal divider, giant timer ticker (`00:34`), glassmorphic pill buttons (`>> Skip` and `🔒 Lock Screen`) |
| 10-min cadence | Foreground service (with persistent notification) checks every ~30s and fires the overlay at each 10-min boundary while the screen is on and a tracked app is in use |
| Local Insult Store | Curated array of 50 offline insults focusing on wasted time and lost potential, iterated sequentially via persisted DataStore index |


## Modules

| Component | What it does |
| --- | --- |
| `MainActivity` (Compose) | Setup wizard: grant Usage Access, Draw-over-other-apps, enable a11y service (verified via `AccessibilityManager`). Settings: pick target apps (checkbox list of installed apps via `QUERY_ALL_PACKAGES`), interval (default 10m). Current-session stats screen. Start/stop service. |
| `SessionMonitorForegroundService` | Loop every ~30s reading `UsageStatsManager`; accumulates per-app + session totals; fires overlay + summary notification each 10-min boundary via `EvaluateOverlayTriggerUseCase`. |
| `ScrollTrackerAccessibilityService` | Tallies scrolls, scroll px distance, reel heuristics per app via `TrackScrollEventUseCase` into `SessionStatsRepository`. |
| `WindowManagerOverlayController` | Renders the ambient full-screen overlay via `WindowManager`; Compose content matching reference design (top status time, big bold sequential insult title, stats body, thin divider, giant timer ticker, `Skip` & `Lock Screen` pill buttons). |
| `LocalInsultRepositoryImpl` | Hardcoded array of 50 time-waste & potential insults + sequential index rotation logic saved in DataStore via `GetNextInsultUseCase`. |
| `AppContainer` | Manages creation, scoping, and dependency injection of singletons, repositories, and use cases across services and ViewModels. |

## Permissions (manifest)

- `PACKAGE_USAGE_STATS`
- `SYSTEM_ALERT_WINDOW`
- `FOREGROUND_SERVICE` (+ `FOREGROUND_SERVICE_SPECIAL_USE`)
- `POST_NOTIFICATIONS`
- `QUERY_ALL_PACKAGES`
- Accessibility service + XML declaration

Sideload-only; Play Protect will warn when installing — expected.

## Environment setup (needed before coding)

This machine has Java 26 but **no Android SDK, no Gradle, no adb**.

1. `brew install --cask temurin@17` — requirement: current AGP/Gradle do **not**
   support JDK 26.
2. `brew install --cask android-commandlinetools`
3. `sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"` +
   accept licenses
4. Gradle via wrapper (auto-downloads); build with `./gradlew assembleDebug`
5. Target device: Android phone via USB debugging (`adb install app-debug.apk`);
   emulator as fallback

## Build order

1. Toolchain (above)
2. Gradle scaffold (Kotlin + Compose, minSdk 26, target 34)
3. Manifest + a11y XML
4. Domain models & contracts (`domain/model`, `domain/repository`, `domain/usecase`)
5. Dependency Injection (`AppContainer`)
6. `CuratedInsultsDataSource` + `LocalInsultRepositoryImpl` (50 curated insults + sequential index manager)
7. Settings & `MemorySessionStatsRepositoryImpl`
8. `ScrollTrackerAccessibilityService`
9. `SessionMonitorForegroundService` + 10-min cadence logic
10. `WindowManagerOverlayController` + ambient Compose overlay UI (designed using `taste-skill` / `high-end-visual-design`)
11. `MainActivity` screens + permission wizard
12. Notifications
13. Build APK, install on phone, smoke-test against Reels/Shorts, tune heuristics

## Caveats

- Reel/short count is a swipe heuristic — close, not exact.
- UsageStats rounds to ~1-min buckets, so "wasted time" is approximate.
- Accessibility service + overlay across apps = sideload only.