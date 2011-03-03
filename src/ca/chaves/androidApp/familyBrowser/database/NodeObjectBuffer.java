package ca.chaves.androidApp.familyBrowser.database;

import ca.chaves.androidApp.familyBrowser.helpers.DatabaseFactory;
import ca.chaves.androidApp.familyBrowser.helpers.DatabaseReadTask;
import ca.chaves.androidApp.familyBrowser.helpers.NodeAttributes;
import ca.chaves.androidApp.familyBrowser.helpers.NodeValueList;
import ca.chaves.androidApp.familyBrowser.helpers.NodeObject;

import android.database.Cursor;

/**
 * The class NodeObjectBuffer is a NodeObject record that knows how to load its
 * data from a database.
 * 
 * @author david@chaves.ca
 */
class NodeObjectBuffer extends NodeObject {
    
    /** Database controller */
    private final NodeController m_controller;
    /** Database factory */
    private final DatabaseFactory m_databaseFactory;
    
    /** NodeAttributes buffer */
    private NodeAttributes m_nodeAttributes;
    /**
     * We need several asynchronous tasks to fully load the data buffer. We use
     * this variable to find out when all these tasks are complete, since
     * m_stepsToComplete counts how many tasks are still in progress.
     */
    private int m_stepsToComplete;
    
    /**
     * Constructor.
     * 
     * @param controller
     * @param databaseFactory
     */
    public NodeObjectBuffer(final NodeController controller, final DatabaseFactory databaseFactory) {
        this.m_controller = controller;
        this.m_databaseFactory = databaseFactory;
    }
    
    /**
     * Start loading the data buffer in background. The asynchronous task will
     * call onLoadComplete() when it finishes loading all data.
     * 
     * @param newId
     * @param newName
     * @param nodeAttributes
     */
    public void load(final Integer newId, final String newName, final NodeAttributes nodeAttributes) {
        
        this.m_nodeAttributes = nodeAttributes;
        
        this.m_stepsToComplete = 1; // there will be a final step to
                                    // complete, where we notify the
                                    // controller that this buffer is ready
                                    // for use
        
        // reset buffer contents
        
        super.setNodeId(newId);
        super.setNodeName(newName);
        
        // create loader tasks - all tasks need to be created BEFORE any of them
        // is executed, in order to have a reliable "steps to complete" counter
        
        Integer id = newId;
        if (id == null) {
            id = new Integer(NodeModel.DEFAULT_NODE_ID);
        }
        final String[] selectArgs = new String[] {
            id.toString()
        };
        
        final DatabaseReadTask attributesLoader = createAttributesLoader(selectArgs);
        
        final DatabaseReadTask parentsLoader = createParentsLoader(selectArgs);
        final DatabaseReadTask partnersLoader = createPartnersLoader(selectArgs);
        final DatabaseReadTask childrenLoader = createChildrenLoader(selectArgs);
        final DatabaseReadTask siblingsLoader = createSiblingsLoader(selectArgs);
        
        // start the execution of all the loader tasks
        
        attributesLoader.execute();
        
        parentsLoader.execute();
        partnersLoader.execute();
        childrenLoader.execute();
        siblingsLoader.execute();
    }
    
    /**
     * Create the asynchronous task that loads the node attributes.
     * 
     * @return the asynchronous task
     */
    private DatabaseReadTask createAttributesLoader(final String[] selectArgs) {
        synchronized (this) {
            ++this.m_stepsToComplete;
            final String selectStmt = NodeModel.SELECT_NODE_ATTRIBUTE_SQL;
            return new DatabaseReadTask(this.m_databaseFactory, selectStmt, selectArgs) {
                @Override
                protected void doProcessDatabaseResult(Cursor cursor) {
                    NodeObjectBuffer.this.loadNodeAttributeListData(NodeObject.LIST_ATTRIBUTES, cursor);
                }
                
                @Override
                protected void doRefreshUserInterface() {
                    NodeObjectBuffer.this.onLoadComplete();
                }
            };
        }
    }
    
    /**
     * Create the asynchronous task that loads the node's parents list.
     * 
     * @return the asynchronous task
     */
    private DatabaseReadTask createParentsLoader(final String[] selectArgs) {
        synchronized (this) {
            ++this.m_stepsToComplete;
            final String selectStmt = NodeModel.SELECT_NODE_PARENTS_SQL;
            return new DatabaseReadTask(this.m_databaseFactory, selectStmt, selectArgs) {
                @Override
                protected void doProcessDatabaseResult(Cursor cursor) {
                    NodeObjectBuffer.this.loadNodeListValuesData(NodeObject.LIST_PARENTS, cursor);
                }
                
                @Override
                protected void doRefreshUserInterface() {
                    NodeObjectBuffer.this.onLoadComplete();
                }
            };
        }
    }
    
    /**
     * Create the asynchronous task that loads the node's partners list.
     * 
     * @return the asynchronous task
     */
    private DatabaseReadTask createPartnersLoader(final String[] selectArgs) {
        synchronized (this) {
            ++this.m_stepsToComplete;
            final String selectStmt = NodeModel.SELECT_NODE_PARTNERS_SQL;
            return new DatabaseReadTask(this.m_databaseFactory, selectStmt, selectArgs) {
                @Override
                protected void doProcessDatabaseResult(Cursor cursor) {
                    NodeObjectBuffer.this.loadNodeListValuesData(NodeObject.LIST_PARTNERS, cursor);
                }
                
                @Override
                protected void doRefreshUserInterface() {
                    NodeObjectBuffer.this.onLoadComplete();
                }
            };
        }
    }
    
    /**
     * Create the asynchronous task that loads the node's children list.
     * 
     * @return the asynchronous task
     */
    private DatabaseReadTask createChildrenLoader(final String[] selectArgs) {
        synchronized (this) {
            ++this.m_stepsToComplete;
            final String selectStmt = NodeModel.SELECT_NODE_CHILDREN_SQL;
            return new DatabaseReadTask(this.m_databaseFactory, selectStmt, selectArgs) {
                @Override
                protected void doProcessDatabaseResult(Cursor cursor) {
                    NodeObjectBuffer.this.loadNodeListValuesData(NodeObject.LIST_CHILDREN, cursor);
                }
                
                @Override
                protected void doRefreshUserInterface() {
                    NodeObjectBuffer.this.onLoadComplete();
                }
            };
        }
    }
    
    /**
     * Create the asynchronous task that loads the node's siblings list.
     * 
     * @return the asynchronous task
     */
    private DatabaseReadTask createSiblingsLoader(final String[] selectArgs) {
        synchronized (this) {
            ++this.m_stepsToComplete;
            final String selectStmt = NodeModel.SELECT_NODE_SIBLINGS_SQL;
            return new DatabaseReadTask(this.m_databaseFactory, selectStmt, selectArgs) {
                @Override
                protected void doProcessDatabaseResult(Cursor cursor) {
                    NodeObjectBuffer.this.loadNodeListValuesData(NodeObject.LIST_SIBLINGS, cursor);
                }
                
                @Override
                protected void doRefreshUserInterface() {
                    NodeObjectBuffer.this.onLoadComplete();
                }
            };
        }
    }
    
    /**
     * Load the node attributes from the database cursor. The "name" attributes
     * is special, since it is used to display the "current node" title view.
     * 
     * @param cursor
     */
    void loadNodeAttributeListData(final int listIndex, final Cursor cursor) {
        synchronized (this) {
            final NodeValueList listValues = super.getNodeListValues(listIndex);
            if (cursor.moveToFirst()) {
                do {
                    final Integer attributeId = new Integer(cursor.getInt(0));
                    final String attributeValue = cursor.getString(1);
                    // load the "name" attribute, just in case none was given
                    if (attributeId.intValue() == NodeModel.NAME_ATTRIBUTE_ID) {
                        super.setNodeName(attributeValue);
                    }
                    // filter-out all hidden attributes
                    if (this.m_nodeAttributes.isAttributeHidden(attributeId)) {
                        continue;
                    }
                    // add attributeId and attributeValue
                    listValues.addValue(attributeId, attributeValue);
                }
                while (cursor.moveToNext());
            }
            // mark another step completed
            --this.m_stepsToComplete;
        }
    }
    
    /**
     * Load the node parents/partners/children/siblings lists from the database
     * cursor.
     * 
     * @param cursor
     */
    void loadNodeListValuesData(final int listIndex, final Cursor cursor) {
        synchronized (this) {
            final NodeValueList listValues = super.getNodeListValues(listIndex);
            if (cursor.moveToFirst()) {
                do {
                    final Integer nodeId = new Integer(cursor.getInt(0));
                    final String nodeName = cursor.getString(1);
                    listValues.addValue(nodeId, nodeName);
                }
                while (cursor.moveToNext());
            }
            // mark another step completed
            --this.m_stepsToComplete;
        }
    }
    
    /**
     * Call-back called by the asynchronous tasks when the data buffer loading
     * is complete.
     */
    void onLoadComplete() {
        synchronized (this) {
            // there is only one step [this one] left to complete?
            if (1 != this.m_stepsToComplete) {
                return;
            }
            // mask the last step completed as well
            --this.m_stepsToComplete;
            
            // that's all folks - all tasks are complete!
            // Log.d(TAG, "load complete");
            this.m_controller.onLoadingNodeObjectComplete(this);
        }
    }
}
