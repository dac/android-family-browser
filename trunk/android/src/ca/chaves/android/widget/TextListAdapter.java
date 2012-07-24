package ca.chaves.android.widget;

import ca.chaves.android.R;
import ca.chaves.android.app.Android;
import ca.chaves.android.util.Debug;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * List adapter for simple lists, which contain an object with a single text.
 *
 * @param <ItemType> the list element type.
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class TextListAdapter<ItemType>
    extends BaseAdapter
{
    /**
     * The layout to be used for each item in the list.
     */
    protected int layoutId = R.layout.textlist_item;

    /**
     * The TextView inside 'layoutId'.
     */
    protected int textViewId = R.id.text;

    /**
     * Background color - we display the list items in alternative backgrounds.
     */
    private static final int[] BACKGROUND_COLORS = new int[]{0x30000000, 0x30FFFFFF};

    /**
     * The list contents.
     */
    private List<ItemType> list;

    /**
     * Set a new data source.
     *
     * @param list the data source.
     */
    public void setList( final List<ItemType> list )
    {
        Debug.enter();
        this.list = list;
        notifyDataSetChanged();
        Debug.leave();
    }

    /**
     * Return the text inside the given item. This function should be override in derived classes.
     *
     * @param item the data element.
     * @return text to be displayed.
     */
    protected String getItemText( final ItemType item )
    {
        return item.toString();
    }

    // -------------------
    // BaseAdapter methods
    // -------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView( final int position, final View convertView, final ViewGroup parentView )
    {
        Debug.enter();
        View view = convertView;
        TextView textView;

        if ( view == null )
        {
            view = Android.App.INFLATER.inflate( this.layoutId, parentView, false );

            textView = (TextView) view.findViewById( this.textViewId );
            view.setTag( textView );
        }
        else
        {
            textView = (TextView) view.getTag();
        }

        final ItemType item = list.get( position );
        final String text = this.getItemText( item );
        textView.setText( text );

        view.setBackgroundColor( TextListAdapter.BACKGROUND_COLORS[position % TextListAdapter.BACKGROUND_COLORS.length] );
        Debug.leave();
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount()
    {
        if ( list == null )
        {
            return 0;
        }
        return list.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ItemType getItem( final int position )
    {
        if ( list == null )
        {
            return null;
        }
        return list.get( position );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId( final int position )
    {
        return position;
    }
}
