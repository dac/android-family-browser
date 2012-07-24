package ca.chaves.familyBrowser.test;

import ca.chaves.android.app.Android;
import ca.chaves.familyBrowser.app.setup.SetupActivity;
import ca.chaves.familyBrowser.main.App;

import android.test.ActivityInstrumentationTestCase2;

import junit.framework.Assert;

/**
 * Test for the {@link App} class.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class MainAppTest
    extends ActivityInstrumentationTestCase2<SetupActivity>
{
    /**
     * Constructor.
     */
    public MainAppTest()
    {
        super( App.PACKAGE_NAME, SetupActivity.class );
    }

    // ----------
    // Unit Tests
    // ----------

    /**
     * Test function.
     */
    public void testNotNullInstance()
    {
        Assert.assertNotNull( Android.App.INSTANCE );
    }

    /**
     * Test function.
     */
    public void testNotNullDataDirectory()
    {
        Assert.assertNotNull( Android.App.DATA_DIRECTORY_NAME );
    }

    /**
     * Test function.
     */
    public void testNotNullMainActivityClass()
    {
        Assert.assertNotNull( Android.App.MAIN_ACTIVITY_CLASS );
    }

    /**
     * Test function.
     */
    public void testNotNullAboutActivityClass()
    {
        Assert.assertNotNull( Android.App.ABOUT_ACTIVITY_CLASS );
    }
}
