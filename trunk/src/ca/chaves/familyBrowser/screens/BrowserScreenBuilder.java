package ca.chaves.familyBrowser.screens;

import ca.chaves.familyBrowser.R;
import ca.chaves.familyBrowser.helpers.Log;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * This class builds the main BrowserActivity screen views. This screen is made
 * of three panes: the 'start' pane, the 'content' pane, and the 'index' pane.
 * They are called TabStart, TabContent and TabIndex in the source code. The
 * TabStart is kind of a handy splash screen. We could use it to have a handy
 * tool-bar as well. The TabContent is where we display the current node content
 * (the current "node attributes"). The TabIndex is were we display the links to
 * other nodes, in order to facilitate navigation. The links in the TabIndex are
 * grouped in links to parents, links to partners, links to children, and so on.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class BrowserScreenBuilder {

    protected static final String TAG = BrowserScreenBuilder.class.getSimpleName();

    /** This is the screen controller */
    final BrowserScreenController m_screenController;
    /** This is the main view - this contains the full screen */
    final TabHost m_tabHost;
    /** This is the view factory - used to inflate Views from layouts */
    final ViewFactory m_viewFactory;

    /**
     * Constructor.
     *
     * @param viewFactory
     * @param controller
     * @param tabHost
     */
    public BrowserScreenBuilder(final ViewFactory viewFactory, final BrowserScreenController controller, final TabHost tabHost) {
        this.m_tabHost = tabHost;
        this.m_viewFactory = viewFactory;
        this.m_screenController = controller;
    }

    /**
     * Create the TabContent tab.
     *
     * @return TabSpec
     */
    public TabHost.TabSpec createTabContent() {
        return this.createViewTab("Content", this.createTabContentPane());
    }

    /**
     * Populate the TabContent pane.
     *
     * @return View
     */
    private View createTabContentPane() {
        Log.d(TAG, "{ createTabContentPane");
        final View layout = this.createViewLayout(R.layout.browser_tab_content);
        final TextView textView = (TextView) layout.findViewById(R.id.label);
        final ListView listView = (ListView) layout.findViewById(R.id.list);
        listView.setAdapter(new BrowserTabContentListAdapter(this.m_viewFactory, this.m_screenController));
        this.m_screenController.registerViews(textView, listView);
        Log.d(TAG, "} createTabContentPane");
        return layout;
    }

    /**
     * Create the TabIndex tab.
     *
     * @return TabSpec
     */
    public TabHost.TabSpec createTabIndex() {
        return this.createViewTab("Index", this.createTabIndexPane());
    }

    /**
     * Populate the TabIndex pane.
     *
     * @return View
     */
    private View createTabIndexPane() {
        Log.d(TAG, "{ createTabIndexPane");
        final View layout = this.createViewLayout(R.layout.browser_tab_index);
        final TextView textView = (TextView) layout.findViewById(R.id.label);
        final ExpandableListView listView = (ExpandableListView) layout.findViewById(R.id.list);
        listView.setAdapter(new BrowserTabIndexListAdapter(this.m_viewFactory, this.m_screenController));
        this.m_screenController.registerViews(textView, listView);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(final ExpandableListView parent, final View v, final int groupPosition,
                            final int childPosition, final long id) {
                BrowserScreenBuilder.this.m_screenController.onTabIndexChildClick(groupPosition, childPosition);
                return false;
            }
        });
        Log.d(TAG, "} createTabIndexPane");
        return layout;
    }

    /**
     * Create the TabStart tab.
     *
     * @return TabSpec
     */
    public TabHost.TabSpec createTabStart() {
        return this.createViewTab("Start", this.createTabStartPane());
    }

    /**
     * Populate the TabStart pane.
     *
     * @return View
     */
    private View createTabStartPane() {
        Log.d(TAG, "{ createTabStartPane");
        final View layout = this.createViewLayout(R.layout.browser_tab_start);
        Log.d(TAG, "} createTabStartPane");
        return layout;
    }

    /**
     * Utility function - it creates the tab indicator.
     */
    private View createViewIndicator(final String indicatorTitle) {
        // this uses the R.layout.tab_indicator from
        // @see http://code.google.com/p/iosched/
        final TextView view = (TextView) this.createViewLayout(R.layout.tab_indicator);
        view.setText(indicatorTitle);
        return view;
    }

    /**
     * Utility function - it creates a pane view.
     *
     * @param layoutResource
     * @return View
     */
    private View createViewLayout(final int layoutResource) {
        return this.m_viewFactory.createView(layoutResource, this.m_tabHost.getTabWidget());
    }

    /**
     * Utility function - it create a tab view.
     *
     * @param tabTitle
     * @param paneView
     * @return View
     */
    public TabHost.TabSpec createViewTab(final String tabTitle, final View paneView) {
        final TabHost.TabSpec tab = this.m_tabHost.newTabSpec(tabTitle);
        // setIndicator() is used to set name for the tab
        tab.setIndicator(this.createViewIndicator(tabTitle));
        // setContent() is used to set content for a particular tab
        tab.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(final String tag) {
                return paneView;
            }
        });
        return tab;
    }
}
