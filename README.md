# CallApp

A custom Android dialer prototype built with Kotlin and Jetpack Compose. The app explores a cleaner dark phone UI with calls, keypad, people, contact details, and a custom in-call screen.

## Features

- Dark, professional dialer UI inspired by modern phone-app concepts.
- Calls screen with All, Missed, and Received filters.
- People screen with saved contacts.
- Keypad screen with responsive layout for emulator and phone sizes.
- Contact detail bottom sheet when tapping a caller/contact.
- Custom in-call activity with mute, hold, speaker, keypad, record placeholder, add-call placeholder, and end-call action.
- Default dialer request flow.
- Custom adaptive launcher icon.

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android Telecom APIs
- Gradle Kotlin DSL

## Requirements

- Android Studio Ladybug or newer is recommended.
- Android SDK installed.
- Android emulator or physical Android phone.
- JDK configured by Android Studio.

This project includes the Gradle wrapper, so users do not need to install Gradle separately.

## Run In Android Studio

1. Clone the repo:

```bash
git clone <your-repo-url>
cd CallApp
```

2. Open the cloned `CallApp` folder in Android Studio.
3. Let Android Studio finish Gradle sync.
4. Start an emulator or connect an Android phone with USB debugging enabled.
5. Click Run.

If Android Studio asks to install missing SDK components, allow it.

## Run From Terminal

Build a debug APK:

```bash
./gradlew assembleDebug
```

The APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Install it on a connected emulator or phone:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Launch it:

```bash
adb shell am start -n com.example.callapp/.MainActivity
```

## Use On Your Phone

1. Build the debug APK using Android Studio or `./gradlew assembleDebug`.
2. Transfer `app-debug.apk` to your phone, or install using `adb install -r`.
3. Open the app.
4. Tap the default dialer prompt if shown.
5. In Android settings, choose CallApp as the default Phone app.

On most Android phones:

```text
Settings > Apps > Default apps > Phone app > CallApp
```

Some manufacturers place this under Apps > Manage apps > Default apps.

## Important Android Notes

- Google Phone is not open-source. This project does not modify Google Phone; it builds a replacement dialer app.
- Android only allows full dialer behavior after the user sets the app as the default Phone app.
- Call recording is heavily restricted by Android version, region, manufacturer, and Play Store policy. The current Record control is a UI placeholder.
- Some in-call features may behave differently on emulators compared with real phones.

## VS Code Workflow

You can edit the project in VS Code, but Android Studio is still useful for SDK setup, Gradle sync, emulator management, and Logcat.

Recommended workflow:

1. Open the same cloned folder in VS Code.
2. Edit Kotlin/XML files.
3. Save changes.
4. Use Android Studio or Gradle commands to build/run.

## What Not To Commit

Do not commit machine-specific files such as:

- `local.properties`
- `.gradle/`
- `.idea/workspace.xml`
- build outputs
- generated APK files

The included `.gitignore` already excludes these.

## Project Structure

```text
app/src/main/java/com/example/callapp/MainActivity.kt
app/src/main/java/com/example/callapp/InCallActivity.kt
app/src/main/java/com/example/callapp/MinimalInCallService.kt
app/src/main/java/com/example/callapp/MinimalCallScreeningService.kt
app/src/main/AndroidManifest.xml
app/src/main/res/drawable/ic_launcher_background.xml
app/src/main/res/drawable/ic_launcher_foreground.xml
```

## GitHub Setup

Initialize Git if needed:

```bash
git init
git add .
git commit -m "Initial CallApp dialer prototype"
```

Create an empty GitHub repository, then connect and push:

```bash
git remote add origin https://github.com/<your-username>/<repo-name>.git
git branch -M main
git push -u origin main
```
