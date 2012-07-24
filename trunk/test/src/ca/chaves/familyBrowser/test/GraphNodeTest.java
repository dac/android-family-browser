package ca.chaves.familyBrowser.test;

import ca.chaves.android.graph.GraphNode;
import ca.chaves.android.util.PairList;

import android.test.AndroidTestCase;

import junit.framework.Assert;

/**
 * Test for the {@link GraphNode} class.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class GraphNodeTest
    extends AndroidTestCase
{
    private static final Integer NUEVE = Integer.valueOf( 9 );

    // ----------
    // Unit Tests
    // ----------

    /**
     * Test function.
     */
    public void testNotNulls()
    {
        Assert.assertNotNull( GraphNodeTest.NUEVE );
    }

    /**
     * Test function.
     */
    public void testIdAndName()
    {
        final GraphNode node = new GraphNode( GraphNodeTest.NUEVE, "Nueve", 0 );
        Assert.assertNotNull( node );

        Assert.assertEquals( GraphNodeTest.NUEVE, node.id );
        Assert.assertEquals( "Nueve", node.label );
    }

    /**
     * Test function.
     */
    public void testListMax()
    {
        final GraphNode node = new GraphNode( GraphNodeTest.NUEVE, "Nueve", 0 );
        Assert.assertNotNull( node );

        try
        {
            @SuppressWarnings( "unused" )
            final PairList<Integer, String> dummy = node.values[( GraphNode.INDEX_EXTRA_VALUES )];
            Assert.fail( "expected array index overflow exception" );
        }
        catch ( final ArrayIndexOutOfBoundsException ex )
        {
            Assert.assertTrue( true );
        }
    }
}
