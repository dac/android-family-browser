package ca.chaves.familyBrowser.screens;

import java.util.LinkedList;

import ca.chaves.familyBrowser.database.NodeController;
import ca.chaves.familyBrowser.database.NodeDatabase;
import ca.chaves.familyBrowser.helpers.Log;
import ca.chaves.familyBrowser.helpers.NodeValueList;
import ca.chaves.familyBrowser.helpers.NodeObject;

import android.app.Activity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class implements the controller for the BrowserActivity main screen. It
 * handles the user interaction and the database access.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class BrowserScreenController extends NodeController {

    /**
     * This is the main view to update. The value m_userView is set when the
     * screen is fully built, and it is used to force a redraw once the
     * user-interface data buffer changes.
     */
    private View m_userView;
    /**
     * This is the list of TextView to update when the user-interface data
     * buffer changes. These TextView(s) display the current NodeObject name.
     */
    private final LinkedList<TextView> m_textViews = new LinkedList<TextView>();
    /**
     * This is the list of ListView to update when the user-interface data
     * buffer changes. These ListView(s) display the node attributes or the node
     * parents/partners/children/siblings. We use customized ListAdapter(s) to
     * fetch the data and create the element views for them.
     */
    private final LinkedList<ListView> m_listViews = new LinkedList<ListView>();

    /**
     * Constructor
     *
     * @param activity
     */
    public BrowserScreenController(final Activity activity) {
        super(NodeDatabase.getDatabaseFactory(activity));
    }

    /**
     * Get the ValueList used for the node attributes.
     *
     * @return the node's value list
     */
    public NodeValueList getTabContentValues() {
        return super.getUserNode().getNodeListValues(NodeObject.LIST_ATTRIBUTES);
    }

    /**
     * Get the ValueList used for the node parents/partners/children/siblings
     * list.
     *
     * @param groupPosition
     *            identifies the list to work on; for example, 0 is the list of
     *            parents. These values are defined by the constants
     *            NodeObject.LIST_PARENTS and up. This value must be between 0
     *            and (getTabIndexGroupCount() - 1).
     * @return the node's list
     */
    public NodeValueList getTabIndexGroupValues(final int groupPosition) {
        return super.getUserNode().getNodeListValues(NodeObject.LIST_PARENTS + groupPosition);
    }

    /**
     * Get the number of groups in the TabIndex pane. We have one group for the
     * node parents; another for the node partners; other for the node children;
     * and so on.
     *
     * @return number of groups
     */
    public int getTabIndexGroupCount() {
        return NodeObject.LIST_MAX - NodeObject.LIST_PARENTS;
    }

    /**
     * This function in called when the user clicks on a group item ("child") at
     * the TabIndex. Please remember that the groups in TabIndex are the list of
     * parents, partners, etc. Therefore, this function is called when the user
     * clicks on a specific parent, or partner, etc. In any case, we start
     * loading a new node in background.
     *
     * @param groupPosition
     *            identifies the list to work on; for example, 0 is the list of
     *            parents. These values are defined by the constants
     *            NodeObject.LIST_PARENTS and up. This value must be between 0
     *            and (getTabIndexGroupCount() - 1).
     * @param childPosition
     *            identifies the item/child element inside the groupPosition
     *            group
     */
    public void onTabIndexChildClick(final int groupPosition, final int childPosition) {
        Log.d(TAG, "click on TabIndex child", groupPosition, childPosition);
        final NodeValueList list = this.getTabIndexGroupValues(groupPosition);
        final Integer id = list.getId(childPosition);
        final String name = list.getString(childPosition);
        super.startLoadingNode(id, name);
    }

    /**
     * Register which TextView(s) and ListView(s) need to be updated when the
     * user-interface data buffer changes.
     *
     * @param textView
     * @param listView
     */
    public void registerViews(final TextView textView, final ListView listView) {
        if (textView != null) {
            // it does not really matter if we load it first or last in the list
            this.m_textViews.addFirst(textView);
        }
        if (listView != null) {
            // it does not really matter if we load it first or last in the list
            this.m_listViews.addFirst(listView);
        }
    }

    /**
     * This function is called when the main screen is finally built. We need to
     * update the user-interface views, just in case the database access
     * operation had finish at this point and the user-interface buffer is
     * properly updated but not displayed yet on the screen.
     *
     * @param userView
     */
    public void updateUserInterface(final View userView) {
        this.m_userView = userView;
        // nice to have: refresh the screen *only if* the user-interface data
        // buffer is not empty!
        super.refreshUserInterface();
    }

    /**
     * Call-back. This function is called when a new user-interface data buffer
     * is ready to display, with data loaded from the database. This function
     * forces a full redraw of the main screen.
     */
    @Override
    protected void onLoadCompleteRefreshUserInterface(final NodeObject node) {
        // do nothing if there is no userView defined yet
        if (this.m_userView == null) {
            return;
        }
        // which title should we use?
        String titleValue = node.getNodeName();
        if (titleValue == null) {
            titleValue = ""; // will use the android:hint property
        }
        // update all textViews
        for (TextView textView : this.m_textViews) {
            textView.setText(titleValue);
            // textView.invalidate();
        }
        // update all listViews
        for (ListView listView : this.m_listViews) {
            // we do not need to update any listView content because they
            // were built using special ListAdators, which will use this
            // BrowserScreenController to populate them
            invalidateDataSet(listView);
        }
        // update the grand-parent view, which will trigger to update everything
        this.m_userView.invalidate();
    }

    /**
     * Utility function to invalidate ListView(s). This function is needed
     * because BaseExpandableListAdapter and BaseAdapter do not have a common
     * base class that defines the 'invalidate' operation.
     *
     * @param listView
     */
    private static void invalidateDataSet(final ListView listView) {
        // for some unknown reason, .notifyDataSetInvalidated() is not in
        // shared interface between BaseExpandableListAdapter and BaseAdapter
        if (listView instanceof ExpandableListView) {
            final ExpandableListView view = (ExpandableListView) listView;
            final BaseExpandableListAdapter adapter = (BaseExpandableListAdapter) view.getExpandableListAdapter();
            adapter.notifyDataSetInvalidated();
        }
        else {
            final BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
            adapter.notifyDataSetInvalidated();
        }
    }
}
