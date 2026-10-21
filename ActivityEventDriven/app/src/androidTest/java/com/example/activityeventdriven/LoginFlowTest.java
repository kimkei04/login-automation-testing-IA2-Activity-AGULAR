package com.example.activityeventdriven;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertTrue;

import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.regex.Pattern;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginFlowTest {

    // Opens MainActivity (the Login screen) fresh before every test
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    // ---------- Test data ----------
    private static final String VALID_EMAIL = "juan.delacruz@example.com";
    private static final String VALID_PASSWORD = "Passw0rd!23";
    private static final String UNKNOWN_EMAIL = "nobody.test@example.com";
    private static final String WRONG_PASSWORD = "WrongPass1";

    // Render can take a while to wake up, so wait up to 90 seconds
    private static final long TIMEOUT_MS = 90_000;

    // ================= SCENARIO 1: INVALID LOGIN =================

    @Test
    public void scenario1a_invalidLogin_unregisteredUser() {
        log("SCENARIO 1a: INVALID LOGIN (UNREGISTERED USER)");

        doLogin(UNKNOWN_EMAIL, WRONG_PASSWORD);

        log("Waiting for the 'Login Unsuccessful' popup");
        waitForDialog("Login Unsuccessful");

        String message = dialogMessage();
        log("Popup message: " + message);
        assertTrue("Expected 'does not exist' but got: " + message,
                message.contains("does not exist"));

        tapDialogButton();

        // The user must NOT get past the login page
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        onView(withId(R.id.et_otp)).check(doesNotExist());
        log("PASSED - unregistered user was blocked");
    }

    @Test
    public void scenario1b_invalidLogin_wrongPassword() {
        log("SCENARIO 1b: INVALID LOGIN (WRONG PASSWORD)");

        doLogin(VALID_EMAIL, WRONG_PASSWORD);

        log("Waiting for the 'Login Unsuccessful' popup");
        waitForDialog("Login Unsuccessful");

        String message = dialogMessage();
        log("Popup message: " + message);
        assertTrue("Expected 'Incorrect password' but got: " + message,
                message.contains("Incorrect password"));

        tapDialogButton();

        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        onView(withId(R.id.et_otp)).check(doesNotExist());
        log("PASSED - wrong password was blocked");
    }

    // ================= SCENARIO 2: VALID LOGIN =================

    @Test
    public void scenario2_validLogin_otp_andUserList() {
        log("SCENARIO 2: VALID LOGIN");

        // ---- 1. Login ----
        doLogin(VALID_EMAIL, VALID_PASSWORD);

        log("Waiting for the 'Login Successful' popup (Render may be slow)");
        waitForDialog("Login Successful");
        String welcome = dialogMessage();
        log("Popup message: " + welcome);
        assertTrue("Expected a welcome message for Juan", welcome.contains("Welcome, Juan"));

        log("Tapping Continue");
        tapDialogButton();

        // ---- 2. OTP page: read the OTP from the popup ----
        log("Waiting for the OTP popup");
        waitForDialog("Your OTP");
        String otpMessage = dialogMessage();
        log("Popup message: " + otpMessage.replace("\n", " "));

        java.util.regex.Matcher m = Pattern.compile("is: (\\d{4})").matcher(otpMessage);
        assertTrue("Could not find a 4-digit OTP in: " + otpMessage, m.find());
        String otp = m.group(1);
        log("OTP read by the script: " + otp);

        tapDialogButton();

        // ---- 3. Type the OTP (the OTP lasts 30 seconds) ----
        log("Typing the OTP: " + otp);
        onView(withId(R.id.et_otp)).perform(replaceText(otp), closeSoftKeyboard());
        log("Tapping Verify");
        onView(withId(R.id.btn_verify)).perform(click());

        log("Waiting for the 'Verification Successful' popup");
        waitForDialog("Verification Successful");
        tapDialogButton();

        // ---- 4. Dashboard ----
        log("Checking the Dashboard");
        waitForView(withId(R.id.tv_dashboard_title));
        onView(withId(R.id.tv_dashboard_title)).check(matches(withText("Dashboard")));
        onView(withId(R.id.btn_customers)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_users)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_logout)).check(matches(isDisplayed()));

        // ---- 5. Users page ----
        log("Opening the Users page");
        onView(withId(R.id.btn_users)).perform(click());
        waitForView(withId(R.id.tv_users_title));
        onView(withId(R.id.tv_users_title)).check(matches(withText("Users")));

        log("Waiting for the user list to load from Render");
        waitForView(allOf(withId(R.id.tv_api_header), withSubstring("Render API users:")));

        onView(allOf(withText(VALID_EMAIL), isDescendantOfA(withId(R.id.lv_api_users))))
                .check(matches(isDisplayed()));

        log("Tapping Back to Dashboard");
        onView(withId(R.id.btn_back_dashboard)).perform(click());
        waitForView(withId(R.id.tv_dashboard_title));

        // ---- 6. Logout (from the Dashboard) ----
        log("Tapping Logout");
        onView(withId(R.id.btn_logout)).perform(click());
        waitForView(withId(R.id.btn_login));
        log("PASSED - valid login reached the Dashboard and the Users page");
    }

    // ================= SCENARIO 3: OTP TESTS =================

    // 3a. A wrong OTP must be rejected
    @Test
    public void scenario3a_otp_wrongOtp() {
        log("SCENARIO 3a: OTP TEST (WRONG OTP)");

        String otp = reachOtpPage();
        String wrongOtp = differentOtp(otp);

        log("Typing a WRONG OTP: " + wrongOtp);
        enterOtpAndVerify(wrongOtp);

        log("Waiting for the 'Verification Unsuccessful' popup");
        waitForDialog("Verification Unsuccessful");
        String message = dialogMessage();
        log("Popup message: " + message);
        assertTrue("Expected 'Wrong OTP' but got: " + message, message.contains("Wrong OTP"));

        tapDialogButton();

        // Still on the OTP page, and the Users page must NOT open
        onView(withId(R.id.et_otp)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_users_title)).check(doesNotExist());
        log("PASSED - wrong OTP was rejected");

        leaveOtpPage();
    }

    // 3b. An incomplete OTP (less than 4 digits) must show a validation error
    @Test
    public void scenario3b_otp_incompleteOtp() {
        log("SCENARIO 3b: OTP TEST (INCOMPLETE OTP)");

        reachOtpPage();

        log("Typing only 2 digits: 12");
        enterOtpAndVerify("12");

        onView(withId(R.id.et_otp)).check(matches(hasErrorText("Enter the 4-digit OTP")));
        onView(withId(R.id.tv_users_title)).check(doesNotExist());
        log("PASSED - incomplete OTP showed the validation error");

        leaveOtpPage();
    }

    // 3c. The OTP must stop working after 30 seconds
    @Test
    public void scenario3c_otp_expiredOtp() {
        log("SCENARIO 3c: OTP TEST (EXPIRED OTP)");

        String otp = reachOtpPage();

        log("Waiting for the 30-second countdown to finish (about 30 seconds)");
        waitForView(allOf(withId(R.id.tv_timer), withText("OTP expired. Tap Resend OTP.")));
        SystemClock.sleep(1000);

        log("Typing the real OTP, but it is now expired: " + otp);
        enterOtpAndVerify(otp);

        log("Waiting for the 'OTP Expired' popup");
        waitForDialog("OTP Expired");
        String message = dialogMessage();
        log("Popup message: " + message);
        assertTrue("Expected the 30 seconds message but got: " + message,
                message.contains("30 seconds"));

        tapDialogButton();

        onView(withId(R.id.tv_users_title)).check(doesNotExist());
        log("PASSED - expired OTP was rejected");

        leaveOtpPage();
    }

    // 3d. Resend OTP must give a new code: the old one fails, the new one works
    @Test
    public void scenario3d_otp_resendOtp() {
        log("SCENARIO 3d: OTP TEST (RESEND OTP)");

        String oldOtp = reachOtpPage();

        log("Tapping Resend OTP");
        onView(withId(R.id.tv_resend)).perform(click());

        waitForDialog("Your OTP");
        String newOtp = readOtpFromDialog();
        log("New OTP read by the script: " + newOtp);
        tapDialogButton();

        // The old code must be rejected (skipped in the rare case both codes are the same)
        if (!oldOtp.equals(newOtp)) {
            log("Typing the OLD OTP: " + oldOtp);
            enterOtpAndVerify(oldOtp);
            waitForDialog("Verification Unsuccessful");
            tapDialogButton();
            log("Old OTP was rejected");
        }

        // The new code must work
        log("Typing the NEW OTP: " + newOtp);
        enterOtpAndVerify(newOtp);

        log("Waiting for the 'Verification Successful' popup");
        waitForDialog("Verification Successful");
        tapDialogButton();

        waitForView(withId(R.id.tv_dashboard_title));
        onView(withId(R.id.tv_dashboard_title)).check(matches(withText("Dashboard")));
        log("PASSED - new OTP worked after Resend");

        onView(withId(R.id.btn_logout)).perform(click());
        waitForView(withId(R.id.btn_login));
    }

    // ================= SCENARIO 4: DASHBOARD + CUSTOMERS (NEW) =================

    // 4a. The Customers page opens from the Dashboard and loads the Supabase list
    @Test
    public void scenario4a_dashboard_customerList() {
        log("SCENARIO 4a: CUSTOMER LIST");

        loginToDashboard();

        log("Opening the Customers page");
        onView(withId(R.id.btn_customers)).perform(click());
        waitForView(withId(R.id.tv_customers_title));
        onView(withId(R.id.tv_customers_title)).check(matches(withText("Customers")));

        log("Waiting for the customer list to load from Supabase");
        waitForView(allOf(withId(R.id.tv_customer_header), withSubstring("Customers:")));

        onView(withId(R.id.btn_customers_back)).perform(click());
        waitForView(withId(R.id.tv_dashboard_title));
        onView(withId(R.id.btn_logout)).perform(click());
        waitForView(withId(R.id.btn_login));
        log("PASSED - customer list loaded");
    }

    // 4b. The Add Customer form must complain when a field is empty (nothing is sent)
    @Test
    public void scenario4b_addCustomer_validation() {
        log("SCENARIO 4b: ADD CUSTOMER VALIDATION");

        loginToDashboard();
        onView(withId(R.id.btn_customers)).perform(click());
        waitForView(withId(R.id.btn_add_customer));

        log("Opening the Add Customer form");
        onView(withId(R.id.btn_add_customer)).perform(click());
        waitForDialog("Add Customer");

        log("Tapping Save with every field empty");
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());
        onView(withId(R.id.et_customer_id)).inRoot(isDialog())
                .check(matches(hasErrorText("Customer ID is required")));
        log("PASSED - empty form was blocked");

        pressBack();   // closes the form
        waitForView(withId(R.id.btn_customers_back));
        onView(withId(R.id.btn_customers_back)).perform(click());
        waitForView(withId(R.id.tv_dashboard_title));
        onView(withId(R.id.btn_logout)).perform(click());
        waitForView(withId(R.id.btn_login));
    }

    // ================= HELPER ACTIONS =================

    // NEW: logs in with the valid user, passes the OTP, and waits on the Dashboard
    private void loginToDashboard() {
        String otp = reachOtpPage();
        enterOtpAndVerify(otp);
        waitForDialog("Verification Successful");
        tapDialogButton();
        waitForView(withId(R.id.tv_dashboard_title));
    }

    // Types the email and password, then taps Login
    private void doLogin(String email, String password) {
        log("Typing email: " + email);
        onView(withId(R.id.et_email)).perform(replaceText(email), closeSoftKeyboard());
        log("Typing password");
        onView(withId(R.id.et_password)).perform(replaceText(password), closeSoftKeyboard());
        log("Tapping the Login button");
        onView(withId(R.id.btn_login)).perform(click());
    }

    // Logs in with the valid user and gets to the OTP page; returns the OTP shown in the popup
    private String reachOtpPage() {
        doLogin(VALID_EMAIL, VALID_PASSWORD);

        log("Waiting for the 'Login Successful' popup");
        waitForDialog("Login Successful");
        tapDialogButton();

        log("Waiting for the OTP popup");
        waitForDialog("Your OTP");
        String otp = readOtpFromDialog();
        log("OTP read by the script: " + otp);
        tapDialogButton();
        return otp;
    }

    // Reads the 4-digit OTP from the popup message
    private String readOtpFromDialog() {
        String otpMessage = dialogMessage();
        log("Popup message: " + otpMessage.replace("\n", " "));
        java.util.regex.Matcher m = Pattern.compile("is: (\\d{4})").matcher(otpMessage);
        assertTrue("Could not find a 4-digit OTP in: " + otpMessage, m.find());
        return m.group(1);
    }

    // Types an OTP into the OTP box and taps Verify
    private void enterOtpAndVerify(String otp) {
        onView(withId(R.id.et_otp)).perform(replaceText(otp), closeSoftKeyboard());
        onView(withId(R.id.btn_verify)).perform(click());
    }

    // Makes a 4-digit code that is different from the real OTP
    private String differentOtp(String otp) {
        return otp.equals("1234") ? "4321" : "1234";
    }

    // Goes back from the OTP page to the Login page
    private void leaveOtpPage() {
        pressBack();
        waitForView(withId(R.id.btn_login));
    }

    // Waits until a popup with this title is on screen
    private void waitForDialog(String title) {
        retryUntilTimeout(() ->
                onView(withText(title)).inRoot(isDialog()).check(matches(isDisplayed())));
    }

    // Waits until a view is on screen
    private void waitForView(Matcher<View> matcher) {
        retryUntilTimeout(() -> onView(matcher).check(matches(isDisplayed())));
    }

    // Reads the text of the popup message
    private String dialogMessage() {
        final String[] holder = {""};
        ViewInteraction message = onView(withId(android.R.id.message)).inRoot(isDialog());
        message.perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "read the popup message";
            }

            @Override
            public void perform(UiController uiController, View view) {
                holder[0] = ((TextView) view).getText().toString();
            }
        });
        return holder[0];
    }

    // Taps the popup's main button (OK / Continue)
    private void tapDialogButton() {
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());
    }

    // Tries a check again and again until it passes or the timeout is reached
    private void retryUntilTimeout(Runnable check) {
        long end = SystemClock.uptimeMillis() + TIMEOUT_MS;
        Throwable last = null;
        while (SystemClock.uptimeMillis() < end) {
            try {
                check.run();
                return;
            } catch (Throwable t) {
                last = t;
                SystemClock.sleep(500);
            }
        }
        throw new AssertionError("Timed out after " + (TIMEOUT_MS / 1000) + "s. Last error: " + last, last);
    }

    // Prints a step to Logcat and pauses briefly so you can watch it
    private void log(String message) {
        android.util.Log.d("LoginFlowTest", message);
        SystemClock.sleep(600);
    }
}