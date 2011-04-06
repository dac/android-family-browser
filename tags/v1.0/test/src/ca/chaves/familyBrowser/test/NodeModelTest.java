package ca.chaves.familyBrowser.test;

import junit.framework.Assert;
import ca.chaves.familyBrowser.activities.BrowserActivity;
import ca.chaves.familyBrowser.database.NodeModel;
import ca.chaves.familyBrowser.helpers.NodeAttributes;
import ca.chaves.familyBrowser.helpers.NodeObject;
import ca.chaves.familyBrowser.helpers.NodeValueList;
import ca.chaves.familyBrowser.test.database.DatabaseAccessSample;
import ca.chaves.familyBrowser.test.database.SampleData;
import ca.chaves.familyBrowser.test.helpers.NodeSample;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Test class NodeModel.
 * 
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeModelTest extends ActivityInstrumentationTestCase2<BrowserActivity> {
    
    protected static final String TAG = NodeModelTest.class.getSimpleName();
    
    private BrowserActivity m_activity;
    private DatabaseAccessSample m_databaseAccess;
    
    public NodeModelTest() {
        super("ca.chaves.familyBrowser", BrowserActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.m_activity = getActivity();
        this.m_databaseAccess = new DatabaseAccessSample(this.m_activity);
    }
    
    @Override
    protected void tearDown() throws Exception {
        this.m_databaseAccess.closeDatabases();
        super.tearDown();
    }
    
    public void test_not_nulls() throws Throwable {
        Assert.assertNotNull(this.m_activity);
        Assert.assertNotNull(this.m_databaseAccess);
    }
    
    public void test_node_attributes() throws Throwable {
        final NodeAttributes nodeAttributes = this.m_databaseAccess.loadNodeAttributes();
        Assert.assertNotNull(nodeAttributes);
        // the "name" attribute must be hidden
        final Integer NAME_ATTRIBUTE_ID = new Integer(NodeModel.NAME_ATTRIBUTE_ID);
        Assert.assertTrue(nodeAttributes.isAttributeHidden(NAME_ATTRIBUTE_ID));
    }
    
    public void test_node_values() throws Throwable {
        final NodeSample defaultNode = SampleData.getDefaultNode();
        Assert.assertNotNull(defaultNode);
        final NodeObject nodeObject = this.m_databaseAccess.loadNodeObject(NodeModel.DEFAULT_NODE_ID);
        Assert.assertNotNull(nodeObject);
        Assert.assertEquals(defaultNode.getNodeId(), nodeObject.getNodeId());
        // WARNING: .loadNodeObject() does not call .setNodeName()
        // therefore, nodeObject.getNodeName() is always null
        Assert.assertNull(nodeObject.getNodeName());
        final NodeValueList attributeList = nodeObject.getNodeListValues(NodeObject.LIST_ATTRIBUTES);
        Assert.assertNotNull(attributeList);
        final Integer NAME_ATTRIBUTE_ID = new Integer(NodeModel.NAME_ATTRIBUTE_ID);
        final String nameValue = attributeList.findString(NAME_ATTRIBUTE_ID);
        Assert.assertNotNull(nameValue);
        // next test will really check for a valid nodeName in nodeObject
        Assert.assertEquals(defaultNode.getNodeName(), nameValue);
    }
}
