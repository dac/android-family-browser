package ca.chaves.familyBrowser.test;

import junit.framework.Assert;
import ca.chaves.familyBrowser.activities.BrowserActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * Test class BrowserActivity.
 * 
 * @author "David Chaves <david@chaves.ca>"
 */
public class BrowserActivityTest extends ActivityInstrumentationTestCase2<BrowserActivity> {
    
    private BrowserActivity m_activity;
    private TabHost m_tabHost;
    
    @SuppressWarnings("unused")
    private View findViewById(final int id) {
        // find and return the requested View
        final FrameLayout layout = this.m_tabHost.getTabContentView();
        final View view = layout.findViewById(id);
        Assert.assertNotNull(view);
        return view;
    }
    
    public BrowserActivityTest() {
        super("ca.chaves.familyBrowser", BrowserActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.m_activity = getActivity();
        this.m_tabHost = (TabHost) this.m_activity.findViewById(android.R.id.tabhost);
    }
    
    public void test_not_nulls() throws Throwable {
        Assert.assertNotNull(this.m_activity);
        Assert.assertNotNull(this.m_tabHost);
    }
    
    public void test_tab_widget() throws Throwable {
        final TabWidget tabWidget = this.m_tabHost.getTabWidget();
        Assert.assertTrue(tabWidget.getTabCount() == 3);
    }
}
