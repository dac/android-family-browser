package ca.chaves.androidApp.familyBrowser.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * The class NodeAttributes stores the node attribute's metadata. Each attribute
 * contains a flag indicating if it is a hidden attribute, and a label.
 * @author david@chaves.ca
 */
public class NodeAttributes {
    
    protected static final String TAG = NodeAttributes.class.getSimpleName();
    
    /** Hidden attribute_id(s) */
    private final Set<Integer> m_hiddenAttributeIds = new HashSet<Integer>();
    /** Attribute labels - this map is indexed by attribute_id(s) */
    private final Map<Integer, String> m_labelsMap = new HashMap<Integer, String>();
    
    /**
     * Is this attribute_id hidden?
     * 
     * @param attributeId
     * @return true if this attributeId is hidden
     */
    public boolean isAttributeHidden(final Integer attributeId) {
        return this.m_hiddenAttributeIds.contains(attributeId);
    }
    
    /**
     * Get the attribute label for this attribute_id.
     * 
     * @param attributeId
     * @return the attributeId's label
     */
    public String getAttributeLabel(final Integer attributeId) {
        final String label = this.m_labelsMap.get(attributeId);
        return (label == null) ? "" : label;
    }
    
    /**
     * Add attribute.
     * 
     * @param attributeId
     * @param isHidden
     * @param attributeLabel
     */
    public void addAttribute(final Integer attributeId, final boolean isHidden, final String attributeLabel) {
        if (isHidden) {
            this.m_hiddenAttributeIds.add(attributeId);
        }
        if (attributeLabel != null) {
            this.m_labelsMap.put(attributeId, attributeLabel);
        }
    }
}
