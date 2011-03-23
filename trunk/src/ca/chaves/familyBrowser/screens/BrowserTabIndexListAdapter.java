package ca.chaves.familyBrowser.screens;

import ca.chaves.familyBrowser.R;
import ca.chaves.familyBrowser.helpers.NodeValueList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

/**
 * This class is a ExpandableListAdapter used to populate the TabIndex tab.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class BrowserTabIndexListAdapter extends BaseExpandableListAdapter {

    /** This is the view factory */
    private final ViewFactory m_viewFactory;
    /** This is the screen controller */
    private final BrowserScreenController m_screenController;

    /**
     * Constructor.
     *
     * @param viewFactory
     * @param controller
     */
    public BrowserTabIndexListAdapter(final ViewFactory viewFactory, final BrowserScreenController controller) {
        this.m_viewFactory = viewFactory;
        this.m_screenController = controller;
    }

    /**
     * Create view from the layout.
     *
     * @param text
     * @param text2
     * @param layoutResource
     * @param parentView
     * @return View
     */
    private View createView(final String text, final String text2, final int layoutResource, final ViewGroup parentView) {
        return this.m_viewFactory.createTextViews(text, R.id.label, text2, R.id.label2, layoutResource, parentView);
    }

    /**
     * Gets the data associated with the given child within the given group. It
     * implements the ExpandableListAdapter interface.
     */
    @Override
    public Object getChild(final int groupPosition, final int childPosition) {
        return null;
    }

    /**
     * Gets the ID for the given child within the given group. It implements the
     * ExpandableListAdapter interface.
     */
    @Override
    public long getChildId(final int groupPosition, final int childPosition) {
        return childPosition;
    }

    /**
     * Gets the number of children in a specified group. It implements the
     * ExpandableListAdapter interface.
     */
    @Override
    public int getChildrenCount(final int groupPosition) {
        final NodeValueList list = this.m_screenController.getTabIndexGroupValues(groupPosition);
        return list.getLength();
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group. It implements the ExpandableListAdapter interface.
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, final View convertView,
                    final ViewGroup parentView) {
        final NodeValueList list = this.m_screenController.getTabIndexGroupValues(groupPosition);
        final String text = list.getString(childPosition);
        return this.createView(text, null, R.layout.browser_tab_index_child, parentView);
    }

    /**
     * Gets the data associated with the given group. It implements the
     * ExpandableListAdapter interface.
     */
    @Override
    public Object getGroup(final int groupPosition) {
        return null;
    }

    /**
     * Gets the number of groups. It implements the ExpandableListAdapter
     * interface.
     */
    @Override
    public int getGroupCount() {
        return this.m_screenController.getTabIndexGroupCount();
    }

    /**
     * Gets the ID for the group at the given position. It implements the
     * ExpandableListAdapter interface.
     */
    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    /**
     * Gets a View that displays the given group. It implements the
     * ExpandableListAdapter interface.
     */
    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, final View convertView, final ViewGroup parentView) {
        final NodeValueList list = this.m_screenController.getTabIndexGroupValues(groupPosition);
        final String text = list.getName();
        final String text2 = Integer.toString(list.getLength());
        return this.createView(text, text2, R.layout.browser_tab_index_group, parentView);
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to
     * the underlying data. It implements the ExpandableListAdapter interface.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Whether the child at the specified position is selectable. It implements
     * the ExpandableListAdapter interface.
     */
    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }
}
