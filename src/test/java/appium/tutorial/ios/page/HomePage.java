package appium.tutorial.ios.page;

import io.appium.java_client.MobileBy;

import static appium.tutorial.ios.util.Helpers.element;

/** Page object for the home page **/
public abstract class HomePage {

    /** Verify the home page has loaded.
     *  Click the buttons button.
     *  Verify the buttons page has loaded. **/
    public static void buttonsClick() {
        loaded();
        element(MobileBy.AccessibilityId("Buttons")).click();
        ButtonsPage.loaded();
    }

    /** Verify the home page has loaded.
     *  Click the controls button.
     *  Verify the controls page has loaded. **/
    public static void controlsClick() {
        loaded();
        element(MobileBy.AccessibilityId("Controls")).click();
        ControlsPage.loaded();
    }

    /** Verify the home page has loaded **/
    public static void loaded() {
        element(MobileBy.AccessibilityId("UICatalog"));
    }
}