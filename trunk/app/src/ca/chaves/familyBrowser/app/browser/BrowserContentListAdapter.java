package ca.chaves.familyBrowser.app.browser;

import ca.chaves.android.graph.GraphNode;
import ca.chaves.android.util.PairList;
import ca.chaves.android.widget.WidgetUtils;
import ca.chaves.familyBrowser.app.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * ListAdapter to populate the "Content" tab.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
class BrowserContentListAdapter
    extends BaseAdapter
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
    public int getCount()
    {
        return BrowserController.graphNode.values[( GraphNode.INDEX_ATTRIBUTE_VALUES )].length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getItem( final int position )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId( final int position )
    {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView( final int position, final View convertView, final ViewGroup parentView )
    {
        final PairList<Integer, String> list = BrowserController.graphNode.values[( GraphNode.INDEX_ATTRIBUTE_VALUES )];
        final String text = BrowserController.graphAttributes.getAttributeLabel( list.array_0[position] );
        final String text2 = list.array_1[position];
        return createView( text, text2, R.layout.browser_content_child, parentView );
    }
}
