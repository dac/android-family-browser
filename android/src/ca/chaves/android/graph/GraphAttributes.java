package ca.chaves.android.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class stores the node attribute's meta-data. Each attribute contains a flag indicating if it is a hidden
 * attribute, and a label.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class GraphAttributes
{
    /**
     * This contains the set of hidden attribute_id(s).
     */
    private final Set<Integer> hiddenIds = new HashSet<Integer>();

    /**
     * This contains the attribute labels - this map is indexed by attribute_id(s).
     */
    private final Map<Integer, String> labelMap = new HashMap<Integer, String>();

    /**
     * Is this attribute_id hidden?
     *
     * @param id the attribute id to check if it is hidden or no
     * @return true if this attribute id is hidden
     */
    public boolean isHiddingAttributeId( final Integer id )
    {
        return hiddenIds.contains( id );
    }

    /**
     * Get the attribute label for this attribute_id.
     *
     * @param id the attribute id to get the label for
     * @return the attribute id's label
     */
    public String getAttributeLabel( final Integer id )
    {
        final String label = labelMap.get( id );
        return ( label == null ) ? "" : label;
    }

    /**
     * Add attribute.
     *
     * @param id the attribute id to add data for
     * @param hidden true if this is an hidden attribute
     * @param label the attribute id's label
     */
    public void addAttribute( final Integer id, final boolean hidden, final String label )
    {
        if ( hidden )
        {
            hiddenIds.add( id );
        }
        if ( label != null )
        {
            labelMap.put( id, label );
        }
    }
}
