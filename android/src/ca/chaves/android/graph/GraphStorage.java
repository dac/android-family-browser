package ca.chaves.android.graph;

import ca.chaves.android.profile.AbstractProfile;
import ca.chaves.android.util.CacheReference;
import ca.chaves.android.util.CacheReferenceMap;
import ca.chaves.android.util.Debug;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * This class defines the graph storage being used in the current database session. For example, it takes care of
 * caching nodes and attributes, as well as reading and writing the graph database.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class GraphStorage
    extends GraphResources
{
    /**
     * The magic column ID for the field "name" - the source file share/tools/familyTree_yaml.py defines this value.
     */
    public static final int NAME_ATTRIBUTE_ID = 1;

    /**
     * The default node_id - we start browsing at this location by default. This value is defined inside the script
     * share/tools/familyTree_yaml.py -- you must ensure that exactly the same constant is being used here and in the
     * script.
     */
    public static final int DEFAULT_NODE_ID = 1000000;

    // -----------
    // Cache Areas
    // -----------

    /**
     * The node cache.
     */
    private final CacheReferenceMap<Integer, GraphNode> nodesCache = new CacheReferenceMap<Integer, GraphNode>();

    /**
     * The attributes cache.
     */
    private final CacheReference<GraphAttributes> attributesCache = new CacheReference<GraphAttributes>();

    // -------------
    // Attribute I/O
    // -------------

    /**
     * Load the Attributes from the database.
     *
     * @param database where to load the graph attributes from
     * @return graph attributes
     * @throws IOException on error
     */
    public GraphAttributes loadAttributes( final SQLiteDatabase database )
        throws IOException
    {
        Debug.enter();

        // load from cache, if possible

        final GraphAttributes cached = attributesCache.get();
        if ( cached != null )
        {
            Debug.leave( "reuse cached" );
            return cached;
        }

        // load new data buffer, otherwise

        final GraphAttributes attributes = new GraphAttributes();

        Debug.print( "execute:", super.selectAttributesStmt );
        final Cursor cursor = database.rawQuery( super.selectAttributesStmt, null );

        try
        {
            if ( cursor.moveToFirst() )
            {
                do
                {
                    final Integer id = Integer.valueOf( cursor.getInt( 0 ) );
                    final boolean hidden = ( cursor.getInt( 1 ) != 0 );
                    final String value = cursor.getString( 2 );
                    Debug.print( "fetch attribute:", id, value, hidden );
                    attributes.addAttribute( id, hidden, value );
                }
                while ( cursor.moveToNext() );
            }
        }
        finally
        {
            if ( cursor != null )
            {
                cursor.close();
            }
        }

        // update cache and return

        Debug.leave();
        return attributesCache.put( attributes );
    }

    // --------
    // Node I/O
    // --------

    /**
     * Load the Node from the database, given the 'nodeId' value.
     *
     * @param database where to load the node from.
     * @param nodeId the node id to load.
     * @param nodeLabel the node label to load.
     * @return the graph node.
     * @throws IOException on error.
     */
    public GraphNode loadNode( final SQLiteDatabase database, final Integer nodeId, final String nodeLabel )
        throws IOException
    {
        Debug.enter();

        // load from cache, if possible

        Integer trueNodeId = nodeId;
        if ( trueNodeId == null )
        {
            trueNodeId = AbstractProfile.active().getGraphNodeId();
        }
        if ( trueNodeId == null )
        {
            trueNodeId = Integer.valueOf( GraphStorage.DEFAULT_NODE_ID );
        }
        final GraphNode cached = nodesCache.get( trueNodeId );
        if ( cached != null )
        {
            Debug.leave( "reuse cached:", trueNodeId );
            return cached;
        }

        // load new data buffer, otherwise

        final GraphAttributes attributes = loadAttributes( database );
        final GraphNode node = new GraphNode( trueNodeId, nodeLabel, //
                                              super.selectEdgesByNodeIdStmt.length );

        // load node attribute's list

        final String[] selectArgs = new String[]{trueNodeId.toString()};

        {
            final GraphValueList valueList = new GraphValueList( null );
            node.values[GraphNode.INDEX_ATTRIBUTE_VALUES] = valueList;

            final String selectStmt = super.selectNodeByNodeIdStmt;
            loadNodeList( database, attributes, node, valueList, selectStmt, selectArgs );
        }

        // load node edge's lists

        for ( int index = 0; index < super.selectEdgesByNodeIdStmt.length; ++index )
        {
            final GraphValueList valueList = new GraphValueList( super.adjacencyEdgeNameList[index] );
            node.values[index + GraphNode.INDEX_EXTRA_VALUES] = valueList;

            final String selectStmt = super.selectEdgesByNodeIdStmt[index];
            loadEdgeList( database, node, valueList, selectStmt, selectArgs );
        }

        // update cache and return

        Debug.leave();
        return nodesCache.put( trueNodeId, node );
    }

    /**
     * Load the node's Attribute list from the database.
     *
     * @param database where to load the attributes from
     * @param attributes the graph attributes
     * @param node the graph node
     * @param valueList the attribute list object to load to
     * @param selectStmt the select statement
     * @param selectArgs the arguments to be applied to 'selectStmt'
     * @throws IOException on error
     */
    private void loadNodeList( final SQLiteDatabase database, final GraphAttributes attributes, final GraphNode node,
                               final GraphValueList valueList, final String selectStmt, final String[] selectArgs )
        throws IOException
    {
        Debug.enter();

        Debug.print( "execute:", selectStmt, Arrays.toString( selectArgs ) );
        final Cursor cursor = database.rawQuery( selectStmt, selectArgs );

        try
        {
            if ( cursor.moveToFirst() )
            {
                do
                {
                    final Integer attributeId = Integer.valueOf( cursor.getInt( 0 ) );
                    final String attributeValue = cursor.getString( 1 );
                    final boolean attributeHidden = attributes.isHiddingAttributeId( attributeId );
                    Debug.print( "fetch value:", valueList.title, attributeId, attributeValue, attributeHidden );
                    // overwrite the given attribute "name"
                    if ( attributeId.intValue() == GraphStorage.NAME_ATTRIBUTE_ID )
                    {
                        node.label = attributeValue;
                    }
                    // filter-out all hidden attributes
                    if ( attributeHidden )
                    {
                        continue;
                    }
                    valueList.add( attributeId, attributeValue );
                }
                while ( cursor.moveToNext() );
            }
        }
        finally
        {
            if ( cursor != null )
            {
                cursor.close();
            }
        }

        Debug.leave();
    }

    /**
     * This internal function loads ValueList from the database containing link_id(s) to other nodes.
     *
     * @param database
     * @param node
     * @param valueList
     * @param selectStmt
     * @param selectArgs
     * @throws IOException
     */
    private void loadEdgeList( final SQLiteDatabase database, final GraphNode node, final GraphValueList valueList,
                               final String selectStmt, final String[] selectArgs )
        throws IOException
    {
        Debug.enter();

        Debug.print( "execute:", selectStmt, Arrays.toString( selectArgs ) );
        final Cursor cursor = database.rawQuery( selectStmt, selectArgs );
        try
        {
            if ( cursor.moveToFirst() )
            {
                do
                {
                    final Integer attributeId = Integer.valueOf( cursor.getInt( 0 ) );
                    final String attributeValue = cursor.getString( 1 );
                    Debug.print( "fetch link:", valueList.title, attributeId, attributeValue );
                    valueList.add( attributeId, attributeValue );
                }
                while ( cursor.moveToNext() );
            }
        }
        finally
        {
            if ( cursor != null )
            {
                cursor.close();
            }
        }

        Debug.leave();
    }

    // -------------
    // Reset Storage
    // -------------

    /**
     * Reinitialize the storage parameters. This function must be called at least once - during the application
     * start-up.
     *
     * @param context the application context
     * @param database is the new database object to use
     * @param locale the new language locale
     * @throws IOException on error
     */
    public void resetStorage( final Context context, final SQLiteDatabase database, final Locale locale )
        throws IOException
    {
        Debug.enter( locale.getDisplayName() );
        // reset caches
        nodesCache.clear();
        attributesCache.clear();
        // reload resources
        reloadResources( context );
        // reload default data
        loadNode( database, null, null );
        Debug.leave();
    }
}
