package ca.chaves.android.util;

import java.lang.ref.SoftReference;

/**
 * This class defines a mutable reference used in cache objects. This class is needed because SoftReference<TValue> are
 * immutable references.
 *
 * @param <ValueType> the instance type to be cached
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class CacheReference<ValueType>
{
    /**
     * Cache reference.
     */
    private SoftReference<ValueType> cache;

    /**
     * Reset the cached reference to null.
     */
    public void clear()
    {
        synchronized ( this )
        {
            if ( cache != null )
            {
                cache.clear();
                cache = null;
            }
        }
    }

    /**
     * Get cached reference.
     *
     * @return the cached reference, or null if nothing cached.
     */
    public ValueType get()
    {
        synchronized ( this )
        {
            if ( cache != null )
            {
                return cache.get();
            }
        }
        return null;
    }

    /**
     * Set a new cached reference.
     *
     * @param value to be cached
     * @return value
     */
    public ValueType put( final ValueType value )
    {
        synchronized ( this )
        {
            cache = new SoftReference<ValueType>( value );
        }
        return value;
    }
}
