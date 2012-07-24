package ca.chaves.android.graph;

/**
 * This class stores the attributes, and lists of parents, partners, children and siblings, corresponding to one
 * node_id.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class GraphNode
{
    /**
     * The node_id. This internal integer is used as the "logical address, name or ID of this node".
     */
    public final Integer id;

    /**
     * The node label. This value is usually displayed as the "node title" or "node name".
     */
    public String label;

    /**
     * The value lists. For example, some of the entries in values[] are lists of node_id(s), representing an edge or
     * directed arc ending in those node_id(s). In particular, values[INDEX_ATTRIBUTE_VALUES] contains the node
     * attributes_id(s).
     */
    public final GraphValueList[] values;

    /**
     * The list at values[INDEX_ATTRIBUTE_VALUES] is the node attribute_id(s).
     */
    public static final int INDEX_ATTRIBUTE_VALUES = 0;

    /**
     * The list at values[INDEX_EXTRA_VALUES] is the first list containing edge name and values. The other edges are in
     * values[INDEX_EXTRA_VALUES+1] and so on.
     */
    public static final int INDEX_EXTRA_VALUES = 1;

    /**
     * Constructor.
     */
    public GraphNode()
    {
        this( null, null, 0 );
    }

    /**
     * Constructor.
     *
     * @param id the graph node id.
     * @param label the graph node label.
     * @param extraValuesLength the number of extra {@link GraphValueList} instances in this node.
     */
    public GraphNode( final Integer id, final String label, final int extraValuesLength )
    {
        this.id = id; // ( id != null ) ? id : Integer.valueOf( GraphStorage.DEFAULT_NODE_ID );
        this.label = ( label != null ) ? label : "";
        this.values = new GraphValueList[GraphNode.INDEX_EXTRA_VALUES + extraValuesLength];
    }
}
