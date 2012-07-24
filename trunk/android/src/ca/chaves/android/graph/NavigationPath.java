package ca.chaves.android.graph;

import ca.chaves.android.util.Debug;
import ca.chaves.android.util.PairList;

/**
 * This class stores a navigation path, from a starting <code>(nodeId, nodeLabel)</code> up to an ending
 * <code>(nodeId, nodeLabel)</code>.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class NavigationPath
    extends PairList<Integer, String>
{
    /**
     * The max depth to keep in the navigation path.
     */
    public static final int MAX_DEPTH = 100;

    /**
     * What would be the index for the next "step forward". The "current" index is, therefore, <code>(next - 1)</code>.
     */
    public int next;

    /**
     * Constructor.
     */
    public NavigationPath()
    {
        super( null );
    }

    /**
     * Get the current position's nodeId.
     *
     * @return the current nodeId.
     */
    public Integer getNodeId()
    {
        Integer nodeId = null;
        if ( 0 < next )
        {
            nodeId = array_0[next - 1];
        }
        return nodeId;
    }

    /**
     * Get the current position's nodeLabel.
     *
     * @return the current nodeLabel.
     */
    public String getNodeLabel()
    {
        String nodeLabel = null;
        if ( 0 < next )
        {
            nodeLabel = array_1[next - 1];
        }
        return nodeLabel;
    }

    /**
     * Clear path.
     */
    @Override
	public void clear()
    {
        next = 0;
        super.clear();
    }

    /**
     * Step back one position.
     */
    public void stepBack()
    {
        Debug.enter( next );

        if ( 0 < next )
        {
            --next;
        }

        // Debug.print( "BACK", next, length, Arrays.toString( array_0 ), Arrays.toString( array_1 ) );
        Debug.leave( next );
    }

    /**
     * Step forward one position.
     *
     * @param nodeId the nodeId for the new position forward.
     * @param nodeLabel the nodeLabel for the new position forward.
     */
    public void stepForward( final Integer nodeId, final String nodeLabel )
    {
        Debug.enter( next, nodeId, nodeLabel );

        if ( next < length && nodeId.equals( array_0[next] ) )
        {
            ++next;
        }
        else if ( 0 < next && nodeId.equals( array_0[next - 1] ) )
        {
            // do nothing - just remain here
        }
        else
        {
            // truncate path, just in case
            remove( next, length - next );

            // remove any previous instance, just in case we are re-visiting an old position
            for ( int index = 0; index < length; ++index )
            {
                if ( nodeId.equals( array_0[index] ) )
                {
                    remove( index, 1 );
                    break;
                }
            }

            // discard all old steps once we reach the MAX_DEPTH limit,
            // but keep the oldest one, which we regard as the "origin"
            final int excessCount = length - NavigationPath.MAX_DEPTH;
            if ( 0 < excessCount )
            {
                remove( 1, excessCount );
            }

            // add the new position
            add( nodeId, nodeLabel );
            next = length;
        }

        // Debug.print( "FRWD", next, length, Arrays.toString( array_0 ), Arrays.toString( array_1 ) );
        Debug.leave( next, nodeId, nodeLabel );
    }
}
