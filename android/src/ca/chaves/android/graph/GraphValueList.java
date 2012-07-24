package ca.chaves.android.graph;

import ca.chaves.android.util.PairList;

/**
 * This class stores a list of tuples (integer, string). For example, it can be used to store either the node
 * attribute(s) or the node parent/partner/child/sibling lists, for a given node_id. This class is implemented to
 * minimize memory allocations, which are expensive in Android.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class GraphValueList
    extends PairList<Integer, String>
{
    /**
     * Constructor.
     *
     * @param label the list label.
     */
    public GraphValueList( final String label )
    {
        super( label );
    }
}
