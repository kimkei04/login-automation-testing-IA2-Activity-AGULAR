package tests;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class BaseTest {

    protected AndroidDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "emulator-5554");
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");

        // TODO: point this at the debug APK built from the LoginApp project,
        // e.g. "C:\\Users\\<you>\\Desktop\\IA2 Activity Aguilar\\LoginApp\\app\\build\\outputs\\apk\\debug\\app-debug.apk"
        capabilities.setCapability(MobileCapabilityType.APP, "PATH_TO_APP_DEBUG_APK");

        capabilities.setCapability(MobileCapabilityType.APP_PACKAGE, "com.aguilar.loginapp");
        capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, ".MainActivity");
        capabilities.setCapability("noReset", false);

        // Default Appium server address/port; update if yours differs.
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
