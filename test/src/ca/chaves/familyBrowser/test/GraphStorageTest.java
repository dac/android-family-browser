package ca.chaves.familyBrowser.test;

import ca.chaves.android.graph.GraphAttributes;
import ca.chaves.android.graph.GraphNode;
import ca.chaves.android.graph.GraphStorage;
import ca.chaves.android.util.PairList;
import ca.chaves.familyBrowser.test.data.TestData;
import ca.chaves.familyBrowser.test.data.TestNode;
import ca.chaves.familyBrowser.test.data.TestStorage;
import ca.chaves.familyBrowser.test.util.GraphUtil;

import android.content.Context;
import android.test.AndroidTestCase;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

/**
 * Test for the {@link GraphStorage} class.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class GraphStorageTest
    extends AndroidTestCase
{
    private transient Context context;

    // ----------
    // Unit Tests
    // ----------

    /**
     * Test function.
     */
    public void testNotNulls()
    {
        Assert.assertNotNull( context );
    }

    /**
     * Test function.
     *
     * @throws IOException on io error
     */
    public void testNodeAttributes()
        throws IOException
    {
        final TestStorage storage = new TestStorage( context );
        Assert.assertNotNull( storage );

        final GraphAttributes nodeAttributes = storage.loadAttributes();
        Assert.assertNotNull( nodeAttributes );

        // the "name" attribute must be hidden
        Assert.assertTrue( nodeAttributes.isHiddingAttributeId( TestStorage.NAME_ATTRIBUTE_ID ) );

        storage.close();
    }

    /**
     * Test function.
     */
    private void doTestNode( final TestStorage storage, final TestNode sample, final GraphNode node )
    {
        Assert.assertNotNull( storage );

        Assert.assertNotNull( sample );
        Assert.assertNotNull( sample.id );
        Assert.assertNotNull( sample.label );
        Assert.assertNotNull( sample.parents );
        Assert.assertNotNull( sample.children );
        Assert.assertNotNull( sample.siblings );

        Assert.assertNotNull( node );
        Assert.assertNotNull( node.id );
        Assert.assertNotNull( node.label, "node name" );
        Assert.assertNotNull( node.values );

        Assert.assertEquals( sample.id, node.id );
        Assert.assertEquals( sample.label, node.label );

        final PairList<Integer, String> attributes = node.values[( GraphNode.INDEX_ATTRIBUTE_VALUES )];
        Assert.assertNotNull( attributes );

        final PairList<Integer, String> parents = node.values[( TestStorage.INDEX_PARENT_VALUES )];
        Assert.assertNotNull( parents );
        Assert.assertEquals( parents.title, TestStorage.PARENTS_LABEL );

        final PairList<Integer, String> children = node.values[( TestStorage.INDEX_CHILDREN_VALUES )];
        Assert.assertNotNull( children );
        Assert.assertEquals( children.title, TestStorage.CHILDREN_LABEL );

        final PairList<Integer, String> siblings = node.values[( TestStorage.INDEX_SIBLINGS_VALUES )];
        Assert.assertNotNull( siblings );
        Assert.assertEquals( siblings.title, TestStorage.SIBLINGS_LABEL );
    }

    /**
     * Test function.
     *
     * @throws IOException on io error
     */
    public void testNodeValues()
        throws IOException
    {
        final TestStorage storage = new TestStorage( context );
        Assert.assertNotNull( storage );

        final TestNode sample = TestData.getDefaultNode( context );
        final GraphNode node = storage.loadNode( GraphStorage.DEFAULT_NODE_ID );

        doTestNode( storage, sample, node );

        storage.close();
    }

    /**
     * Test function.
     *
     * @throws IOException on io error.
     */
    public void testNodeTree()
        throws IOException
    {
        final TestStorage storage = new TestStorage( context );
        Assert.assertNotNull( storage );

        final TestNode sample = TestData.getSampleTree( context );
        final GraphNode node = storage.loadNode( GraphStorage.DEFAULT_NODE_ID );

        doTestNode( storage, sample, node );

        final List<TestNode> queue = new LinkedList<TestNode>();
        queue.add( sample );

        Assert.assertFalse( sample.visited );

        int counter = 0;
        while ( !queue.isEmpty() )
        {
            final TestNode that = queue.remove( 0 );
            if ( !that.visited )
            {
                that.visited = true;
                final GraphNode data = storage.loadNode( that.id.intValue() );

                doTestNode( storage, that, data );

                GraphStorageTest.addToQueue( storage, that, data, queue );
                ++counter;
            }
        }

        // we visited more than 5 nodes from `queue`
        Assert.assertTrue( 5 < counter );

        storage.close();
    }

    // ------
    // Set Up
    // ------

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        context = super.getContext();
        // note: can not initialize `storage' here
        // since `Application.instance' is still null
    }

    // -------
    // Helpers
    // -------

    /**
     * Update the visiting queue with links from this node.
     *
     * @param storage
     * @param that
     * @param node
     * @param queue
     */
    private static void addToQueue( final TestStorage storage, final TestNode that, final GraphNode node,
                                    final List<TestNode> queue )
    {
        Assert.assertNotNull( storage );
        Assert.assertNotNull( queue );

        Assert.assertNotNull( that );
        Assert.assertNotNull( node );

        GraphStorageTest.addListToQueue( that.parents, node.values[( TestStorage.INDEX_PARENT_VALUES )], queue );
        GraphStorageTest.addListToQueue( that.children, node.values[( TestStorage.INDEX_CHILDREN_VALUES )], queue );
        GraphStorageTest.addListToQueue( that.siblings, node.values[( TestStorage.INDEX_SIBLINGS_VALUES )], queue );
    }

    /**
     * Utility function.
     *
     * @param samples
     * @param listIndex
     * @param node
     * @param queue
     */
    private static void addListToQueue( final TestNode[] samples, final PairList<Integer, String> values,
                                        final List<TestNode> queue )
    {
        Assert.assertNotNull( samples );
        Assert.assertNotNull( values );

        for ( final TestNode sample : samples )
        {
            sample.id = GraphUtil.findValueIdByString( values, sample.label );
            Assert.assertNotNull( sample.id );

            queue.add( sample );
        }
    }
}
