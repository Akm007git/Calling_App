# CallApp

This is an Android app project built with Android Studio and compatible with VS Code when the Android development environment is configured.

## Overview

A standard Android app project structured with Gradle Kotlin DSL (`build.gradle.kts`) and an `app/` module.

## Requirements

- Android Studio installed
- Android SDK installed and configured
- Java JDK compatible with the Android Gradle plugin
- Emulator or physical Android device available
- Optional: VS Code with Android/Java/Kotlin extensions for editing

## How to use after cloning

1. Clone the repository:

```bash
git clone <repo-url>
cd CallApp
```

2. Open the project in Android Studio:

- Select `Open`, then choose the cloned `CallApp` folder.
- Let Gradle sync and build the project.

3. If `local.properties` is missing or invalid:

- Android Studio usually regenerates it automatically when the SDK path is configured.
- You can also create it manually with a line like:

```properties
sdk.dir=/Users/your-username/Library/Android/sdk
```

4. Run the app:

- Start an emulator or connect a device.
- Click the run button in Android Studio.

## Using VS Code

If you want to use VS Code instead of Android Studio:

- Open the cloned folder in VS Code.
- Install necessary Android and Kotlin extensions.
- Make sure the Android SDK and emulator are configured.
- Use Gradle tasks or an Android extension to build and run the app.

## Notes

- `local.properties` is usually not committed to GitHub because it contains a local SDK path.
- After cloning, the user must configure their own SDK path before running the app.
- The repo should be ready to build and run once the Android environment is set up correctly.

## Push to GitHub

- Add this `README.md` to the repository.
- Commit and push to GitHub.
- Anyone cloning the repo will then have this usage guide available.
