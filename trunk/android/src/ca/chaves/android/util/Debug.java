package ca.chaves.android.util;

import ca.chaves.android.BuildManifest;

/**
 * This class makes it easy to "deactivate any calls to Log methods in the source code" before publishing this
 * application in the Android Market.
 * <p/>
 * The order in terms of verbosity, from least to most is ERROR, WARN, INFO, DEBUG, VERBOSE. Verbose should never be
 * compiled into an application except during development. Debug logs are compiled in but stripped at runtime.
 * <p/>
 * Error, warning and info logs are always kept.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public final class Debug
{
    /**
     * The global locker.
     */
    private static final Object LOCK = new Object();

    // ------------
    // Logging Tags
    // ------------

    /**
     * Create {@link Class} tag for debugging.
     *
     * @param cls class to build the tag for.
     * @return the tag associated wit the class 'cls'.
     */
    public static String getTag( final Class<?> cls )
    {
        return cls.getSimpleName();
    }

    /**
     * Create {@link Class} tag for debugging.
     *
     * @param cls class to build the tag for.
     * @param extraTag extra value to be added to the tag.
     * @return the tag to be used with `cls', plus the given `extraTag'.
     */
    public static String getTag( final Class<?> cls, final String extraTag )
    {
        final StringBuilder builder = Debug.acquire();
        {
            // builder.append( getTag( cls ) );
            if ( extraTag != null )
            {
                // builder.append( '?' );
                builder.append( extraTag );
            }
        }
        final String result = builder.toString();
        Debug.release( builder );
        return result;
    }

    /**
     * Create unique {@link Class} tag for debugging - used to tag instances.
     *
     * @param cls class to build the unique tag for
     * @return an unique-tag associated with the class 'cls'.
     */
    public static String getUniqueTag( final Class<?> cls )
    {
        return Debug.getUniqueTag( cls, null );
    }

    /**
     * Create unique {@link Class} tag for debugging - used to tag instances.
     *
     * @param cls class to build the unique tag for.
     * @param extraTag extra value to be added to the tag.
     * @return an unique-tag associated with the class 'cls', plus the given 'extraTag'.
     */
    public static String getUniqueTag( final Class<?> cls, final String extraTag )
    {
        final StringBuilder builder = Debug.acquire();
        {
            // Class part
            if ( cls != null )
            {
                builder.append( getTag( cls ) );
                if ( extraTag != null )
                {
                    builder.append( '?' );
                    builder.append( extraTag );
                }
            }
            else if ( extraTag != null )
            {
                builder.append( extraTag );
            }
            // unique part
            int uniqueId;
            synchronized ( Debug.LOCK )
            {
                uniqueId = ++Debug.uniqueIdGenerator;
            }
            builder.append( '#' );
            builder.append( Integer.toString( uniqueId ) );
        }
        final String result = builder.toString();
        Debug.release( builder );
        return result;
    }

    /**
     * This integer is used to generate unique tags.
     */
    private static int uniqueIdGenerator = 1000;

    // ------------------
    // StringBuilder Pool
    // ------------------

    /**
     * Preallocated capacity for all StringBuilder's in the pool[].
     */
    private static final int CAPACITY = 128;

    /**
     * Pool of StringBuilder's, used to build strings inside methods t().
     */
    private static final StringBuilder[] POOL = ( BuildManifest.DEBUG_ENABLED ? new StringBuilder[]{
        new StringBuilder( Debug.CAPACITY ), //
        new StringBuilder( Debug.CAPACITY ), //
        new StringBuilder( Debug.CAPACITY ), //
        new StringBuilder( Debug.CAPACITY ), //
    } : null );

    /**
     * Rover index in the pool[] of StringBuilder's.
     */
    private static int rover;

    /**
     * Acquire a new StringBuilder from the pool[].
     *
     * @return StringBuilder
     */
    private static StringBuilder acquire()
    {
        if ( BuildManifest.DEBUG_ENABLED )
        {
            synchronized ( Debug.LOCK )
            {
                for ( int count = Debug.POOL.length; 0 < count; --count )
                {
                    if ( Debug.POOL.length <= ++Debug.rover )
                    {
                        Debug.rover = 0;
                    }
                    final StringBuilder builder = Debug.POOL[Debug.rover];
                    if ( builder != null )
                    {
                        Debug.POOL[Debug.rover] = null;
                        return builder;
                    }
                }
            }
        }
        return new StringBuilder( Debug.CAPACITY );
    }

    /**
     * Release a StringBuilder back to the pool[].
     *
     * @param builder
     */
    private static String release( final StringBuilder builder )
    {
        final String result = builder.toString();
        if ( BuildManifest.DEBUG_ENABLED )
        {
            builder.setLength( 0 ); // reset to empty
            synchronized ( Debug.LOCK )
            {
                for ( int count = Debug.POOL.length; 0 < count; --count )
                {
                    if ( Debug.rover <= 0 )
                    {
                        Debug.rover = Debug.POOL.length;
                    }
                    if ( Debug.POOL[--Debug.rover] == null )
                    {
                        Debug.POOL[Debug.rover] = builder;
                        return result;
                    }
                }
            }
        }
        return result;
    }

    // ---------
    // Handy t()
    // ---------

    /**
     * Get text for logging.
     *
     * @param args list of Object's to generate text from
     * @return text from the concatenation of Object.toString()'s
     */
    public static String t( final Object... args )
    {
        final StringBuilder builder = Debug.acquire();
        {
            char delimiter = '\0';
            for ( final Object object : args )
            {
                if ( delimiter != '\0' )
                {
                    builder.append( delimiter );
                }
                delimiter = ' ';
                if ( object == null )
                {
                    builder.append( "(null)" );
                    continue;
                }
                final String value = object.toString();
                if ( ( value == null ) || ( value.length() <= 0 ) )
                {
                    builder.append( "(empty)" );
                    continue;
                }
                builder.append( value );
            }
        }
        return Debug.release( builder );
    }

    // ----------------
    // Android.util.Log
    // ----------------

    /*-
     * stackTrace[0] = VMStack.java:-2: dalvik.system.VMStack.getThreadStackTrace()
     * stackTrace[1] = Thread.java:786: java.lang.Thread.getStackTrace()
     * stackTrace[2] = Debug.java: the current function
     * stackTrace[3] = caller of the current function
     */
    private static final int CALLER_STACK_FRAME = 3;

    /**
     * Array of white spaces, used to generate indentations.
     */
    private static final String BLANKS = "                                               ";

    /**
     * Get substring.
     *
     * @param text input.
     * @param delimiter delimiter.
     * @param defaultValue default value.
     * @return the substring of `text' after the given `delimiter'.
     */
    private static String substringAfter( final String text, final char delimiter, final String defaultValue )
    {
        if ( text != null )
        {
            final int index = text.lastIndexOf( delimiter );
            return ( index < 0 ) ? text : text.substring( 1 + index );
        }
        return defaultValue;
    }

    /**
     * Get logging tag from the 'stackTrace'.
     *
     * @param stackTrace
     * @return the tag to be used on logging.
     */
    private static String getLogTag( final StackTraceElement[] stackTrace )
    {
        final StackTraceElement caller = stackTrace[Debug.CALLER_STACK_FRAME];
        return Debug.substringAfter( caller.getClassName(), '.', "Debug" );
    }

    /**
     * Get logging tag indentation prefix.
     *
     * @param delimiter delimiter.
     * @return the prefix used on each log line; this prefix includes all indentation, for example.
     */
    private static String getLogPrefix( final char delimiter )
    {
        final ThreadValues local = Debug.TLS.get();

        final StringBuilder builder = Debug.acquire();
        {
            // append indentation space
            if ( 0 <= local.level )
            {
                String blanks = Debug.BLANKS;
                final int index = blanks.length() - local.level;
                builder.append( ( 0 <= index ) ? blanks.substring( index ) : blanks );
            }
            // append delimiter
            builder.append( delimiter );
        }
        return Debug.release( builder );
    }

    /**
     * Get logging suffix.
     *
     * @param stackTrace stack trace.
     * @return the suffix used on each log line.
     */
    private static String getLogSuffix( final StackTraceElement[] stackTrace )
    {
        final StackTraceElement caller = stackTrace[Debug.CALLER_STACK_FRAME];
        final String method = caller.getMethodName();
        final String line = Integer.toString( caller.getLineNumber() );

        final StringBuilder builder = Debug.acquire();
        {
            builder.append( "; at " );
            // append method name
            builder.append( method );
            builder.append( ':' );
            // append line number
            builder.append( line );
        }
        return Debug.release( builder );
    }

    /**
     * Get logging arguments.
     *
     * @param prefix log prefix.
     * @param suffix log suffix.
     * @param args arguments.
     * @return the arguments as an array, plus the given log prefix.
     */
    private static Object[] getLogArgs( final String prefix, final String suffix, final Object... args )
    {
        if ( 0 < args.length )
        {
            final Object[] params = new Object[2 + args.length];
            params[0] = prefix;
            System.arraycopy( args, 0, params, 1, args.length );
            params[1 + args.length] = suffix;
            return params;
        }
        return new Object[]{prefix, suffix};
    }

    /**
     * Send an ERROR log message to 'adb logcat'.
     *
     * @param trowable where to get the stack trace from
     * @param args message arguments to log
     */
    public static void error( final Throwable trowable, final Object... args )
    {
        if ( BuildManifest.DEBUG_ENABLED )
        {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            final String tag = Debug.getLogTag( stackTrace );
            final String prefix = Debug.getLogPrefix( ':' );
            final String suffix = Debug.getLogSuffix( stackTrace );
            if ( trowable != null )
            {
                android.util.Log.e( tag, Debug.t( Debug.getLogArgs( prefix, suffix, args ) ), trowable );
            }
            else
            {
                android.util.Log.e( tag, Debug.t( Debug.getLogArgs( prefix, suffix, args ) ) );
            }
        }
    }

    /**
     * Send a VERBOSE log message to 'logcat'.
     *
     * @param args message arguments to log.
     */
    public static void print( final Object... args )
    {
        if ( BuildManifest.DEBUG_ENABLED )
        {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            final String tag = Debug.getLogTag( stackTrace );
            final String prefix = Debug.getLogPrefix( ' ' );
            final String suffix = Debug.getLogSuffix( stackTrace );
            android.util.Log.v( tag, Debug.t( Debug.getLogArgs( prefix, suffix, args ) ) );
        }
    }

    /**
     * Send a VERBOSE log message to `logcat'. This function is meant to be used at every function prologue.
     *
     * @param args message arguments to log
     */
    public static void enter( final Object... args )
    {
        if ( BuildManifest.DEBUG_ENABLED )
        {
            final ThreadValues local = Debug.TLS.get();

            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            final String tag = Debug.getLogTag( stackTrace );
            final String prefix = Debug.getLogPrefix( '{' );
            final String suffix = Debug.getLogSuffix( stackTrace );
            android.util.Log.v( tag, Debug.t( Debug.getLogArgs( prefix, suffix, args ) ) );

            ++local.level;
        }

    }

    /**
     * Send a VERBOSE log message to `logcat'. This function is meant to be used at every function epilogue.
     *
     * @param args message arguments to log
     */
    public static void leave( final Object... args )
    {
        if ( BuildManifest.DEBUG_ENABLED )
        {
            final ThreadValues local = Debug.TLS.get();

            --local.level;

            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            final String tag = Debug.getLogTag( stackTrace );
            final String prefix = Debug.getLogPrefix( '}' );
            final String suffix = Debug.getLogSuffix( stackTrace );
            android.util.Log.v( tag, Debug.t( Debug.getLogArgs( prefix, suffix, args ) ) );
        }

    }

    // --------------------
    // Thread Local Storage
    // --------------------

    private static final ThreadLocalStorage TLS = ( BuildManifest.DEBUG_ENABLED ? new ThreadLocalStorage() : null );

    /**
     * TLS instance generator.
     */
    private static final class ThreadLocalStorage
        extends ThreadLocal<ThreadValues>
    {
        @Override
        protected ThreadValues initialValue()
        {
            return new ThreadValues();
        }
    }

    /**
     * Thread local storage for {@link Debug#enter(Object...)} and {@link Debug#leave(Object...)}.
     */
    private static final class ThreadValues
    {
        /**
         * The current indentation level. This integer is increased automatically in {@link Debug#enter(Object...)}, and
         * decremented at {@link Debug#leave(Object...)}.
         */
        int level = 2;
    }
}
