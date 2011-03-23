package ca.chaves.familyBrowser.test;

import junit.framework.Assert;
import ca.chaves.familyBrowser.helpers.NodeObject;

import android.test.AndroidTestCase;

/**
 * Test class NodeObject.
 * 
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeObjectTest extends AndroidTestCase {
    
    private static final Integer nueve = new Integer(9);
    
    public void test_id_and_name() throws Throwable {
        final NodeObject node = new NodeObject();
        Assert.assertNull(node.getNodeId());
        Assert.assertNull(node.getNodeName());
        node.setNodeId(nueve);
        node.setNodeName("Nueve");
        Assert.assertEquals(nueve, node.getNodeId());
        Assert.assertEquals("Nueve", node.getNodeName());
        node.resetNodeContent();
        Assert.assertNull(node.getNodeId());
        Assert.assertNull(node.getNodeName());
    }
    
    public void test_listValues() throws Throwable {
        final NodeObject node = new NodeObject();
        
        Assert.assertNotNull(node.getNodeListValues(NodeObject.LIST_ATTRIBUTES));
        Assert.assertNotNull(node.getNodeListValues(NodeObject.LIST_CHILDREN));
        Assert.assertNotNull(node.getNodeListValues(NodeObject.LIST_PARENTS));
        Assert.assertNotNull(node.getNodeListValues(NodeObject.LIST_PARTNERS));
        Assert.assertNotNull(node.getNodeListValues(NodeObject.LIST_SIBLINGS));
        
        for (int index = 0; index < NodeObject.LIST_MAX; ++index) {
            Assert.assertNotNull(node.getNodeListValues(index));
        }
        
        Assert.assertTrue(0 <= NodeObject.LIST_ATTRIBUTES);
        Assert.assertTrue(NodeObject.LIST_ATTRIBUTES < NodeObject.LIST_CHILDREN);
        Assert.assertTrue(NodeObject.LIST_ATTRIBUTES < NodeObject.LIST_PARENTS);
        Assert.assertTrue(NodeObject.LIST_ATTRIBUTES < NodeObject.LIST_PARTNERS);
        Assert.assertTrue(NodeObject.LIST_ATTRIBUTES < NodeObject.LIST_SIBLINGS);
        Assert.assertTrue(NodeObject.LIST_ATTRIBUTES < NodeObject.LIST_MAX);
        
        Assert.assertTrue(0 <= NodeObject.LIST_CHILDREN);
        Assert.assertTrue(NodeObject.LIST_CHILDREN < NodeObject.LIST_MAX);
        
        Assert.assertTrue(0 <= NodeObject.LIST_PARENTS);
        Assert.assertTrue(NodeObject.LIST_PARENTS < NodeObject.LIST_MAX);
        
        Assert.assertTrue(0 <= NodeObject.LIST_PARTNERS);
        Assert.assertTrue(NodeObject.LIST_PARTNERS < NodeObject.LIST_MAX);
        
        Assert.assertTrue(0 <= NodeObject.LIST_SIBLINGS);
        Assert.assertTrue(NodeObject.LIST_SIBLINGS < NodeObject.LIST_MAX);
    }
    
    public void test_list_max() throws Throwable {
        final NodeObject node = new NodeObject();
        try {
            node.getNodeListValues(NodeObject.LIST_MAX);
            Assert.fail("accessing NodeObject.LIST_MAX should throw an exception");
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            Assert.assertTrue(true);
        }
    }
}
