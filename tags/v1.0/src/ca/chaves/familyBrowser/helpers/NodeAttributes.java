package ca.chaves.familyBrowser.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class stores the node attribute's meta-data. Each attribute contains a
 * flag indicating if it is a hidden attribute, and a label.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeAttributes {

    protected static final String TAG = NodeAttributes.class.getSimpleName();

    /** This contains the hidden attribute_id(s) */
    private final Set<Integer> m_hiddenIds = new HashSet<Integer>();
    /**
     * This contains the attribute labels - this map is indexed by
     * attribute_id(s)
     */
    private final Map<Integer, String> m_labelMap = new HashMap<Integer, String>();

    /**
     * Is this attribute_id hidden?
     *
     * @param id
     * @return true if this attribute id is hidden
     */
    public boolean isAttributeHidden(final Integer id) {
        return this.m_hiddenIds.contains(id);
    }

    /**
     * Get the attribute label for this attribute_id.
     *
     * @param id
     * @return the attribute id's label
     */
    public String getAttributeLabel(final Integer id) {
        final String label = this.m_labelMap.get(id);
        return (label == null) ? "" : label;
    }

    /**
     * Add attribute.
     *
     * @param id
     * @param hiddenFlag
     * @param label
     */
    public void addAttribute(final Integer id, final boolean hiddenFlag, final String label) {
        if (hiddenFlag) {
            this.m_hiddenIds.add(id);
        }
        if (label != null) {
            this.m_labelMap.put(id, label);
        }
    }
}
