# Unscroll — Working Memory & Development Log

## Current Objective & Phase
- **Phase:** Environment Setup & Gradle Scaffold Initialization (Phase 1 & 2)
- **Status:** Planning complete; preparing toolchain & Kotlin + Jetpack Compose scaffold setup.

## Architecture & Context State
- **Pattern:** Clean Architecture (Domain, Data, Service, UI, DI via `AppContainer`).
- **Design System:** Jetpack Compose Material3 + Ambient Blurred Glassmorphic Overlay (`taste-skill` / `high-end-visual-design`).
- **Insult Engine:** Offline local `InsultRepository` (50 sequential time-waste & potential insults).
- **Tracking:** `AccessibilityService` (`TYPE_VIEW_SCROLLED`) + `UsageStatsManager` foreground monitoring loop.

## File & Component Checklist
- [ ] Toolchain Setup (JDK 17, Android SDK CLI, Platform 34)
- [ ] Root Gradle Scaffold (`build.gradle.kts`, `settings.gradle.kts`, `app/build.gradle.kts`)
- [ ] `AndroidManifest.xml` & `accessibility_service_config.xml`
- [ ] `domain/model` (`AppUsageInfo.kt`, `Insult.kt`, `UserProfile.kt`, `SessionStats.kt`)
- [ ] `domain/repository` contracts (`InsultRepository.kt`, `SessionStatsRepository.kt`, `SettingsRepository.kt`)
- [ ] `domain/usecase` (`GetNextInsultUseCase.kt` with `{name}` placeholder substitution, `CalculateReelHeuristicsUseCase.kt`, `TrackScrollEventUseCase.kt`, `EvaluateOverlayTriggerUseCase.kt`)
- [ ] `di/AppContainer.kt`
- [ ] `data/source/CuratedInsultsDataSource.kt` (50 curated insults with `{name}` placeholders)
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
