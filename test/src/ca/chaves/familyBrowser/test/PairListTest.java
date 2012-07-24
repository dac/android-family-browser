package ca.chaves.familyBrowser.test;

import ca.chaves.android.util.PairList;
import ca.chaves.familyBrowser.test.util.GraphUtil;

import android.test.AndroidTestCase;

import junit.framework.Assert;

/**
 * Test for the {@link PairList} class.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class PairListTest
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
    public void testNotNulls()
    {
        Assert.assertNotNull( PairListTest.NUEVE );
        Assert.assertNotNull( PairListTest.OCHO );
        Assert.assertNotNull( PairListTest.SIETE );
    }

    /**
     * Test function.
     */
    public void testEmptyList()
    {
        final PairList<Integer, String> list = new PairList<Integer, String>( "blah" );
        Assert.assertNotNull( list );

        Assert.assertEquals( "blah", list.title );
        Assert.assertEquals( 0, list.length );
    }

    /**
     * Test function.
     */
    private PairList<Integer, String> create987List()
    {
        final PairList<Integer, String> list = new PairList<Integer, String>( "one" );
        Assert.assertNotNull( list );

        list.add( PairListTest.NUEVE, "Nueve" );
        list.add( PairListTest.OCHO, "Ocho" );
        list.add( PairListTest.SIETE, "Siete" );
        return list;
    }

    /**
     * Test function.
     */
    public void test987Length()
    {
        final PairList<Integer, String> list = create987List();
        Assert.assertNotNull( list );

        Assert.assertEquals( 3, list.length );
    }

    /**
     * Test function.
     */
    public void test987Content()
    {
        final PairList<Integer, String> list = create987List();
        Assert.assertNotNull( list );

        Assert.assertEquals( PairListTest.NUEVE, list.array_0[0] );
        Assert.assertEquals( "Nueve", list.array_1[0] );
        Assert.assertEquals( "Nueve", GraphUtil.findValueStringById( list, PairListTest.NUEVE ) );
        Assert.assertEquals( PairListTest.NUEVE, GraphUtil.findValueIdByString( list, "Nueve" ) );

        Assert.assertEquals( PairListTest.OCHO, list.array_0[1] );
        Assert.assertEquals( "Ocho", list.array_1[1] );
        Assert.assertEquals( "Ocho", GraphUtil.findValueStringById( list, PairListTest.OCHO ) );
        Assert.assertEquals( PairListTest.OCHO, GraphUtil.findValueIdByString( list, "Ocho" ) );

        Assert.assertEquals( PairListTest.SIETE, list.array_0[2] );
        Assert.assertEquals( "Siete", list.array_1[2] );
        Assert.assertEquals( "Siete", GraphUtil.findValueStringById( list, PairListTest.SIETE ) );
        Assert.assertEquals( PairListTest.SIETE, GraphUtil.findValueIdByString( list, "Siete" ) );
    }

    /**
     * Test function.
     */
    public void test987Remove9()
    {
        final PairList<Integer, String> list = create987List();
        Assert.assertNotNull( list );

        list.remove( 0, 1 );
        Assert.assertEquals( 2, list.length );
        Assert.assertEquals( PairListTest.OCHO, list.array_0[0] );
        Assert.assertEquals( "Ocho", list.array_1[0] );
        Assert.assertEquals( PairListTest.SIETE, list.array_0[1] );
        Assert.assertEquals( "Siete", list.array_1[1] );
        list.remove( 0, 2 );
        Assert.assertEquals( 0, list.length );
    }

    /**
     * Test function.
     */
    public void test987Remove8()
    {
        final PairList<Integer, String> list = create987List();
        Assert.assertNotNull( list );

        list.remove( 1, 1 );
        Assert.assertEquals( 2, list.length );
        Assert.assertEquals( PairListTest.NUEVE, list.array_0[0] );
        Assert.assertEquals( "Nueve", list.array_1[0] );
        Assert.assertEquals( PairListTest.SIETE, list.array_0[1] );
        Assert.assertEquals( "Siete", list.array_1[1] );
        list.remove( 0, 2 );
        Assert.assertEquals( 0, list.length );
    }

    /**
     * Test function.
     */
    public void test987Remove7()
    {
        final PairList<Integer, String> list = create987List();
        Assert.assertNotNull( list );

        list.remove( 2, 1 );
        Assert.assertEquals( 2, list.length );
        Assert.assertEquals( PairListTest.NUEVE, list.array_0[0] );
        Assert.assertEquals( "Nueve", list.array_1[0] );
        Assert.assertEquals( PairListTest.OCHO, list.array_0[1] );
        Assert.assertEquals( "Ocho", list.array_1[1] );
        list.remove( 0, 2 );
        Assert.assertEquals( 0, list.length );
    }

    /**
     * Test function.
     */
    public void test987Remove98()
    {
        final PairList<Integer, String> list = create987List();
        Assert.assertNotNull( list );

        list.remove( 0, 2 );
        Assert.assertEquals( 1, list.length );
        Assert.assertEquals( PairListTest.SIETE, list.array_0[0] );
        Assert.assertEquals( "Siete", list.array_1[0] );
        list.remove( 0, 1 );
        Assert.assertEquals( 0, list.length );
    }

    /**
     * Test function.
     */
    public void test987Remove87()
    {
        final PairList<Integer, String> list = create987List();
        Assert.assertNotNull( list );

        list.remove( 1, 2 );
        Assert.assertEquals( 1, list.length );
        Assert.assertEquals( PairListTest.NUEVE, list.array_0[0] );
        Assert.assertEquals( "Nueve", list.array_1[0] );
        list.remove( 0, 1 );
        Assert.assertEquals( 0, list.length );
    }
}
