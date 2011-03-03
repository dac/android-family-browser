package ca.chaves.androidApp.familyBrowser.activities;

import ca.chaves.androidApp.familyBrowser.R;
import ca.chaves.androidApp.familyBrowser.helpers.Log;
import ca.chaves.androidApp.familyBrowser.helpers.ViewFactory;
import ca.chaves.androidApp.familyBrowser.screens.FamilyController;
import ca.chaves.androidApp.familyBrowser.screens.FamilyScreenBuilder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * FamilyBrowser is the main Activity for the Family Browser application.
 * 
 * @author david@chaves.ca
 */
public class FamilyBrowser extends Activity {
    
    protected static final String TAG = FamilyBrowser.class.getSimpleName();
    
    /**
     * The controller for the Family Browser screen.
     * 
     * @see "http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller"
     */
    private FamilyController m_controller;
    
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.d(TAG, "{ onCreate");
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.family_screen);
        // setup the screen views
        this.buildActivityScreen();
        Log.d(TAG, "} onCreate");
    }
    
    /**
     * The activity comes to foreground.
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "{ onResume");
        super.onResume();
        Log.d(TAG, "} onResume");
    }
    
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        // save away the original data, so we still
        // have it if the activity needs to be killed while paused
    }
    
    /**
     * Called when the user is going somewhere, so make sure changes are saved.
     */
    @Override
    protected void onPause() {
        Log.d(TAG, "{ onPause");
        super.onPause();
        Log.d(TAG, "} onPause");
    }
    
    /**
     * Build the screen views - called from onCreate().
     */
    private void buildActivityScreen() {
        Log.d(TAG, "{ buildActivityScreen");
        
        // create the screen controller
        this.m_controller = new FamilyController(this);
        
        // create and populate data [in parallel]
        Log.d(TAG, "setup tabHost");
        final TabHost tabHost = (TabHost) super.findViewById(android.R.id.tabhost);
        tabHost.setup();
        
        // create helpers
        Log.d(TAG, "create helpers");
        final ViewFactory viewFactory = new ViewFactory(this);
        final FamilyScreenBuilder screenBuilder = new FamilyScreenBuilder(viewFactory, this.m_controller, tabHost);
        
        // create all screen views
        Log.d(TAG, "add tabs");
        tabHost.addTab(screenBuilder.createTabStart());
        tabHost.addTab(screenBuilder.createTabContent());
        tabHost.addTab(screenBuilder.createTabIndex());
        tabHost.setCurrentTab(1); // start at the "Content" tab
        
        // finally, update the user-interface from our buffers
        this.m_controller.updateUserInterface(tabHost);
        Log.d(TAG, "} buildActivityScreen");
    }
}
