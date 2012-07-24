package ca.chaves.familyBrowser.test;

import ca.chaves.familyBrowser.app.browser.BrowserActivity;
import ca.chaves.familyBrowser.main.App;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

import junit.framework.Assert;

/**
 * Test for the {@link BrowserActivity} class.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class BrowserActivityTest
    extends ActivityInstrumentationTestCase2<BrowserActivity>
{
    private BrowserActivity activity;

    private transient TabHost tabHost;

    /**
     * Constructor.
     */
    public BrowserActivityTest()
    {
        super( App.PACKAGE_NAME, BrowserActivity.class );
    }

    // ----------
    // Unit Tests
    // ----------

    /**
     * Test function.
     */
    public void testNotNulls()
    {
        Assert.assertNotNull( activity );
        Assert.assertNotNull( tabHost );
    }

    /**
     * Test function.
     */
    public void testTabWidget()
    {
        final TabWidget tabWidget = tabHost.getTabWidget();
        Assert.assertTrue( tabWidget.getTabCount() == 3 );
    }

    // ------
    // Set Up
    // ------

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        activity = getActivity();
        tabHost = (TabHost) activity.findViewById( android.R.id.tabhost );
    }

    // -------
    // Helpers
    // -------

    @SuppressWarnings( "unused" )
    private View findViewById( final int viewId )
    {
        // find and return the requested View
        final FrameLayout layout = tabHost.getTabContentView();
        final View view = layout.findViewById( viewId );
        Assert.assertNotNull( view );
        return view;
    }
}
