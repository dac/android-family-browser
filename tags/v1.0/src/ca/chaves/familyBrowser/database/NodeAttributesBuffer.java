package ca.chaves.familyBrowser.database;

import ca.chaves.familyBrowser.helpers.NodeAttributes;

import android.database.Cursor;

/**
 * This class is a NodeAttributes that knows how to load itself from a database,
 * asynchronously.
 *
 * @note this class has no synchronized blocks because it has only one
 *       asynchronous task.
 * @author "David Chaves <david@chaves.ca>"
 */
class NodeAttributesBuffer {

    /** This is the database controller */
    private final NodeController m_nodeController;
    /** This is the database factory */
    private final DatabaseFactory m_databaseFactory;

    /** This is the data buffer */
    private final NodeAttributes m_nodeAttributes = new NodeAttributes();

    /**
     * Constructor.
     *
     * @param nodeController
     * @param databaseFactory
     */
    public NodeAttributesBuffer(final NodeController nodeController, final DatabaseFactory databaseFactory) {
        this.m_nodeController = nodeController;
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
        final String selectStmt = NodeModel.SELECT_ATTRIBUTES_SQL;
        return new DatabaseReadTask(this.m_databaseFactory, selectStmt, null) {
            @Override
            protected void doProcessDatabaseResult(final Cursor cursor) {
                NodeAttributesBuffer.this.loadNodeAttributesData(cursor);
            }

            @Override
            protected void doRefreshUserInterface() {
                NodeAttributesBuffer.this.onLoadComplete();
            }
        };
    }

    /**
     * Load the data buffer from the database cursor.
     *
     * @param cursor
     */
    void loadNodeAttributesData(final Cursor cursor) {
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

    /**
     * This call-back is called by the asynchronous tasks when the data buffer
     * loading is complete.
     */
    void onLoadComplete() {
        // that's all folks - all tasks are complete!
        this.m_nodeController.onLoadingNodeAttributesComplete(this.m_nodeAttributes);
    }
}
