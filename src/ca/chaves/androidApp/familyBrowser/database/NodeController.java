package ca.chaves.androidApp.familyBrowser.database;

import ca.chaves.androidApp.familyBrowser.helpers.DatabaseFactory;
import ca.chaves.androidApp.familyBrowser.helpers.Log;
import ca.chaves.androidApp.familyBrowser.helpers.NodeAttributes;
import ca.chaves.androidApp.familyBrowser.helpers.NodeObject;

/**
 * The class NodeController implements a double-buffer, where all user-interface
 * views use one of these buffers, and all asynchronous database operations use
 * the other one. When the database operation finishes, both buffers are swapped
 * and the old user-interface buffer becomes available for future database
 * operations.
 * 
 * @author david@chaves.ca
 */
public abstract class NodeController {
    
    protected static final String TAG = NodeController.class.getSimpleName();
    
    /** User-interface buffer */
    private NodeObjectBuffer m_userBuffer;
    /** Available database buffer */
    private NodeObjectBuffer m_idleBuffer;
    
    /** NodeAttributes buffer */
    private NodeAttributes m_nodeAttributes;
    
    /**
     * Constructor.
     * 
     * @param databaseFactory
     */
    public NodeController(final DatabaseFactory databaseFactory) {
        // create the double-buffer
        this.m_userBuffer = new NodeObjectBuffer(this, databaseFactory);
        this.m_idleBuffer = new NodeObjectBuffer(this, databaseFactory);
        
        // automatically load the node attributes
        final NodeAttributesBuffer dataBuffer = new NodeAttributesBuffer(this, databaseFactory);
        dataBuffer.load();
    }
    
    /**
     * Return the current user-interface buffer
     * 
     * @return NodeObject buffer
     */
    public synchronized NodeObject getUserNode() {
        return this.m_userBuffer;
    }
    
    /**
     * Return the NodeAttributes buffer
     * 
     * @return NodeAttributes buffer
     */
    public synchronized NodeAttributes getNodeAttributes() {
        return this.m_nodeAttributes;
    }
    
    /**
     * Refresh the user-interface buffer. This is called once we have created
     * all the screen views, in order to ensure that the user-interface is
     * properly displayed.
     */
    public synchronized void refreshUserInterface() {
        this.onLoadCompleteRefreshUserInterface(this.m_userBuffer);
    }
    
    /**
     * Start loading a new database-buffer in background. The asynchronous tasks
     * will call doRefreshUserInterface() when it finishes loading all data.
     * 
     * @param newId
     * @param newName
     */
    public synchronized void startLoadingNode(final Integer newId, final String newName) {
        this.doStartUserInterface(newId, newName);
    }
    
    /**
     * Call-back called by the asynchronous tasks when loading the
     * NodeAttributes buffer is complete.
     * 
     * @param nodeAttributes
     */
    public synchronized void onLoadingNodeAttributesComplete(final NodeAttributes nodeAttributes) {
        // update the node attributes buffer
        this.m_nodeAttributes = nodeAttributes;
        // automatically load the default node *after* the
        // node attributes buffer had been fully loaded
        this.doStartUserInterface(null, null);
    }
    
    /**
     * Call-back called by the asynchronous tasks when loading the NodeObject
     * buffer is complete.
     * 
     * @param dataBuffer
     */
    public synchronized void onLoadingNodeObjectComplete(final NodeObjectBuffer dataBuffer) {
        this.m_idleBuffer = this.m_userBuffer;
        this.m_userBuffer = dataBuffer;
        // free some memory from the unused buffer, if possible
        this.m_idleBuffer.resetNodeContent();
        // update the user-interface buffer
        this.onLoadCompleteRefreshUserInterface(dataBuffer);
    }
    
    /**
     * Start loading a new database-buffer in background. The asynchronous tasks
     * will call doRefreshUserInterface() when it finishes loading all data.
     * Notice this function is NOT synchronized.
     * 
     * @param newId
     * @param newName
     */
    private void doStartUserInterface(final Integer newId, final String newName) {
        // be sure that we have all the requirements available
        if (this.m_nodeAttributes == null) {
            Log.e(TAG, "nodeAttributes not available");
            return;
        }
        // swap buffers, then start all Database AsyncTasks
        final NodeObjectBuffer dataBuffer = this.m_idleBuffer;
        this.m_idleBuffer = null;
        if (dataBuffer != null) {
            dataBuffer.load(newId, newName, this.m_nodeAttributes);
        }
    }
    
    /**
     * Call-back called by the asynchronous tasks when the data buffer loading
     * is complete. This call-back must refresh the user-interface views with
     * the new data.
     */
    abstract protected void onLoadCompleteRefreshUserInterface(NodeObject node);
}
