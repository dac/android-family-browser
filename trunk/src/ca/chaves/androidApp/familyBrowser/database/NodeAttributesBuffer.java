package ca.chaves.androidApp.familyBrowser.database;

import ca.chaves.androidApp.familyBrowser.helpers.DatabaseFactory;
import ca.chaves.androidApp.familyBrowser.helpers.DatabaseReadTask;
import ca.chaves.androidApp.familyBrowser.helpers.NodeAttributes;

import android.database.Cursor;

/**
 * The class NodeAttributesBuffer is a NodeAttributes record that knows how to
 * load its data from a database.
 * 
 * @author david@chaves.ca
 */
class NodeAttributesBuffer {
    
    /** Data buffer */
    private final NodeAttributes m_nodeAttributes = new NodeAttributes();
    
    /** Database controller */
    private final NodeController m_controller;
    /** Database factory */
    private final DatabaseFactory m_databaseFactory;
    
    /**
     * Constructor.
     * 
     * @param controller
     * @param databaseFactory
     */
    public NodeAttributesBuffer(final NodeController controller, final DatabaseFactory databaseFactory) {
        this.m_controller = controller;
        this.m_databaseFactory = databaseFactory;
    }
    
    /**
     * Start loading the data buffer in background. The asynchronous task will
     * call onLoadComplete() when it finishes loading all data.
     */
    public void load() {
        
        // create loader task
        
        final DatabaseReadTask nodeAttributesLoader = createNodeAttributesLoader();
        
        // start the execution of loader task
        
        nodeAttributesLoader.execute();
    }
    
    /**
     * Create the asynchronous task that loads the data buffer.
     * 
     * @return the asynchronous task
     */
    private DatabaseReadTask createNodeAttributesLoader() {
        synchronized (this) {
            final String selectStmt = NodeModel.SELECT_ATTRIBUTES_SQL;
            return new DatabaseReadTask(this.m_databaseFactory, selectStmt, null) {
                @Override
                protected void doProcessDatabaseResult(Cursor cursor) {
                    NodeAttributesBuffer.this.loadNodeAttributesData(cursor);
                }
                
                @Override
                protected void doRefreshUserInterface() {
                    NodeAttributesBuffer.this.onLoadComplete();
                }
            };
        }
    }
    
    /**
     * Load the data buffer from the database cursor.
     * 
     * @param cursor
     */
    void loadNodeAttributesData(final Cursor cursor) {
        synchronized (this) {
            if (cursor.moveToFirst()) {
                do {
                    final Integer attributeId = new Integer(cursor.getInt(0));
                    final boolean hiddenFlag = (cursor.getInt(1) != 0);
                    final String attributeValue = cursor.getString(2);
                    this.m_nodeAttributes.addAttribute(attributeId, hiddenFlag, attributeValue);
                }
                while (cursor.moveToNext());
            }
        }
    }
    
    /**
     * Call-back called by the asynchronous tasks when the data buffer loading
     * is complete.
     */
    void onLoadComplete() {
        synchronized (this) {
            // that's all folks - all tasks are complete!
            this.m_controller.onLoadingNodeAttributesComplete(this.m_nodeAttributes);
        }
    }
}
