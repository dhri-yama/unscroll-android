# Unscroll — Working Memory & Development Log

## Current Objective & Phase
- **Phase:** Data & Repository Layer Implementation (Tranche 2)
- **Status:** Tranche 1 (Toolchain, Gradle Scaffold, Domain Models & Contracts, Use Cases, 50 Curated Insults Data Source) complete and committed to git.

## Architecture & Context State
- **Pattern:** Clean Architecture (Domain, Data, Service, UI, DI via `AppContainer`).
- **Design System:** Jetpack Compose Material3 + Ambient Blurred Glassmorphic Overlay (`taste-skill` / `high-end-visual-design`).
- **Insult Engine:** Offline local `InsultRepository` (50 sequential time-waste & potential insults with `{name}` dynamic placeholders).
- **Tracking:** `AccessibilityService` (`TYPE_VIEW_SCROLLED`) + `UsageStatsManager` foreground monitoring loop.

## File & Component Checklist
- [x] Toolchain & Git Init Setup (`.gitignore`, Gradle CLI)
- [x] Root Gradle Scaffold (`build.gradle.kts`, `settings.gradle.kts`, `app/build.gradle.kts`, `gradle/libs.versions.toml`)
- [x] `domain/model` (`AppUsageInfo.kt`, `Insult.kt`, `UserProfile.kt`, `SessionStats.kt`)
- [x] `domain/repository` contracts (`InsultRepository.kt`, `SessionStatsRepository.kt`, `SettingsRepository.kt`)
- [x] `domain/usecase` (`GetNextInsultUseCase.kt`, `CalculateReelHeuristicsUseCase.kt`, `TrackScrollEventUseCase.kt`, `EvaluateOverlayTriggerUseCase.kt`)
- [x] `data/source/CuratedInsultsDataSource.kt` (50 curated insults with `{name}` placeholders)
- [ ] `di/AppContainer.kt`
- [ ] `data/repository/LocalInsultRepositoryImpl.kt`
- [ ] `data/repository/MemorySessionStatsRepositoryImpl.kt`
- [ ] `data/repository/DataStoreSettingsRepositoryImpl.kt` (stores user name, target apps, interval)

- [ ] `service/accessibility/ScrollTrackerAccessibilityService.kt`
- [ ] `service/monitor/SessionMonitorForegroundService.kt`
- [ ] `service/overlay/WindowManagerOverlayController.kt`
- [ ] `ui/onboarding/OnboardingFlowScreen.kt` & `OnboardingViewModel.kt`
- [ ] `ui/onboarding/steps/` (`WelcomeStep.kt`, `NamePersonalizationStep.kt`, `TargetAppPickerStep.kt`, `PermissionsWizardStep.kt`, `CadenceSetupStep.kt`)
- [ ] `ui/main/MainActivity.kt` (Onboarding vs Main Dashboard routing)
- [ ] `ui/main/screens/UserProfileSettingsScreen.kt` (Personalized name & preferences)
- [ ] `ui/main/screens/TargetAppsSettingsScreen.kt` (App selector)
- [ ] `ui/main/screens/CadenceSettingsScreen.kt` (Interruption interval timer)
- [ ] `ui/main/screens/PermissionsDashboardScreen.kt` (Service & permission controls)



## Known Issues, Blockers & Heuristics Notes
- Local machine runs JDK 26 without preinstalled Android SDK/Gradle; JDK 17 & `android-commandlinetools` required before building APK.
- Reels/Shorts detection uses ~40% screen height vertical delta swipe heuristic.

## Immediate Next Steps
1. Install JDK 17 & Android SDK via Homebrew/sdkmanager.
2. Initialize Gradle wrapper and scaffold root project files.
