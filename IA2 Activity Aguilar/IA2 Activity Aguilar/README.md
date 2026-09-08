# IA2 Activity - Aguilar

Two projects:

- **LoginApp/** — Android Studio project. The app under test. One screen with
  Username, Password, and a Login button, nothing else. Valid credentials are
  hardcoded in `MainActivity.java` (`admin` / `Admin@123`) so the tests have
  something to check against. Wrong input shows a Toast message ("Invalid
  username", "Invalid password", or the empty-field message) — no extra UI
  elements were added just to keep it testable.

- **LoginAppTests/** — Java + Appium + JUnit automation project (Maven). Runs
  the three required test cases against the app.

## 1. Build the app

1. Open `LoginApp/` in Android Studio.
2. Let Gradle sync, then **Build > Build Bundle(s)/APK(s) > Build APK(s)**.
3. Note the path to the generated `app-debug.apk`
   (usually `LoginApp/app/build/outputs/apk/debug/app-debug.apk`).

## 2. Set up Appium

1. Install Node.js, then: `npm install -g appium`
2. Install the UiAutomator2 driver: `appium driver install uiautomator2`
3. Have an Android emulator running, or a device connected with USB
   debugging on (check with `adb devices`).
4. Start the server: `appium` (defaults to `http://127.0.0.1:4723/wd/hub`).

## 3. Point the tests at your APK

Open `LoginAppTests/src/test/java/tests/BaseTest.java` and replace
`PATH_TO_APP_DEBUG_APK` with the actual path to `app-debug.apk` from step 1,
e.g.:

```java
capabilities.setCapability(MobileCapabilityType.APP,
    "C:\\Users\\<you>\\Desktop\\IA2 Activity Aguilar\\LoginApp\\app\\build\\outputs\\apk\\debug\\app-debug.apk");
```

If your emulator/device name or Appium port is different, update
`DEVICE_NAME` and the server URL in the same file.

## 4. Run the tests

From inside `LoginAppTests/`:

```
mvn test
```

This runs all three cases in `LoginTest.java`:

| Test | Input | Expected result |
|---|---|---|
| `testCase1_CannotLogin_EmptyFields` | Username and password left blank | "Please enter both username and password" message, no login |
| `testCase2_WrongUsername` | Wrong username, correct password | "Invalid username" message, no login |
| `testCase3_WrongPassword` | Correct username, wrong password | "Invalid password" message, no login |
