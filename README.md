# Unscroll

An Android app that keeps score of the apps you say you'll stop scrolling, and
interrupts you when you've had enough.

Pick the apps you want to keep an eye on. While you're actively using your phone,
Unscroll watches how much you scroll and how far. Every few minutes it drops a
full-screen overlay in your face with a rotating insult, your session stats, and a
countdown — and you have to acknowledge it.

Sideloaded app. No Play Store, no accounts, no network calls. Everything runs
locally on your device.

## What it does

- **Tracks the apps you choose** from your installed list — Instagram, YouTube,
  TikTok, whatever you pick.
- **Counts scrolls and scroll distance** via an accessibility service, and
  estimates Reels/Shorts-style content consumed.
- **Interrupts on an interval** you set during onboarding (1, 5, 10, 15, 20, or
  30 minutes) with a blocking overlay: current time, a sequentially rotated
  insult from a local set of 50, your session stats, and a countdown timer.
- **Tracks one session**, combining all your tracked apps into a single streak.
  Locking your phone ends the session; unlocking starts a fresh one from zero.
- **Records how you respond.** Each overlay has two buttons, and both dismiss it.
  Tapping the background does nothing, and locking your device dismisses it too.
  Daily tallies for each button are kept for a year and charted on the Profile
  screen over 7, 30, or 90 days.

## Screens

| Screen | What it's for |
| --- | --- |
| **Onboarding** | Name, permission grants, tracked apps, interruption interval |
| **Live dashboard** | Session timer, reels/gestures/distance metrics, event stream |
| **Targets** | Choose which installed apps to track |
| **Cadence** | Change the interruption interval |
| **Profile** | Your name, plus per-day charts of which response button you reach for |
| **System** | Permission status, refresh, restart monitoring |
| **Overlay** | The blocking interruption screen (drawn over other apps) |

## Build and install

Requires **Java 17** and the **Android SDK** (platform 34, build-tools 34).
AGP 8.5.2, Gradle 8.9, Kotlin 2.0.20, Jetpack Compose (BOM 2024.09.00).
`minSdk 26`, `targetSdk 34`.

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 17)"

./gradlew assembleDebug

adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.unscroll.app/.ui.main.MainActivity
```

If Java 17 isn't installed: `brew install --cask temurin@17`.

## Permissions

Unscroll needs special-access permissions, and each one is granted by hand — the
app can't request them through a normal dialog.

- **Usage access** (`PACKAGE_USAGE_STATS`) — which apps you're using and for how long.
- **Display over other apps** (`SYSTEM_ALERT_WINDOW`) — draw the interruption overlay.
- **Accessibility service** — observe scroll gestures to count them and measure distance.
- **Notifications** (`POST_NOTIFICATIONS`) — the ongoing monitoring notification.
- **Query all packages** (`QUERY_ALL_PACKAGES`) — list installed apps for you to choose from.

Play Protect will warn when you sideload. That's expected.

## Architecture

Plain layered Kotlin, no framework, wired by hand through a small container.

```
domain/         models, repository + use case contracts (no Android deps)
data/           DataStore settings, in-memory session stats, insult source
di/             AppContainer — manual dependency injection
service/
  accessibility ScrollTrackerAccessibilityService — scroll gesture counting
  monitor       SessionMonitorForegroundService — the loop, session lifecycle
  overlay       WindowManagerOverlayController — draws over other apps
ui/             Compose screens and shared components
```

**Session state is derived, not broadcast.** A session is "screen on, unlocked,
in active use of a tracked app." A foreground service polls that state once a
second and reconciles on `SCREEN_ON` / `USER_PRESENT` / `SCREEN_OFF`, so the
session can't get stuck if a broadcast is ever missed. Locking ends the session;
unlocking starts a new one at `0:00` with counters reset.

## Tests

```bash
./gradlew testDebugUnitTest
```

70 unit tests cover session reconciliation, the glitch engine's random timing and
color accents, daily response statistics, chart geometry, and the repository
guards. `lintDebug` and `assembleDebug` are also clean.

## Known limitations

- **Not Play Store distributable.** Overlay-based interruption patterns conflict
  with Play policy, hence sideloading.
- **Untested on physical devices.** Verified on an Android emulator only. Real
  keyguard/OEM lock behavior (`ACTION_USER_PRESENT` delivery) hasn't been
  confirmed on hardware, and background process-death recovery (`START_STICKY`)
  is unverified.
- **Scroll distance is an estimate** from accessibility gesture deltas, not a
  ground-truth pixel measurement.
- **Requires the app process to be alive or the service sticky-restarted** for
  monitoring to keep running.

## Notes

`step.md` has day-to-day install and troubleshooting notes.
`PLAN.md` is the original design spec; `working.md` is a development log.
