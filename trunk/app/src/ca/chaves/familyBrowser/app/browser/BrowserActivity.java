package ca.chaves.familyBrowser.app.browser;

import ca.chaves.android.app.AbstractActivity;
import ca.chaves.android.app.Android;
import ca.chaves.android.graph.GraphNode;
import ca.chaves.android.graph.NavigationMenu;
import ca.chaves.android.util.Debug;
import ca.chaves.android.util.PairList;
import ca.chaves.familyBrowser.app.R;
import ca.chaves.familyBrowser.app.bookmark.BookmarksActivity;
import ca.chaves.familyBrowser.app.settings.SettingsActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * The Browser screen for the "Family Browser" application.
 * 
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a> *
 */
public class BrowserActivity
    extends AbstractActivity
{
    /**
     * This is the main view to update. The value userView is set when the screen is fully built, and it is used to
     * force a redraw once the user-interface data buffer changes.
     */
    private View mainView;

    /**
     * "Back" button.
     */
    private ImageButton backButton;

    /**
     * "Start" button.
     */
    private RadioButton startButton;

    /**
     * "Content" button.
     */
    private RadioButton contentButton;

    /**
     * "Index" button.
     */
    private RadioButton indexButton;

    /**
     * Title view.
     */
    private TextView titleTextView;

    /**
     * "Start" pane view.
     */
    private View startPaneView;

    /**
     * "Content" pane view.
     */
    private View contentPaneView;

    /**
     * This ListView displays the node attributes like name, birth-date, etc.
     */
    private ListView contentListView;

    /**
     * {@link BaseAdapter} for 'contentListView'.
     */
    private final BrowserContentListAdapter contentListAdapter = new BrowserContentListAdapter();

    /**
     * "Index" pane view.
     */
    private View indexPaneView;

    /**
     * This ListView displays the node parents, partners, children and siblings.
     */
    private ExpandableListView indexListView;

    /**
     * {@link BaseExpandableListAdapter} for <code>indexListView</code>.
     */
    private final BrowserIndexListAdapter indexListAdapter = new BrowserIndexListAdapter();

    /**
     * How many nodes had been visited so far? This value is used to know if we need to initialize the ListAdapter(s) or
     * just call their `notifyDataSetChanged()' methods.
     */
    private int visitCount;

    /**
     * Click listener for the "Back" button.
     */
    private View.OnClickListener onBackClick = new View.OnClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick( final View view )
        {
            Debug.enter();
            actionStepBack();
            Debug.leave();
        }
    };

    /**
     * Long-click listener for the "Back" button.
     */
    private View.OnLongClickListener onBackLongClick = new View.OnLongClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onLongClick( final View view )
        {
            Debug.enter();

            final NavigationMenu menu = new NavigationMenu( BrowserController.NAVIGATION_PATH );

            final AlertDialog.Builder builder = new AlertDialog.Builder( BrowserActivity.this );
            builder.setCancelable( true );
            builder.setTitle( R.string.browser_goto_title );
            builder.setItems( menu.nodeLabels, new DialogInterface.OnClickListener()
            {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onClick( final DialogInterface dialog, final int which )
                {
                    Debug.enter();
                    actionStepForward( menu.nodeIds[which], menu.nodeLabels[which] );
                    Debug.leave();
                }
            } );

            final AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside( true );
            dialog.show();

            Debug.leave();
            return false;
        }
    };

    /**
     * Change listener for the "Start" button.
     */
    private CompoundButton.OnCheckedChangeListener onStartChange = new CompoundButton.OnCheckedChangeListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onCheckedChanged( final CompoundButton buttonView, final boolean isChecked )
        {
            Debug.enter( isChecked );
            if ( isChecked )
            {
                startPaneView.setVisibility( View.VISIBLE );
                contentPaneView.setVisibility( View.GONE );
                indexPaneView.setVisibility( View.GONE );
            }
            Debug.leave();
        }
    };

    /**
     * Change listener for the "Content" button.
     */
    private CompoundButton.OnCheckedChangeListener onContentChange = new CompoundButton.OnCheckedChangeListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onCheckedChanged( final CompoundButton buttonView, final boolean isChecked )
        {
            Debug.enter( isChecked );
            if ( isChecked )
            {
                startPaneView.setVisibility( View.GONE );
                contentPaneView.setVisibility( View.VISIBLE );
                indexPaneView.setVisibility( View.GONE );
            }
            Debug.leave();
        }
    };

    /**
     * Change listener for the "Index" button.
     */
    private CompoundButton.OnCheckedChangeListener onIndexChange = new CompoundButton.OnCheckedChangeListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onCheckedChanged( final CompoundButton buttonView, final boolean isChecked )
        {
            Debug.enter( isChecked );
            if ( isChecked )
            {
                startPaneView.setVisibility( View.GONE );
                contentPaneView.setVisibility( View.GONE );
                indexPaneView.setVisibility( View.VISIBLE );
            }
            Debug.leave();
        }
    };

    /**
     * Click listener on the "Index" pane.
     */
    private final ExpandableListView.OnChildClickListener onIndexChildClick =
        new ExpandableListView.OnChildClickListener()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean onChildClick( final ExpandableListView parent, final View view, final int groupPosition,
                                         final int childPosition, final long id )
            {
                Debug.enter();
                Debug.print( "click on Index child", groupPosition, childPosition );
                actionStepForward( groupPosition, childPosition );
                Debug.leave();
                return false;
            }
        };

    // ------------------
    // Life cycle methods
    // ------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate( final Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        {
            Debug.enter();
            setupBrowserViews();
            setController( BrowserController.INSTANCE );
            Debug.leave();
        }
    }

    // ----------
    // Call backs
    // ----------

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu( final Menu menu )
    {
        Debug.enter();
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.activity_browser, menu );
        Debug.leave();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected( final MenuItem item )
    {
        Debug.enter();
        boolean handled = false;
        final int itemId = item.getItemId();
        if ( R.id.browser_about_menu_item == itemId )
        {
            final Intent intent = new Intent( this, Android.App.ABOUT_ACTIVITY_CLASS );
            startActivity( intent );
            handled = true;
        }
        else if ( R.id.browser_bookmarks_menu_item == itemId )
        {
            final Intent intent = new Intent( this, BookmarksActivity.class );
            startActivity( intent );
            handled = true;
        }
        else if ( R.id.browser_settings_menu_item == itemId )
        {
            final Intent intent = new Intent( this, SettingsActivity.class );
            startActivity( intent );
            handled = true;
        }
        else
        {
            Debug.print( "unknown option", itemId );
            // builder.setTitle( R.string.select_dialog );
            handled = super.onOptionsItemSelected( item );
        }
        Debug.leave( handled );
        return handled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onControllerUpdated()
    {
        Debug.enter( BrowserController.graphNode.id, BrowserController.graphNode.label );

        // which title should we use?
        final String title = BrowserController.graphNode.label;
        titleTextView.setText( title );

        // update content screen
        if ( visitCount == 0 )
        {
            contentListView.setAdapter( contentListAdapter );
        }
        else
        {
            contentListAdapter.notifyDataSetChanged();
        }

        // update index screen
        if ( visitCount == 0 )
        {
            indexListView.setAdapter( indexListAdapter );
        }
        else
        {
            indexListAdapter.notifyDataSetChanged();
        }

        // make "index" pane visible
        startPaneView.setVisibility( View.GONE );
        contentPaneView.setVisibility( View.GONE );
        indexPaneView.setVisibility( View.VISIBLE );

        // update the main view, to update the whole screen
        mainView.invalidate();

        // increase count
        ++visitCount;
        Debug.leave();
    }

    // -----------------
    // Protected methods
    // -----------------

    /**
     * Show the previous node on the screen.
     */
    protected void actionStepBack()
    {
        Debug.enter();
        // start (re)loading new data
        BrowserController.stepBack();
        Debug.leave();
    }

    /**
     * Show a new node on the screen.
     * 
     * @param nodeId the nodeId to load.
     * @param nodeLabel the nodeLabel to load.
     */
    protected void actionStepForward( final Integer nodeId, final String nodeLabel )
    {
        Debug.enter();
        // start loading new data
        BrowserController.stepForward( nodeId, nodeLabel );
        Debug.leave();
    }

    /**
     * Show a new node on the screen.
     * 
     * @param groupPosition where the new node is.
     * @param childPosition where the new node is.
     */
    protected void actionStepForward( final int groupPosition, final int childPosition )
    {
        Debug.enter( groupPosition, childPosition );
        final PairList<Integer, String> list =
            BrowserController.graphNode.values[( groupPosition + GraphNode.INDEX_EXTRA_VALUES )];
        // start loading new data
        final Integer nodeId = list.array_0[childPosition];
        final String nodeLabel = list.array_1[childPosition];
        actionStepForward( nodeId, nodeLabel );
        Debug.leave();
    }

    // ---------------
    // Private methods
    // ---------------

    /**
     * Initialize this screen.
     */
    private void setupBrowserViews()
    {
        Debug.enter();
        setContentView( R.layout.activity_browser );

        // initialize variables

        mainView = (View) findViewById( R.id.main );

        backButton = (ImageButton) mainView.findViewById( R.id.back_button );

        startButton = (RadioButton) mainView.findViewById( R.id.start_button );
        contentButton = (RadioButton) mainView.findViewById( R.id.content_button );
        indexButton = (RadioButton) mainView.findViewById( R.id.index_button );

        titleTextView = (TextView) mainView.findViewById( R.id.title );

        startPaneView = (View) mainView.findViewById( R.id.start_pane );
        contentPaneView = (View) mainView.findViewById( R.id.content_pane );
        indexPaneView = (View) mainView.findViewById( R.id.index_pane );

        contentListView = (ListView) contentPaneView.findViewById( R.id.content_list );
        indexListView = (ExpandableListView) indexPaneView.findViewById( R.id.index_list );

        // setup listeners

        backButton.setOnClickListener( onBackClick );
        backButton.setOnLongClickListener( onBackLongClick );

        startButton.setOnCheckedChangeListener( onStartChange );
        contentButton.setOnCheckedChangeListener( onContentChange );
        indexButton.setOnCheckedChangeListener( onIndexChange );

        indexListView.setOnChildClickListener( onIndexChildClick );

        // startPaneView.setVisibility( View.GONE );
        // contentPaneView.setVisibility( View.GONE );
        // indexPaneView.setVisibility( View.VISIBLE );

        Debug.leave();
    }
}
