package ca.chaves.familyBrowser.activities;

import ca.chaves.familyBrowser.R;
import ca.chaves.familyBrowser.helpers.Log;
import ca.chaves.familyBrowser.screens.BrowserScreenController;
import ca.chaves.familyBrowser.screens.BrowserScreenBuilder;
import ca.chaves.familyBrowser.screens.ViewFactory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * This class is the main Activity for the Family Browser application. It
 * implements the database Browser screen.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class BrowserActivity extends Activity {

    protected static final String TAG = BrowserActivity.class.getSimpleName();

    /**
     * This is the controller for the Family Browser screen.
     *
     * @see "http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller"
     */
    private BrowserScreenController m_controller;

    /**
     * This function is called when the activity is being created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "{ onCreate");
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.browser_screen);
        // setup the screen views
        this.createScreenViews();
        Log.d(TAG, "} onCreate");
    }

    /**
     * This function is called when the activity comes to foreground.
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "{ onResume");
        super.onResume();
        Log.d(TAG, "} onResume");
    }

    /**
     * This function is called to save away the original data, so we still have
     * it if the activity needs to be killed while paused.
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        // nothing to do here [yet]
    }

    /**
     * This function is called when the user is going somewhere, so make sure
     * changes are saved.
     */
    @Override
    protected void onPause() {
        Log.d(TAG, "{ onPause");
        super.onPause();
        Log.d(TAG, "} onPause");
    }

    /**
     * This function builds the screen views - it is called from onCreate().
     */
    private void createScreenViews() {
        Log.d(TAG, "{ createScreenViews");

        // create the screen controller and populate data [in parallel]
        this.m_controller = new BrowserScreenController(this);

        // create the main TabHost view
        Log.d(TAG, "setup tabHost");
        final TabHost tabHost = (TabHost) super.findViewById(android.R.id.tabhost);
        tabHost.setup();

        // create helpers
        Log.d(TAG, "create helpers");
        final ViewFactory viewFactory = new ViewFactory(this);
        final BrowserScreenBuilder screenBuilder = new BrowserScreenBuilder(viewFactory, this.m_controller, tabHost);

        // create all screen views
        Log.d(TAG, "add tabs");
        tabHost.addTab(screenBuilder.createTabStart());
        tabHost.addTab(screenBuilder.createTabContent());
        tabHost.addTab(screenBuilder.createTabIndex());
        tabHost.setCurrentTab(2); // start at the "Index" tab

        // finally, update the user-interface from our buffers
        this.m_controller.updateUserInterface(tabHost);
        Log.d(TAG, "} createScreenViews");
    }
}
