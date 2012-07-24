package ca.chaves.android.util;

/**
 * This class is used to return values from inside a closure back to the caller.
 *
 * @param <Type> the value type
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class Value<Type>
{
    /**
     * The value of this <code>Type</code>.
     */
    public Type value;
}
