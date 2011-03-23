package ca.chaves.familyBrowser.helpers;

/**
 * This class stores a list of tuples (id, string). It is used to store either
 * the node attribute(s) or the node parent/partner/child/sibling lists, for a
 * given node_id. Notice that the node_id is not stored here. This class is
 * implemented to minimize memory allocations, which are expensive in Android.
 *
 * @warning out-of-range-index errors are not always detected!
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeValueList {

    protected static final String TAG = NodeValueList.class.getSimpleName();

    /** This is the list name */
    private final String m_name;

    /** This is the (logical) list length */
    private int m_length;
    /** This is the list of id(s) */
    private Integer[] m_ids = new Integer[16];
    /** This is the list of string(s) */
    private String[] m_values = new String[16];

    /**
     * Constructor.
     *
     * @param name
     */
    public NodeValueList(final String name) {
        this.m_name = name;
    }

    /**
     * Get the list name.
     *
     * @return list name
     */
    public String getName() {
        return this.m_name;
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
    public Integer getId(final int index) {
        return this.m_ids[index];
    }

    /**
     * Get the string stored at position 'index'.
     *
     * @param index
     *            is a number between 0 and (length-1).
     * @return the string at position 'index'.
     */
    public String getString(final int index) {
        return this.m_values[index];
    }

    /**
     * Get the string with the given 'id'.
     *
     * @param id
     * @return the string associated to the given 'id'
     */
    public String findString(final Integer id) {
        for (int index = 0; index < this.m_length; ++index) {
            final Integer current = this.m_ids[index];
            if (current != null && current.equals(id)) {
                return this.m_values[index];
            }
        }
        return null;
    }

    /**
     * Get the id with the given 'string' value.
     *
     * @param string
     * @return the id associated to the given 'string'
     */
    public Integer findId(final String string) {
        for (int index = 0; index < this.m_length; ++index) {
            final String current = this.m_values[index];
            if (current != null && current.equals(string)) {
                return this.m_ids[index];
            }
        }
        return null;
    }

    /**
     * Append pair (id, string)
     *
     * @param id
     * @param string
     */
    public void addValue(final Integer id, final String string) {
        // expand internal lists if needed
        if (this.m_ids.length <= this.m_length) {
            this.m_ids = (Integer[]) Utils.resizeArray(this.m_ids, this.m_length + 1);
        }
        if (this.m_values.length <= this.m_length) {
            this.m_values = (String[]) Utils.resizeArray(this.m_values, this.m_length + 1);
        }
        // store new id/value at the end of our lists
        this.m_ids[this.m_length] = id;
        this.m_values[this.m_length] = string;
        // now, we have one more element in our internal lists
        ++this.m_length;
    }

    /**
     * Clear this instance. Its [logical] length becomes 0.
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
