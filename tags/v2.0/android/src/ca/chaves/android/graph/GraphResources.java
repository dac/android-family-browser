package ca.chaves.android.graph;

import ca.chaves.android.R;
import ca.chaves.android.util.Debug;

import android.content.Context;
import android.content.res.Resources;

/**
 * This class contains the SQL Statements which are build from the application resources.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
class GraphResources
{
    /**
     * This SELECT statement returns all attribute_id(s).
     */
    protected String selectAttributesStmt;

    /**
     * This SELECT statement returns all attribute_id(s) and attribute values for a given node_id.
     */
    protected String selectNodeByNodeIdStmt;

    /**
     * This SELECT statement return all node_id(s) associated with the given node_id.
     */
    protected String[] selectEdgesByNodeIdStmt;

    /**
     * Types of adjacency edges/links/arcs: parent, child, sibling, etc...
     */
    protected String[] adjacencyEdgeNameList;

    /**
     * Load/load/creates all the SQL statements.
     *
     * @param context the application context.
     * @param adjacencyEdgeNames the array of adjacency edge names.
     * @param selectEdgesByNodeId the node_id for the sql statement.
     */
    protected void reloadResources( final Context context, final String[] adjacencyEdgeNames,
                                    final String[] selectEdgesByNodeId )
    {
        final Resources resources = context.getResources();

        // adjacency edge types
        adjacencyEdgeNameList = adjacencyEdgeNames;

        // sql statements for attributes
        selectAttributesStmt = resources.getString( R.string.graph_sql_select_attributes );
        Debug.print( "select attributes:", selectAttributesStmt );

        // sql statements for nodes
        selectNodeByNodeIdStmt = resources.getString( R.string.graph_sql_select_node_by_node_id );
        Debug.print( "select node by node_id:", selectNodeByNodeIdStmt );

        // sql statements for node links

        final String stmtTemplate = resources.getString( R.string.graph_sql_select_edges_by_node_id );

        selectEdgesByNodeIdStmt = new String[selectEdgesByNodeId.length];
        for ( int index = 0; index < selectEdgesByNodeId.length; ++index )
        {
            selectEdgesByNodeIdStmt[index] = String.format( stmtTemplate, selectEdgesByNodeId[index] );
            Debug.print( "select", adjacencyEdgeNameList[index], //
                         "by node_id:", selectEdgesByNodeIdStmt[index] );
        }
    }

    /**
     * Reset resources storage.
     *
     * @param context the application context
     */
    protected void reloadResources( final Context context )
    {
        final Resources resources = context.getResources();

        reloadResources( context, resources.getStringArray( R.array.graph_edge_names ),
                         resources.getStringArray( R.array.graph_edge_ids ) );
    }
}
