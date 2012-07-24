package ca.chaves.android.widget;

import ca.chaves.android.app.Android;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class contains static functions to inflate views from layouts. We use this factory to build screens.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public final class WidgetUtils
{
    /**
     * Create a view from the given layout resource_id.
     *
     * @param layoutResourceId the layout resource id
     * @param parentView the parent {@link View}
     * @return View
     */
    public static View createView( final int layoutResourceId, final ViewGroup parentView )
    {
        return Android.App.INFLATER.inflate( layoutResourceId, parentView, false );
    }

    /**
     * Create a text view from the given layout resource_id.
     *
     * @param text value
     * @param textResourceId resource_id, embedded inside the layoutResourceId
     * @param layoutResourceId resource_id
     * @param parentView the parent view
     * @return View
     */
    public static View createTextView( final String text, final int textResourceId, final int layoutResourceId,
                                       final ViewGroup parentView )
    {
        final View layout = WidgetUtils.createView( layoutResourceId, parentView );
        if ( text != null )
        {
            final TextView textView = (TextView) layout.findViewById( textResourceId );
            textView.setText( text );
        }
        return layout;
    }

    /**
     * Create two text views from the given layout resource_id.
     *
     * @param text value
     * @param textResourceId resource_id, embedded inside the layoutResourceId
     * @param text2 2nd value
     * @param text2ResourceId 2nd resource_id
     * @param layoutResourceId resource_id
     * @param parentView parent view
     * @return View
     */
    public static View createTextViews( final String text, final int textResourceId, final String text2,
                                        final int text2ResourceId, final int layoutResourceId,
                                        final ViewGroup parentView )
    {
        final View layout = WidgetUtils.createView( layoutResourceId, parentView );
        if ( text != null )
        {
            final TextView textView = (TextView) layout.findViewById( textResourceId );
            textView.setText( text );
        }
        if ( text2 != null )
        {
            final TextView textView = (TextView) layout.findViewById( text2ResourceId );
            textView.setText( text2 );
        }
        return layout;
    }

    /**
     * Utility function to invalidate ListView(s). This function is needed because BaseExpandableListAdapter and
     * BaseAdapter do not have a common base class that defines the 'invalidate' operation.
     *
     * @param listView the {@link ListView} to invalidate
     */
    public static void invalidateDataSet( final ListView listView )
    {
        // for some unknown reason, .notifyDataSetInvalidated() is not in
        // shared interface between BaseExpandableListAdapter and BaseAdapter
        if ( listView instanceof ExpandableListView )
        {
            final ExpandableListView view = (ExpandableListView) listView;
            final BaseExpandableListAdapter adapter = (BaseExpandableListAdapter) view.getExpandableListAdapter();
            adapter.notifyDataSetInvalidated();
        }
        else
        {
            final BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
            adapter.notifyDataSetInvalidated();
        }
    }
}
