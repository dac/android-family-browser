package ca.chaves.android.util;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * This class stores a list of pairs <FirstType, SecondType. This class is implemented to minimize memory allocations,
 * which are expensive in Android.
 *
 * @param <FirstType> type for the <code>first</code> value in the pair.
 * @param <SecondType> type for the <code>second</code> value in the pair.
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class PairList<FirstType, SecondType>
{
    /**
     * This is the list name or title.
     */
    public final String title;

    /**
     * This is the logical list length. Outsiders, consider this a read-only value.
     */
    public int length;

    /**
     * This is the list of <code>FirstType</code> values. The logical length is <code>this.length</code>.
     */
    public FirstType[] array_0;

    /**
     * This is the list of <code>SecondType</code> values. The logical length is <code>this.length</code>.
     */
    public SecondType[] array_1;

    /**
     * Constructor.
     *
     * @param title the list title.
     */
    public PairList( final String title )
    {
        this.title = title;
    }

    /**
     * Clear list.
     */
    public void clear()
    {
        Arrays.fill( array_0, 0, length, null );
        Arrays.fill( array_1, 0, length, null );
        length = 0;
    }

    /**
     * Remove pair.
     *
     * @param index pair index to remove, between <code>0</code> and <code>(length-1)</code>.
     * @param count number of elements to remove, between <code>1</code> and <code>(length - index)</code>.
     */
    public void remove( final int index, final int count )
    {
        if ( 0 <= index && index < length && 0 < count )
        {
            final int shiftCount = length - index - count;
            if ( 0 <= shiftCount )
            {
                System.arraycopy( array_0, index + count, array_0, index, shiftCount );
                System.arraycopy( array_1, index + count, array_1, index, shiftCount );

                length -= count;
                Arrays.fill( array_0, length, length + count, null );
                Arrays.fill( array_1, length, length + count, null );
            }
        }
    }

    /**
     * Append pair.
     *
     * @param first the first value of the pair to add.
     * @param second the second value of the pair to add.
     */
    public void add( final FirstType first, final SecondType second )
    {
        // create arrays if necessary
        if ( array_0 == null )
        {
            @SuppressWarnings( "unchecked" )
            final FirstType[] array = (FirstType[]) Array.newInstance( first.getClass(), 16 );
            array_0 = array;
        }
        if ( array_1 == null )
        {
            @SuppressWarnings( "unchecked" )
            final SecondType[] array = (SecondType[]) Array.newInstance( second.getClass(), 16 );
            array_1 = array;
        }

        // expand internal lists if needed
        if ( array_0.length <= length )
        {
            @SuppressWarnings( "unchecked" )
            final FirstType[] array = (FirstType[]) resize( array_0, length + 1 );
            array_0 = array;
        }
        if ( array_1.length <= length )
        {
            @SuppressWarnings( "unchecked" )
            final SecondType[] array = (SecondType[]) resize( array_1, length + 1 );
            array_1 = array;
        }

        // store new id/value at the end of our lists
        array_0[length] = first;
        array_1[length] = second;
        // now, we have one more element in here
        ++length;
    }

    /**
     * Resize a Java array.
     *
     * @param oldArray the original array.
     * @param minimumSize the minimum size for the new array.
     * @return the new Java array.
     */
    private static Object resize( final Object oldArray, final int minimumSize )
    {
        Debug.enter();
        final Class<?> arrayClass = oldArray.getClass();
        if ( arrayClass.isArray() )
        {
            final int oldLength = Array.getLength( oldArray );
            int newLength = oldLength + ( oldLength / 2 ); // 50% more
            if ( newLength < minimumSize )
            {
                newLength = minimumSize;
            }
            final Class<?> componentType = arrayClass.getComponentType();
            final Object newArray = Array.newInstance( componentType, newLength );
            System.arraycopy( oldArray, 0, newArray, 0, oldLength );
            Debug.leave( oldLength, newLength );
            return newArray;
        }
        Debug.leave( "not an array", arrayClass );
        return null;
    }
}
