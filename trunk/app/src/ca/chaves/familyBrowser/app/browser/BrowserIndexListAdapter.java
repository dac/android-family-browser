package ca.chaves.familyBrowser.app.browser;

import ca.chaves.android.graph.GraphNode;
import ca.chaves.android.util.PairList;
import ca.chaves.android.widget.WidgetUtils;
import ca.chaves.familyBrowser.app.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

/**
 * ExpandableListAdapter to populate the "Index" tab.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
class BrowserIndexListAdapter
    extends BaseExpandableListAdapter
{
    /**
     * Create view from the layout.
     *
     * @param text
     * @param text2
     * @param layoutResourceId
     * @param parentView
     * @return View
     */
    private View createView( final String text, final String text2, final int layoutResourceId,
                             final ViewGroup parentView )
    {
        return WidgetUtils.createTextViews( text, R.id.label, text2, R.id.label2, layoutResourceId, parentView );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getChild( final int groupPosition, final int childPosition )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getChildId( final int groupPosition, final int childPosition )
    {
        return childPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChildrenCount( final int groupPosition )
    {
        return BrowserController.graphNode.values[( groupPosition + GraphNode.INDEX_EXTRA_VALUES )].length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getChildView( final int groupPosition, final int childPosition, final boolean isLastChild,
                              final View convertView, final ViewGroup parentView )
    {
        final PairList<Integer, String> list =
            BrowserController.graphNode.values[( groupPosition + GraphNode.INDEX_EXTRA_VALUES )];
        final String text = list.array_1[childPosition];
        return createView( text, null, R.layout.browser_index_child, parentView );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getGroup( final int groupPosition )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGroupCount()
    {
        return BrowserController.graphNode.values.length - GraphNode.INDEX_EXTRA_VALUES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getGroupId( final int groupPosition )
    {
        return groupPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getGroupView( final int groupPosition, final boolean isExpanded, final View convertView,
                              final ViewGroup parentView )
    {
        final PairList<Integer, String> list =
            BrowserController.graphNode.values[( groupPosition + GraphNode.INDEX_EXTRA_VALUES )];
        final String text = list.title;
        final String text2 = Integer.toString( list.length );
        return createView( text, text2, R.layout.browser_index_group, parentView );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isChildSelectable( final int groupPosition, final int childPosition )
    {
        return true;
    }
}
