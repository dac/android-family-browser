package ca.chaves.familyBrowser.test.database;

import junit.framework.Assert;
import ca.chaves.familyBrowser.database.DatabaseFactory;
import ca.chaves.familyBrowser.database.NodeDatabase;
import ca.chaves.familyBrowser.database.NodeModel;
import ca.chaves.familyBrowser.helpers.Log;
import ca.chaves.familyBrowser.helpers.NodeAttributes;
import ca.chaves.familyBrowser.helpers.NodeObject;
import ca.chaves.familyBrowser.helpers.NodeValueList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class is a simplified version of NodeController, NodeObjectBrowser and
 * NodeAttributesBuffer all together. It loads data directly from the database,
 * synchronously.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class DatabaseAccessSample {

    protected static final String TAG = DatabaseAccessSample.class.getSimpleName();

    /** This is the database factory */
    private final DatabaseFactory m_databaseFactory;

    /**
     * Constructor.
     *
     * @param activity
     */
    public DatabaseAccessSample(final Activity activity) {
        this.m_databaseFactory = NodeDatabase.getDatabaseFactory(activity);
        Assert.assertNotNull(this.m_databaseFactory);
    }

    /**
     * Close all database instances.
     */
    public synchronized void closeDatabases() {
        this.m_databaseFactory.closeDatabases();
    }

    /**
     * Load the NodeAttributes from the database.
     *
     * @return NodeAttributes
     */
    public NodeAttributes loadNodeAttributes() {
        final NodeAttributes nodeAttributes = new NodeAttributes();
        Log.d(TAG, NodeModel.SELECT_ATTRIBUTES_SQL);
        final SQLiteDatabase database = this.m_databaseFactory.getDatabase();
        final Cursor cursor = database.rawQuery(NodeModel.SELECT_ATTRIBUTES_SQL, null);
        if (cursor.moveToFirst()) {
            do {
                final Integer attributeId = new Integer(cursor.getInt(0));
                final boolean hiddenFlag = (cursor.getInt(1) != 0);
                final String attributeValue = cursor.getString(2);
                Log.d(TAG, "attribute #" + attributeId + " = [" + attributeValue + "]");
                nodeAttributes.addAttribute(attributeId, hiddenFlag, attributeValue);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return nodeAttributes;
    }

    /**
     * This internal function loads NodeValueList from the database.
     *
     * @param nodeObject
     * @param listIndex
     * @param selectStmt
     */
    private void loadNodeValueList(final NodeObject nodeObject, final int listIndex, final String selectStmt) {
        Assert.assertNotNull(nodeObject);
        Assert.assertTrue(0 <= listIndex);
        Assert.assertTrue(listIndex < NodeObject.LIST_MAX);
        Assert.assertNotNull(selectStmt);
        final NodeValueList valueList = nodeObject.getNodeListValues(listIndex);
        final String[] selectArgs = new String[] {
            nodeObject.getNodeId().toString()
        };
        Log.d(TAG, selectStmt);
        final SQLiteDatabase database = this.m_databaseFactory.getDatabase();
        final Cursor cursor = database.rawQuery(selectStmt, selectArgs);
        if (cursor.moveToFirst()) {
            do {
                final Integer attributeId = new Integer(cursor.getInt(0));
                final String attributeValue = cursor.getString(1);
                Log.d(TAG, valueList.getName() + " #" + attributeId + " = [" + attributeValue + "]");
                valueList.addValue(attributeId, attributeValue);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }

    /**
     * Load the NodeObject from the database, given the 'nodeId' value
     *
     * @param nodeId
     * @return NodeObject
     */
    public NodeObject loadNodeObject(final int nodeId) {
        Assert.assertTrue(nodeId > 0);
        final NodeObject nodeObject = new NodeObject();
        nodeObject.setNodeId(new Integer(nodeId));
        this.loadNodeValueList(nodeObject, NodeObject.LIST_ATTRIBUTES, NodeModel.SELECT_NODE_ATTRIBUTE_SQL);
        this.loadNodeValueList(nodeObject, NodeObject.LIST_PARENTS, NodeModel.SELECT_NODE_PARENTS_SQL);
        this.loadNodeValueList(nodeObject, NodeObject.LIST_PARTNERS, NodeModel.SELECT_NODE_PARTNERS_SQL);
        this.loadNodeValueList(nodeObject, NodeObject.LIST_CHILDREN, NodeModel.SELECT_NODE_CHILDREN_SQL);
        this.loadNodeValueList(nodeObject, NodeObject.LIST_SIBLINGS, NodeModel.SELECT_NODE_SIBLINGS_SQL);
        return nodeObject;
    }
}
