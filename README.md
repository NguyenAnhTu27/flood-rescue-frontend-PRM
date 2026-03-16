# Flood Rescue Mobile

Android native frontend scaffold for the Flood Rescue system, structured with `MVVM` so you can keep UI, business logic, and data access separated from the start.

## Tech stack

- Android Studio
- Java
- XML layouts
- MVVM
- Retrofit + OkHttp
- LiveData + ViewModel

## Project structure

```text
app/src/main/java/com/floodrescue/mobile/
|- core/
|  |- network/
|  |- util/
|- data/
|  |- local/
|  |- model/
|  |- repository/
|- ui/
   |- splash/
   |- auth/login/
   |- home/
```

## Current scaffold

- `SplashActivity`: decides whether the user should go to login or home
- `LoginActivity`: login UI wired to backend auth
- `HomeActivity`: placeholder dashboard screen for the mobile app
- `SessionManager`: stores JWT and basic user information
- `ApiClient` / `ApiService`: ready for backend integration

## Backend connection

The Android app currently points to:

```text
http://10.0.2.2:8080/
```

This is the correct base URL when:

- your backend runs on the same PC
- your Android app runs on an Android emulator

If you run on a physical phone, replace `BASE_URL` in `app/build.gradle` with your LAN IP, for example:

```text
http://192.168.1.10:8080/
```

## Open in Android Studio

1. Open the folder `flood-rescue-frontend-PRM`
2. Let Android Studio sync Gradle
3. Create or select an emulator
4. Run the `app` configuration

## Suggested next screens

1. Register
2. Forgot password
3. Citizen dashboard
4. Rescue request list/detail
5. Notifications
6. Rescue chat
