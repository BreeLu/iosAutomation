package appium.tutorial.ios.util;

import appium.tutorial.ios.page.HomePage;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.junit.SauceOnDemandTestWatcher;
import com.saucelabs.saucerest.SauceREST;
import io.appium.java_client.AppiumDriver;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AppiumTest implements SauceOnDemandSessionIdProvider {

    public static final String CAPABILITIES_APPIUM_VERSION = "appium-version";
    public static final String CAPABILITIES_PLATFORM_VERSION = "platformVersion";
    public static final String CAPABILITIES_PLATFORM_NAME = "platformName";
    public static final String CAPABILITIES_DEVICE_NAME = "deviceName";
    public static final String CAPABILITIES_JOB_NAME = "name";
    public static final String CAPABILITIES_APP = "app";
    public static final String DEFAULT_WD_HUB_URL = "http://127.0.0.1:4723/wd/hub";
    private AppiumDriver driver;

    /**
     * Page object references. Allows using 'home' instead of 'HomePage' *
     */
    protected HomePage home;

    /**
     * wait wraps Helpers.wait *
     */
    public static WebElement wait(By locator) {
        return Helpers.wait(locator);
    }

    private boolean runOnSauce = System.getProperty("sauce") != null;

    /**
     * Authenticate to Sauce with environment variables SAUCE_USER_NAME and SAUCE_API_KEY *
     */
    private SauceOnDemandAuthentication auth = new SauceOnDemandAuthentication();

    /**
     * Report pass/fail to Sauce Labs *
     */
    // false to silence Sauce connect messages.
    public
    @Rule
    SauceOnDemandTestWatcher reportToSauce = new SauceOnDemandTestWatcher(this, auth, false);

    private String sessionId;

    /**
     * Keep the same date prefix to identify job sets. *
     */
    private static final Date date = new Date();

    @Rule
    public TestRule printTests = new TestWatcher() {
        protected void starting(Description description) {
            System.out.print("  test: " + description.getMethodName());
        }

        protected void finished(Description description) {
            final String session = getSessionId();

            if (session != null) {
                System.out.println(" " + "https://saucelabs.com/tests/" + session);
            } else {
                System.out.println();
            }
        }
    };

    /**
     * Run before each test *
     */
    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CAPABILITIES_APPIUM_VERSION, "1.4.1");
        capabilities.setCapability(CAPABILITIES_PLATFORM_VERSION, "8.3");
        capabilities.setCapability(CAPABILITIES_PLATFORM_NAME, "ios");
        capabilities.setCapability(CAPABILITIES_DEVICE_NAME, "iPhone Simulator");

        // Set job name on Sauce Labs
        capabilities.setCapability(CAPABILITIES_JOB_NAME, "Java iOS tutorial " + date);
        String userDir = System.getProperty("user.dir");
        String localApp = "UICatalog6.1.app.zip";
        if (runOnSauce) {
            String user = auth.getUsername();
            String key = auth.getAccessKey();

            // Upload app to Sauce Labs
            SauceREST rest = new SauceREST(user, key);

            rest.uploadFile(new File(userDir, localApp), localApp);

            capabilities.setCapability(CAPABILITIES_APP, "sauce-storage:" + localApp);
            URL sauceURL = new URL("http://" + user + ":" + key + "@ondemand.saucelabs.com:80/wd/hub");
            driver = new AppiumDriver(sauceURL, capabilities);
        } else {
            String appPath = Paths.get(userDir, localApp).toAbsolutePath().toString();
            capabilities.setCapability(CAPABILITIES_APP, appPath);
            driver = new AppiumDriver(new URL(DEFAULT_WD_HUB_URL), capabilities);
        }

        sessionId = driver.getSessionId().toString();

        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        Helpers.init(driver);
    }

    /**
     * Run after each test *
     */
    @After
    public void tearDown() throws Exception {
        if (driver != null) driver.quit();
    }

    /**
     * If we're not on Sauce then return null otherwise SauceOnDemandTestWatcher will error. *
     */
    public String getSessionId() {
        return runOnSauce ? sessionId : null;
    }
}