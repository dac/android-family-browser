package ca.chaves.familyBrowser.test;

import junit.framework.Assert;
import ca.chaves.familyBrowser.helpers.NodeAttributes;

import android.test.AndroidTestCase;

/**
 * Test class NodeAttributes.
 * 
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeAttributesTest extends AndroidTestCase {
    
    private static final Integer nueve = new Integer(9);
    private static final Integer ocho = new Integer(8);
    private static final Integer siete = new Integer(7);
    
    public void test_attributes() throws Throwable {
        final NodeAttributes a = new NodeAttributes();
        a.addAttribute(nueve, true, "Nueve");
        a.addAttribute(ocho, false, "Ocho");
        // NOTE we do not call a.addAttribute(siete, ...)
        Assert.assertTrue(a.isAttributeHidden(nueve));
        Assert.assertFalse(a.isAttributeHidden(ocho));
        Assert.assertFalse(a.isAttributeHidden(siete));
        Assert.assertEquals("Nueve", a.getAttributeLabel(nueve));
        Assert.assertEquals("Ocho", a.getAttributeLabel(ocho));
        Assert.assertEquals("", a.getAttributeLabel(siete));
    }
}
