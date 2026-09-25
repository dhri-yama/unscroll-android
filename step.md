# Run Unscroll on this Mac

Unscroll is a native Kotlin and Jetpack Compose Android app. It requires Android 8.0 (API 26) or newer.

## Current setup

This Mac already has:

- Android SDK: `$HOME/Library/Android/sdk`
- Android platform 34
- Android build tools 34.0.0
- Android platform tools and `adb`
- A debug APK at `app/build/outputs/apk/debug/app-debug.apk`

At the time this guide was created, no physical device or emulator was connected to `adb`.

## Option 1: Run the existing APK on a physical device

A physical device is recommended for testing usage tracking, accessibility events, overlays, notifications, scrolling, and screen-lock behavior.

1. Enable **Developer options** on the Android phone.
2. Enable **USB debugging**.
3. Connect the phone to the Mac by USB.
4. Unlock the phone and accept the USB debugging authorization prompt.
5. Run:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"

adb devices -l
```

The phone should appear in the command output. Then install and launch the app:

```bash
cd /path/to/unscroll
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.unscroll.app/.ui.main.MainActivity
```

### Required app permissions

After opening Unscroll, grant the permissions requested during onboarding:

1. **Usage access**: Settings > Apps > Special app access > Usage access > Unscroll
2. **Display over other apps**: Settings > Apps > Special app access > Display over other apps > Unscroll
3. **Notifications**: Allow notifications when Android requests permission
4. **Accessibility**: Settings > Accessibility > Installed services or Downloaded apps > Unscroll Scroll Tracker

Menu names can vary slightly between Android versions. Complete onboarding, select the apps to monitor, choose the interruption interval, and start the monitoring service.

## Option 2: Create and run an Android emulator

The emulator is useful for quick onboarding and UI testing. Use a physical device for final testing of scrolling, overlays, accessibility, usage monitoring, and screen-lock behavior.

### Install Android Studio

If Android Studio is not already installed:

```bash
brew install --cask android-studio
```

Open Android Studio, then:

1. Select **Device Manager** from the welcome screen, or open **Tools > Device Manager**.
2. Select **Create Virtual Device**.
3. Choose a recent phone profile, such as **Pixel 7**.
4. Select an Android 14 or API 34 system image.
5. On Apple Silicon, use an `arm64-v8a` system image.
6. Finish the setup and start the emulator.
7. Wait for the Android home screen to appear.

Confirm that `adb` can see the emulator:

```bash
"$HOME/Library/Android/sdk/platform-tools/adb" devices -l
```

Install and launch the app:

```bash
cd /path/to/unscroll
"$HOME/Library/Android/sdk/platform-tools/adb" install -r app/build/outputs/apk/debug/app-debug.apk
"$HOME/Library/Android/sdk/platform-tools/adb" shell am start -n com.unscroll.app/.ui.main.MainActivity
```

Grant the same usage, overlay, notification, and accessibility permissions inside the emulator.

## Build the APK from source

The project uses Android Gradle Plugin 8.5.2 and requires Java 17. The default Java 26 and system Gradle 9.7.1 on this Mac should not be used for this project.

Install Java 17 if necessary:

```bash
brew install --cask temurin@17
```

Select Java 17 for the current terminal:

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

The required Gradle 8.9 distribution is already cached on this Mac. Build the debug APK with:

```bash
cd /path/to/unscroll
"$HOME"/.gradle/wrapper/dists/gradle-8.9-bin/*/gradle-8.9/bin/gradle assembleDebug
```

Install the newly built APK:

```bash
"$HOME/Library/Android/sdk/platform-tools/adb" install -r app/build/outputs/apk/debug/app-debug.apk
```

### Generate or refresh the Gradle wrapper

The project includes a Gradle 8.9 wrapper. After Java 17 is available, build with the wrapper:

```bash
cd /path/to/unscroll
./gradlew assembleDebug
```

To regenerate the wrapper, use the cached Gradle 8.9 distribution:

```bash
"$HOME"/.gradle/wrapper/dists/gradle-8.9-bin/*/gradle-8.9/bin/gradle wrapper --gradle-version 8.9
```

## Troubleshooting

### `adb: command not found`

Run:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
```

Or use the full executable path:

```bash
"$HOME/Library/Android/sdk/platform-tools/adb" devices
```

### `adb devices` shows `unauthorized`

Unlock the device, reconnect it, and accept the USB debugging prompt. If necessary, stop and restart the ADB server:

```bash
adb kill-server
adb start-server
```

### More than one device is connected

Specify the device serial shown by `adb devices -l`:

```bash
adb -s DEVICE_SERIAL install -r app/build/outputs/apk/debug/app-debug.apk
```

### Build fails because Java 26 or Gradle 9.7.1 is being used

Confirm and select Java 17:

```bash
export JAVA_HOME="$(/usr/libexec/java_home -v 17)"
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

Then run Gradle 8.9 as shown in the source-build section.

### Installation fails with `INSTALL_FAILED_UPDATE_INCOMPATIBLE`

An installed copy of Unscroll was signed with a different key. Uninstall it first, noting that this removes its local settings and onboarding progress:

```bash
adb uninstall com.unscroll.app
adb install app/build/outputs/apk/debug/app-debug.apk
```
