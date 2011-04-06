package ca.chaves.familyBrowser.database;

import ca.chaves.familyBrowser.helpers.Log;
import ca.chaves.familyBrowser.helpers.NodeAttributes;
import ca.chaves.familyBrowser.helpers.NodeObject;

/**
 * This class is a database-level controller. It implements a double-buffer,
 * where all user-interface views use one of these buffers, and all asynchronous
 * database operations use the other one. When the database operation finishes,
 * both buffers are swapped and the old user-interface buffer becomes available
 * for future database operations.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public abstract class NodeController {

    protected static final String TAG = NodeController.class.getSimpleName();

    /** This is the database factory */
    private final DatabaseFactory m_databaseFactory;

    /** This is the user-interface buffer */
    private NodeObjectBuffer m_userBuffer;
    /** This is the buffer available for database operations */
    private NodeObjectBuffer m_idleBuffer;

    /** This is the NodeAttributes data */
    private NodeAttributes m_nodeAttributes;

    /**
     * Constructor.
     *
     * @param databaseFactory
     */
    public NodeController(final DatabaseFactory databaseFactory) {
        this.m_databaseFactory = databaseFactory;

        // create the double-buffer
        this.m_userBuffer = new NodeObjectBuffer(this, databaseFactory);
        this.m_idleBuffer = new NodeObjectBuffer(this, databaseFactory);

        // automatically load the node attributes
        final NodeAttributesBuffer dataBuffer = new NodeAttributesBuffer(this, databaseFactory);
        dataBuffer.load();
    }

    /**
     * Close all database instances.
     */
    public synchronized void closeDatabases() {
        this.m_databaseFactory.closeDatabases();
    }

    /**
     * @return the current user-interface buffer
     */
    public synchronized NodeObject getUserNode() {
        return this.m_userBuffer;
    }

    /**
     * @return the NodeAttributes buffer
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
     * This call-back is called by the asynchronous tasks when loading the
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
     * This call-back is called by the asynchronous tasks when loading the
     * NodeObject buffer is complete.
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
            Log.e(TAG, "nodeAttributes are not available");
            return;
        }
        // swap buffers, then start all Database AsyncTasks
        final NodeObjectBuffer dataBuffer = this.m_idleBuffer;
        this.m_idleBuffer = null;
        if (dataBuffer == null) {
            // there is another i/o request in progress - ignore this one
            Log.d(TAG, "doStartUserInterface ignored");
            return;
        }
        // start new i/o task
        dataBuffer.load(newId, newName, this.m_nodeAttributes);
    }

    /**
     * This call-back is called by the asynchronous tasks when the data buffer
     * loading is complete. This function should refresh the user-interface
     * views with the new data.
     */
    abstract protected void onLoadCompleteRefreshUserInterface(final NodeObject node);
}
