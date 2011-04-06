package ca.chaves.familyBrowser.helpers;

/**
 * This class stores the attributes, and lists of parents, partners, children
 * and siblings, for a given node_id.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeObject {

    protected static final String TAG = NodeObject.class.getSimpleName();

    /** This is the node_id */
    private Integer m_nodeId;
    /** This is the node name */
    private String m_nodeName;

    /**
     * This contains the node lists. For example, m_listValues[LIST_ATTRIBUTES]
     * contains the node attributes, m_listValues[LIST_PARENTS] contains the
     * parents, and so on.
     */
    private final NodeValueList[] m_listValues = new NodeValueList[] {
                    new NodeValueList("Attributes"), //
                    new NodeValueList("Parents"), //
                    new NodeValueList("Partners"), //
                    new NodeValueList("Children"), //
                    new NodeValueList("Siblings"), //
    };

    /** m_listValues[LIST_ATTRIBUTES] is the node attributes */
    public static final int LIST_ATTRIBUTES = 0;
    /** m_listValues[LIST_PARENTS] is the node parents */
    public static final int LIST_PARENTS = 1;
    /** m_listValues[LIST_PARTNERS] is the node partners */
    public static final int LIST_PARTNERS = 2;
    /** m_listValues[LIST_CHILDREN] is the node children */
    public static final int LIST_CHILDREN = 3;
    /** m_listValues[LIST_SIBLINGS] is the node siblings */
    public static final int LIST_SIBLINGS = 4;
    /** length of m_listValues */
    public static final int LIST_MAX = 5;

    /**
     * Get the node_id.
     *
     * @return the node_id
     */
    public Integer getNodeId() {
        return this.m_nodeId;
    }

    /**
     * Set the node_id.
     *
     * @param nodeId
     */
    public void setNodeId(final Integer nodeId) {
        this.m_nodeId = nodeId;
    }

    /**
     * Get the node name.
     *
     * @return the node name
     */
    public String getNodeName() {
        return this.m_nodeName;
    }

    /**
     * Set the node name.
     *
     * @param nodeName
     */
    public void setNodeName(final String nodeName) {
        this.m_nodeName = nodeName;
    }

    /**
     * Get the list at the given position 'listIndex'.
     *
     * @param listIndex
     *            a number between 0 and (LIST_MAX-1).
     * @return NodeValueList
     */
    public NodeValueList getNodeListValues(final int listIndex) {
        return this.m_listValues[listIndex];
    }

    /**
     * Clear this NodeObject. We use this function in order to minimize memory
     * allocations, which are expensive in Android.
     */
    public void resetNodeContent() {
        this.m_nodeId = null;
        this.m_nodeName = null;
        for (NodeValueList list : this.m_listValues) {
            list.clearValues();
        }
    }
}
