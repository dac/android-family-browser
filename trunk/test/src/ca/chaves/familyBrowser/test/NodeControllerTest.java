package ca.chaves.familyBrowser.test;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;
import ca.chaves.familyBrowser.activities.BrowserActivity;
import ca.chaves.familyBrowser.helpers.NodeObject;
import ca.chaves.familyBrowser.test.database.NodeControllerSample;
import ca.chaves.familyBrowser.test.database.SampleData;
import ca.chaves.familyBrowser.test.helpers.NodeSample;

import android.test.ActivityInstrumentationTestCase2;

/**
 * Test class NodeController.
 * 
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeControllerTest extends ActivityInstrumentationTestCase2<BrowserActivity> {
    
    protected static final String TAG = NodeControllerTest.class.getSimpleName();
    
    private BrowserActivity m_activity;
    private NodeControllerSample m_controller;
    
    public NodeControllerTest() {
        super("ca.chaves.familyBrowser", BrowserActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.m_activity = getActivity();
        this.m_controller = new NodeControllerSample(this.m_activity);
    }
    
    @Override
    protected void tearDown() throws Exception {
        this.m_controller.closeDatabases();
        super.tearDown();
    }
    
    public void test_not_nulls() throws Throwable {
        Assert.assertNotNull(this.m_activity);
        Assert.assertNotNull(this.m_controller);
    }
    
    public void test_default_node() throws Throwable {
        final NodeSample defaultNode = SampleData.getDefaultNode();
        Assert.assertNotNull(defaultNode);
        final NodeObject nodeObject = this.m_controller.loadNodeObject(defaultNode.getNodeId());
        Assert.assertNotNull(nodeObject);
        Assert.assertEquals(defaultNode.getNodeId(), nodeObject.getNodeId());
        Assert.assertEquals(defaultNode.getNodeName(), nodeObject.getNodeName());
    }
    
    public void test_sample_tree() throws Throwable {
        // load default data
        final NodeSample rootNode = SampleData.getSampleTree();
        Assert.assertNotNull(rootNode);
        // create queue - we use a non-recursive depth-first tree walk
        final List<NodeSample> visitingQueue = new LinkedList<NodeSample>();
        visitingQueue.add(rootNode);
        // non-recursive walk hierarchy
        while (!visitingQueue.isEmpty()) {
            // get next node to visit
            final NodeSample currentNode = visitingQueue.remove(0);
            Assert.assertNotNull(currentNode);
            // load node_id from the database
            final NodeObject nodeObject = this.m_controller.loadNodeObject(currentNode.getNodeId());
            Assert.assertNotNull(nodeObject);
            Assert.assertEquals(currentNode.getNodeId(), nodeObject.getNodeId());
            Assert.assertEquals(currentNode.getNodeName(), nodeObject.getNodeName());
            // add node links to the visiting queue list
            currentNode.addToVisitingQueue(nodeObject, visitingQueue);
        }
    }
}
