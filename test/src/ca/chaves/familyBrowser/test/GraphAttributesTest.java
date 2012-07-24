package ca.chaves.familyBrowser.test;

import ca.chaves.android.graph.GraphAttributes;

import android.test.AndroidTestCase;

import junit.framework.Assert;

/**
 * Test for the {@link GraphAttributes} class.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class GraphAttributesTest
    extends AndroidTestCase
{
    private static final Integer NUEVE = Integer.valueOf( 9 );

    private static final Integer OCHO = Integer.valueOf( 8 );

    private static final Integer SIETE = Integer.valueOf( 7 );

    // ----------
    // Unit Tests
    // ----------

    /**
     * Test function.
     */
    public void testAttributes()
    {
        final GraphAttributes attributes = new GraphAttributes();
        attributes.addAttribute( GraphAttributesTest.NUEVE, true, "Nueve" );
        attributes.addAttribute( GraphAttributesTest.OCHO, false, "Ocho" );
        // NOTE we do not call a.addAttribute(siete, ...)
        Assert.assertTrue( attributes.isHiddingAttributeId( GraphAttributesTest.NUEVE ) );
        Assert.assertFalse( attributes.isHiddingAttributeId( GraphAttributesTest.OCHO ) );
        Assert.assertFalse( attributes.isHiddingAttributeId( GraphAttributesTest.SIETE ) );
        Assert.assertEquals( "Nueve", attributes.getAttributeLabel( GraphAttributesTest.NUEVE ) );
        Assert.assertEquals( "Ocho", attributes.getAttributeLabel( GraphAttributesTest.OCHO ) );
        Assert.assertEquals( "", attributes.getAttributeLabel( GraphAttributesTest.SIETE ) );
    }
}
