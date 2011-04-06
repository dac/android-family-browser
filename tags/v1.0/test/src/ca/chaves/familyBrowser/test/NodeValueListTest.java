package ca.chaves.familyBrowser.test;

import junit.framework.Assert;
import ca.chaves.familyBrowser.helpers.NodeValueList;

import android.test.AndroidTestCase;

/**
 * Test class NodeValueList.
 * 
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeValueListTest extends AndroidTestCase {
    
    public void test_empty_list() throws Throwable {
        final NodeValueList list = new NodeValueList("blah");
        Assert.assertEquals("blah", list.getName());
        Assert.assertEquals(0, list.getLength());
    }
    
    private static final Integer nueve = new Integer(9);
    private static final Integer ocho = new Integer(8);
    private static final Integer siete = new Integer(7);
    
    private NodeValueList create_987_list() {
        final NodeValueList list = new NodeValueList("one");
        list.addValue(nueve, "Nueve");
        list.addValue(ocho, "Ocho");
        list.addValue(siete, "Siete");
        return list;
    }
    
    public void test_987_length() throws Throwable {
        final NodeValueList list = create_987_list();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.getLength() == 3);
        list.clearValues();
        Assert.assertTrue(list.getLength() == 0);
    }
    
    public void test_987_content() throws Throwable {
        final NodeValueList list = create_987_list();
        Assert.assertEquals(nueve, list.getId(0));
        Assert.assertEquals("Nueve", list.getString(0));
        Assert.assertEquals("Nueve", list.findString(nueve));
        Assert.assertEquals(nueve, list.findId("Nueve"));
        Assert.assertEquals(ocho, list.getId(1));
        Assert.assertEquals("Ocho", list.getString(1));
        Assert.assertEquals("Ocho", list.findString(ocho));
        Assert.assertEquals(ocho, list.findId("Ocho"));
        Assert.assertEquals(siete, list.getId(2));
        Assert.assertEquals("Siete", list.getString(2));
        Assert.assertEquals("Siete", list.findString(siete));
        Assert.assertEquals(siete, list.findId("Siete"));
    }
    
    public void test_987_clearValues() throws Throwable {
        final NodeValueList list = create_987_list();
        Assert.assertTrue(list.getLength() == 3);
        Assert.assertNotNull(list.getId(0));
        Assert.assertNotNull(list.getId(1));
        Assert.assertNotNull(list.getId(2));
        Assert.assertNotNull(list.getString(0));
        Assert.assertNotNull(list.getString(1));
        Assert.assertNotNull(list.getString(2));
        list.clearValues();
        Assert.assertTrue(list.getLength() == 0);
        Assert.assertNull(list.getId(0));
        Assert.assertNull(list.getId(1));
        Assert.assertNull(list.getId(2));
        Assert.assertNull(list.getString(0));
        Assert.assertNull(list.getString(1));
        Assert.assertNull(list.getString(2));
    }
}
