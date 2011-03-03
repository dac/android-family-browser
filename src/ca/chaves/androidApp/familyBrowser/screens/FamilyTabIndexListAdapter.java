package ca.chaves.androidApp.familyBrowser.screens;

import ca.chaves.androidApp.familyBrowser.R;
import ca.chaves.androidApp.familyBrowser.helpers.NodeValueList;
import ca.chaves.androidApp.familyBrowser.helpers.ViewFactory;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

/**
 * The class FamilyTabIndexListAdapter is a ExpandableListAdapter used to
 * populate the TabIndex tab
 * 
 * @author david@chaves.ca
 */
class FamilyTabIndexListAdapter extends BaseExpandableListAdapter {
    
    /** View Factory */
    private final ViewFactory m_viewFactory;
    /** Screen Controller */
    private final FamilyController m_controller;
    
    /**
     * Constructor.
     * 
     * @param viewFactory
     * @param controller
     */
    public FamilyTabIndexListAdapter(final ViewFactory viewFactory, final FamilyController controller) {
        this.m_viewFactory = viewFactory;
        this.m_controller = controller;
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
     * Implement the ExpandableListAdapter interface. Gets the data associated
     * with the given child within the given group.
     */
    @Override
    public Object getChild(final int groupPosition, final int childPosition) {
        return null;
    }
    
    /**
     * Implement the ExpandableListAdapter interface. Gets the ID for the given
     * child within the given group.
     */
    @Override
    public long getChildId(final int groupPosition, final int childPosition) {
        return childPosition;
    }
    
    /**
     * Implement the ExpandableListAdapter interface. Gets the number of
     * children in a specified group.
     */
    @Override
    public int getChildrenCount(final int groupPosition) {
        final NodeValueList list = this.m_controller.getTabIndexGroupValues(groupPosition);
        return list.getLength();
    }
    
    /**
     * Implement the ExpandableListAdapter interface. Gets a View that displays
     * the data for the given child within the given group.
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, final View convertView,
                    final ViewGroup parentView) {
        final NodeValueList list = this.m_controller.getTabIndexGroupValues(groupPosition);
        final String text = list.getStringValue(childPosition);
        return this.createView(text, null, R.layout.family_index_child, parentView);
    }
    
    /**
     * Implement the ExpandableListAdapter interface. Gets the data associated
     * with the given group.
     */
    @Override
    public Object getGroup(final int groupPosition) {
        return null;
    }
    
    /**
     * Implement the ExpandableListAdapter interface. Gets the number of groups.
     */
    @Override
    public int getGroupCount() {
        return this.m_controller.getTabIndexGroupCount();
    }
    
    /**
     * Implement the ExpandableListAdapter interface. Gets the ID for the group
     * at the given position.
     */
    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }
    
    /**
     * Implement the ExpandableListAdapter interface. Gets a View that displays
     * the given group.
     */
    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, final View convertView, final ViewGroup parentView) {
        final NodeValueList list = this.m_controller.getTabIndexGroupValues(groupPosition);
        final String text = list.getListName();
        final String text2 = Integer.toString(list.getLength());
        return this.createView(text, text2, R.layout.family_index_group, parentView);
    }
    
    /**
     * Implement the ExpandableListAdapter interface. Indicates whether the
     * child and group IDs are stable across changes to the underlying data.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }
    
    /**
     * Implement the ExpandableListAdapter interface. Whether the child at the
     * specified position is selectable.
     */
    @Override
    public boolean isChildSelectable(final int groupPosition, final int childPosition) {
        return true;
    }
}
