package ca.chaves.familyBrowser.test.data;

import ca.chaves.android.app.Android;
import ca.chaves.android.app.DatabaseSession;
import ca.chaves.android.graph.GraphAttributes;
import ca.chaves.android.graph.GraphNode;
import ca.chaves.android.graph.GraphStorage;
import ca.chaves.familyBrowser.app.R;

import android.content.Context;
import android.content.res.Resources;

import java.io.IOException;

/**
 * The DatabaseSession and GraphStorage for all unit-tests.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class TestStorage
{
    private final GraphStorage graphStorage;

    /**
     * The index for the "Parent" list.
     */
    public static final int INDEX_PARENT_VALUES = 0 + GraphNode.INDEX_EXTRA_VALUES;

    /**
     * The index for the "Children" list.
     */
    public static final int INDEX_CHILDREN_VALUES = 1 + GraphNode.INDEX_EXTRA_VALUES;

    /**
     * The index for the "Siblings" list.
     */
    public static final int INDEX_SIBLINGS_VALUES = 2 + GraphNode.INDEX_EXTRA_VALUES;

    /**
     * The label for the "Parents" list.
     */
    public static final String PARENTS_LABEL = "Parents";

    /**
     * The label for the "Children" list.
     */
    public static final String CHILDREN_LABEL = "Children";

    /**
     * The label for the "Sibling" list.
     */
    public static final String SIBLINGS_LABEL = "Siblings";

    /**
     * The id for the name attribute.
     */
    public static final Integer NAME_ATTRIBUTE_ID = Integer.valueOf( GraphStorage.NAME_ATTRIBUTE_ID );

    /**
     * Constructor.
     *
     * @param context the Android {@link Context}.
     */
    public TestStorage( final Context context )
    {
        final Resources resources = context.getResources();

        final String[] graphEdgeNames = new String[]{ //
            TestStorage.PARENTS_LABEL, //
                TestStorage.CHILDREN_LABEL, //
                TestStorage.SIBLINGS_LABEL, //
            };

        final String[] graphEdgeIds = new String[]{ //
            resources.getString( R.string.graph_edge_id_parents ), //
                resources.getString( R.string.graph_edge_id_children ), //
                resources.getString( R.string.graph_edge_id_siblings ), //
            };

        graphStorage = new GraphStorage()
        {
            {
                reloadResources( context, graphEdgeNames, graphEdgeIds );
            }
        };
    }

    /**
     * Closes this database.
     */
    public void close()
    {
        DatabaseSession.close();
    }

    /**
     * Load the {@link GraphAttributes} values.
     *
     * @return the graph attributes.
     * @throws IOException on error.
     */
    public GraphAttributes loadAttributes()
        throws IOException
    {
        return graphStorage.loadAttributes( Android.App.getDatabase() );
    }

    /**
     * Load the {@link GraphNode} value for the given 'nodeId'.
     *
     * @param nodeId the node id to load.
     * @return GraphNode.
     * @throws IOException on error.
     */
    public GraphNode loadNode( final int nodeId )
        throws IOException
    {
        return graphStorage.loadNode( Android.App.getDatabase(), nodeId, null );
    }
}
