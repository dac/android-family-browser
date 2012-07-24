package ca.chaves.familyBrowser.app.browser;

import ca.chaves.android.app.Android;
import ca.chaves.android.app.DatabaseSession;
import ca.chaves.android.graph.GraphAttributes;
import ca.chaves.android.graph.GraphNode;
import ca.chaves.android.graph.NavigationPath;
import ca.chaves.android.util.AbstractAsyncTask;
import ca.chaves.android.util.AbstractController;
import ca.chaves.android.util.Debug;
import ca.chaves.familyBrowser.app.R;

import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

/**
 * The controller for the {@link BrowserActivity}.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public final class BrowserController
    extends AbstractController
{
    /**
     * Controller singleton.
     */
    public static final BrowserController INSTANCE = new BrowserController();

    /**
     * Navigation path singleton.
     */
    public static final NavigationPath NAVIGATION_PATH = new NavigationPath();

    /**
     * Data buffer - node.
     */
    public static transient GraphNode graphNode;

    /**
     * Data buffer - attributes.
     */
    public static transient GraphAttributes graphAttributes;

    /**
     * Initializer.
     */
    static
    {
        BrowserController.stepForward( null, null );
    }

    /**
     * Reload the "previous" graph node in background.
     */
    public static void stepBack()
    {
        Debug.enter();
        // update navigation path
        BrowserController.NAVIGATION_PATH.stepBack();
        // load new node
        final Integer nodeId = BrowserController.NAVIGATION_PATH.getNodeId();
        final String nodeLabel = BrowserController.NAVIGATION_PATH.getNodeLabel();
        stepForward( nodeId, nodeLabel );
        Debug.leave();
    }

    /**
     * Load the graph node in background.
     *
     * @param nodeId the node id to be loaded
     * @param nodeLabel default node label, if no label is included in the node storage
     */
    public static void stepForward( final Integer nodeId, final String nodeLabel )
    {
        Debug.enter();

        /**
         * Background task used to load graph nodes.
         */
        final class Task
            extends AbstractAsyncTask<Void, Void, Void>
        {
            /**
             * The node buffer.
             */
            private GraphNode nodeBuffer;

            /**
             * The attributes buffer.
             */
            private GraphAttributes attributesBuffer;

            /**
             * {@inheritDoc}
             */
            @Override
            protected boolean isCanceling()
            {
                return false;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected Void doInBackground( final Void... params )
            {
                Debug.enter();
                try
                {
                    final SQLiteDatabase database = Android.App.getDatabase();
                    nodeBuffer = DatabaseSession.GRAPH_STORE.loadNode( database, nodeId, nodeLabel );
                    attributesBuffer = DatabaseSession.GRAPH_STORE.loadAttributes( database );
                    check( nodeBuffer != null && attributesBuffer != null, R.string.io_error );
                }
                catch ( final IOException ex )
                {
                    Debug.error( ex, "unable to read data", nodeId );
                    error( R.string.io_error );
                }
                Debug.leave();
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void onPostExecute( final Void result )
            {
                Debug.enter();
                if ( canceled() )
                {
                    Debug.print( "canceled - ignoring" );
                }
                else if ( failed() )
                {
                    Debug.print( "failed", nodeId, errorMessage );
                    BrowserController.INSTANCE.flash( errorMessage );
                }
                else
                {
                    // save new data
                    BrowserController.graphNode = nodeBuffer;
                    BrowserController.graphAttributes = attributesBuffer;
                    // update navigation path
                    BrowserController.NAVIGATION_PATH.stepForward( nodeBuffer.id, nodeBuffer.label );
                    // update user interface
                    BrowserController.INSTANCE.onControllerUpdated();
                }
                Debug.leave();
            }
        }

        new Task().execute();
        Debug.leave();
    }
}
