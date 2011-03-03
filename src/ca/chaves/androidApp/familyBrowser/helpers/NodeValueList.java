package ca.chaves.androidApp.familyBrowser.helpers;

/**
 * The class NodeValueList stores a list of tuples (id, string). This class is
 * used to store either the node attribute(s) or the node
 * parent/partner/child/sibling lists, for a given node_id. Notice that the
 * node_id is not stored here.
 * 
 * @author david@chaves.ca
 */
public class NodeValueList {
    
    /** List name */
    private final String m_listName;
    
    /** Logical list length */
    private int m_length;
    /** List of id(s) */
    private Integer[] m_ids = new Integer[16];
    /** List of string(s) */
    private String[] m_values = new String[16];
    
    /**
     * Constructor.
     * 
     * @param listName
     */
    public NodeValueList(final String listName) {
        this.m_listName = listName;
    }
    
    /**
     * Get the list name.
     * 
     * @return list name
     */
    public String getListName() {
        return this.m_listName;
    }
    
    /**
     * Get the list length.
     * 
     * @return list size
     */
    public int getLength() {
        return this.m_length;
    }
    
    /**
     * Get the id stored at position 'index'.
     * 
     * @param index
     *            a number between 0 and (length-1).
     * @return the id as an Integer instance.
     */
    public Integer getExternalId(final int index) {
        return this.m_ids[index];
    }
    
    /**
     * Get the string stored at position 'index'.
     * 
     * @param index
     *            is a number between 0 and (length-1).
     * @return the string at position 'index'.
     */
    public String getStringValue(final int index) {
        return this.m_values[index];
    }
    
    /**
     * Append pair (id, string)
     * 
     * @param externalId
     * @param value
     */
    public void addValue(final Integer externalId, final String value) {
        // expand internal lists if needed
        if (this.m_ids.length <= this.m_length) {
            this.m_ids = (Integer[]) Utils.resizeArray(this.m_ids, this.m_length + 1);
        }
        if (this.m_values.length <= this.m_length) {
            this.m_values = (String[]) Utils.resizeArray(this.m_values, this.m_length + 1);
        }
        // store new id/value at the end of our lists
        this.m_ids[this.m_length] = externalId;
        this.m_values[this.m_length] = value;
        // now, we have one more element in our internal lists
        ++this.m_length;
    }
    
    /**
     * Clear this instance. Its length becomes 0.
     */
    public void clearValues() {
        // free some memory, if possible
        for (int index = 0; index < this.m_length; ++index) {
            this.m_ids[index] = null;
            this.m_values[index] = null;
        }
        // reset the logical size of your internal lists, but
        // we keep the memory allocated for the internal arrays
        this.m_length = 0;
    }
}
