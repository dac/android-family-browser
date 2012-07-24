package ca.chaves.familyBrowser.test.util;

import ca.chaves.android.util.PairList;

/**
 * Utility functions to work with GraphValueList(s).
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class GraphUtil
{
    /**
     * Get the string with the given 'id'.
     *
     * @param list the list where to look for 'id'
     * @param id the id to look for
     * @return the string associated to the given 'id'
     */
    public static String findValueStringById( final PairList<Integer, String> list, final Integer id )
    {
        for ( int index = 0; index < list.length; ++index )
        {
            final Integer current = list.array_0[index];
            if ( ( current != null ) && current.equals( id ) )
            {
                return list.array_1[index];
            }
        }
        return null;
    }

    /**
     * Get the id with the given 'string' value.
     *
     * @param list the list where to look 'string' at
     * @param string the string to look for
     * @return the id associated to the given 'string'
     */
    public static Integer findValueIdByString( final PairList<Integer, String> list, final String string )
    {
        for ( int index = 0; index < list.length; ++index )
        {
            final String current = list.array_1[index];
            if ( ( current != null ) && current.equals( string ) )
            {
                return list.array_0[index];
            }
        }
        return null;
    }
}
