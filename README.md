# CallApp

CallApp is a custom Android dialer prototype built with **Kotlin** and **Jetpack Compose**. It includes a dark, modern phone UI with call history, filters, keypad, contacts, contact details, and a custom in-call screen.

## Preview

This project is designed as a replacement-style dialer UI prototype. To use real calling features, the app must be selected as the default Phone app on the Android device.

## Features

- Modern dark dialer interface
- Call list with `All`, `Missed`, and `Received` filters
- Responsive keypad screen
- People/contact list
- Contact detail bottom sheet
- Custom in-call screen
- Default dialer request support
- Custom adaptive launcher icon

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android Telecom APIs
- Gradle Kotlin DSL

## Requirements

Before running the project, install:

- Android Studio
- Android SDK
- Android Emulator, or a physical Android phone
- Git

You do **not** need to install Gradle separately. This project includes the Gradle wrapper.

## Clone The Project

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
cd YOUR_REPO_NAME
```

Replace `YOUR_USERNAME` and `YOUR_REPO_NAME` with the actual GitHub repo path.

## Run In Android Studio

1. Open Android Studio.
2. Click **Open**.
3. Select the cloned project folder.
4. Wait for Gradle sync to finish.
5. If Android Studio asks to install missing SDK components, click **Install**.
6. Start an emulator or connect your Android phone.
7. Click the green **Run** button.

## Run On An Emulator

1. Open Android Studio.
2. Go to **Device Manager**.
3. Create or start an emulator.
4. Select the emulator from the device dropdown.
5. Click **Run**.

## Run On A Physical Android Phone

1. Enable Developer Options on your phone.
2. Enable USB Debugging.
3. Connect the phone to your computer using USB.
4. Allow USB debugging permission on the phone.
5. Select your phone in Android Studio.
6. Click **Run**.

## Build APK

To build a debug APK from terminal:

```bash
./gradlew assembleDebug
```

The APK will be created here:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Install APK Manually

If you have `adb` configured:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Then launch the app from your phone or emulator.

## Set As Default Phone App

For full dialer behavior, set CallApp as the default Phone app.

On most Android phones:

```text
Settings > Apps > Default apps > Phone app > CallApp
```

On some phones, the path may be slightly different:

```text
Settings > Apps > Manage apps > Default apps > Phone app
```

## Important Notes

- This app does not modify Google Phone.
- Google Phone is not open source.
- CallApp is a separate custom dialer app.
- Android restricts full call control unless the app is selected as the default Phone app.
- Call recording is restricted by Android version, country, device manufacturer, and Play Store policy.
- Some calling features may work differently on emulator and real devices.

## Common Issues

### Gradle sync failed

Open Android Studio and let it install the missing SDK or build tools.

### `local.properties` missing

This is normal. Android Studio creates it automatically for each user.

Do not commit `local.properties` to GitHub because it contains your local Android SDK path.

### App installs but does not replace the phone UI

Set CallApp as the default Phone app in Android settings.

### Emulator does not show real call behavior

Some telecom features work better on a physical Android phone than on an emulator.

## Project Structure

```text
app/src/main/java/com/example/callapp/MainActivity.kt
app/src/main/java/com/example/callapp/InCallActivity.kt
app/src/main/java/com/example/callapp/MinimalInCallService.kt
app/src/main/java/com/example/callapp/MinimalCallScreeningService.kt
app/src/main/java/com/example/callapp/CallStateRepository.kt
app/src/main/AndroidManifest.xml
```

## Development Workflow

You can edit the code in Android Studio or VS Code.

Recommended workflow:

1. Open the same project folder in VS Code and Android Studio.
2. Edit code in VS Code if you prefer.
3. Use Android Studio to sync, run, debug, and view Logcat.

## License

This project is currently a prototype. Add a license before publishing it for public reuse.

