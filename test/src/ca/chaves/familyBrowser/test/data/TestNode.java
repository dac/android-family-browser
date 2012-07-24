package ca.chaves.familyBrowser.test.data;

/**
 * This class represents a GraphNode to be used inside unit tests.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class TestNode
{
    /**
     * The node id inside the database.
     */
    public Integer id;

    /**
     * The node name.
     */
    public String label;

    /**
     * The parents list.
     */
    public TestNode[] parents = new TestNode[0];

    /**
     * The siblings list.
     */
    public TestNode[] siblings = new TestNode[0];

    /**
     * The children list.
     */
    public TestNode[] children = new TestNode[0];

    /**
     * This flag is true if this node was already visited in assertEqualsAndAdd().
     */
    public boolean visited;
}
