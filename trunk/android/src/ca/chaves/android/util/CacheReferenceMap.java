package ca.chaves.android.util;

import java.lang.ref.SoftReference;
import java.util.Hashtable;

/**
 * This class defines a container of cached references.
 *
 * @param <KeyType> key type
 * @param <ValueType> value type
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class CacheReferenceMap<KeyType, ValueType>
{
    /**
     * Cache map.
     */
    private SoftReference<Hashtable<KeyType, SoftReference<ValueType>>> cache;

    /**
     * Clear this container, making it empty.
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
     * @param key to retrieve the value for
     * @return the cached reference indexed by the given key.
     */
    public ValueType get( final KeyType key )
    {
        Hashtable<KeyType, SoftReference<ValueType>> map = null;
        synchronized ( this )
        {
            if ( cache != null )
            {
                map = cache.get();
            }
            if ( map != null )
            {
                final SoftReference<ValueType> reference = map.get( key );
                if ( reference != null )
                {
                    final ValueType value = reference.get();
                    if ( value != null )
                    {
                        return value;
                    }
                    // the garbage collector freed the reference
                    map.remove( key );
                }
            }
        }
        return null;
    }

    /**
     * Set a cached reference associated with the given key.
     *
     * @param key where to save the value at
     * @param value to save
     * @return value
     */
    public ValueType put( final KeyType key, final ValueType value )
    {
        Hashtable<KeyType, SoftReference<ValueType>> map = null;
        synchronized ( this )
        {
            if ( cache != null )
            {
                map = cache.get();
            }
            if ( map == null )
            {
                map = new Hashtable<KeyType, SoftReference<ValueType>>();
                cache = new SoftReference<Hashtable<KeyType, SoftReference<ValueType>>>( map );
            }
            map.put( key, new SoftReference<ValueType>( value ) );
        }
        return value;
    }
}
