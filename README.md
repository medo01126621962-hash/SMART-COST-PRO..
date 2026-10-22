# SMART COST PRO — Native Android

This repository's application is the native Android project in `app/`, built with Kotlin, Jetpack Compose, Room, and Gradle.

## Open and build

1. Open the repository root in Android Studio (do not open `app/` as a separate project).
2. Allow Gradle sync to finish.
3. Select the `app` run configuration and run it on an Android device.

## Android application

- Gradle module: `:app`
- Application ID: `com.smartcostpro`
- Minimum Android version: Android 8.0 (API 26)
- UI: Kotlin + Jetpack Compose
- Local persistence: Room

The Gradle project is authoritative for Android builds. A React/Vite/npm build is not an Android APK build. Do not replace Android Gradle tasks with an `npmBuild` task.

## Important

This repository includes authentication and home-screen code, local data components, and calculation logic. A successful Gradle build confirms compilation only; verify sign-in, expense CRUD, persistence, and totals on a real device before treating those flows as complete.
