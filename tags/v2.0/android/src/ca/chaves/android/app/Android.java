package ca.chaves.android.app;

import ca.chaves.android.BuildManifest;
import ca.chaves.android.util.Debug;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.LayoutInflater;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * The "Application" class.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public abstract class Android
    extends Application
{
    // -----
    // Stage
    // -----

    /**
     * All member values in this class must be initialized before the {@link Android} instance is created; perhaps, in
     * the static constructor of the final Application class.
     */
    protected static final class Stage
    {
        /**
         * We save the application instance here, when the application is started. This instance can be used to create
         * state variables, global resources and context from within the application components.
         */
        private static Android instance;

        /**
         * The data root directory here.
         */
        public static String dataDirectoryName;

        /**
         * The current EULA version, or null if no EULA is needed.
         */
        public static String eulaVersion;

        /**
         * The "main activity" class. This class will be launched by the Splash activities automatically, after all the
         * initialization is complete.
         */
        public static Class<? extends AbstractActivity> mainActivityClass;

        /**
         * The "about activity" class.
         */
        public static Class<? extends AbstractActivity> aboutActivityClass;
    }

    // -----
    // Final
    // -----

    /**
     * This class will be statically initialized after `instance`.
     */
    public static final class App
    {
        /**
         * The standard Application instance.
         */
        public static final Android INSTANCE = Android.Stage.instance;

        /**
         * The data directory name.
         */
        public static final String DATA_DIRECTORY_NAME = Android.Stage.dataDirectoryName;

        /**
         * The current EULA version, or null if no EULA is needed.
         */
        public static final String EULA_VERSION = Android.Stage.eulaVersion;

        /**
         * The "main activity" class.
         */
        public static final Class<? extends AbstractActivity> MAIN_ACTIVITY_CLASS = Android.Stage.mainActivityClass;

        /**
         * The "about activity" class.
         */
        public static final Class<? extends AbstractActivity> ABOUT_ACTIVITY_CLASS = Android.Stage.aboutActivityClass;

        /**
         * The standard LayoutInflater instance.
         */
        public static final LayoutInflater INFLATER = LayoutInflater.from( Android.App.INSTANCE );

        // --------------
        // Home directory
        // --------------

        /**
         * Initialize and return the "home directory" object.
         *
         * @return home properly initialized
         * @throws IOException on error
         */
        public static File getHome()
            throws IOException
        {
            return Android.App.INSTANCE.getHome( null );
        }

        /**
         * Initialize and return path, relative from the "home" directory.
         *
         * @param path inside $HOME
         * @return File($HOME, path) properly initialized
         * @throws IOException on error
         */
        public static File getHome( final String path )
            throws IOException
        {
            return Android.App.INSTANCE.getHome( path );
        }

        // ----------------
        // Database methods
        // ----------------

        /**
         * Initialize and return this.database object.
         *
         * @return SQLiteDatabase properly initialized.
         * @throws IOException on error.
         */
        public static SQLiteDatabase getDatabase()
            throws IOException
        {
            return Android.App.INSTANCE.getDatabase();
        }

        // --------------
        // Locale methods
        // --------------

        /**
         * Return {@link Locale}.
         *
         * @param localeCode something like "es" or "en_CA"
         * @return new {@link Locale} instance for the given 'localeCode'
         */
        public static Locale getLocale( final String localeCode )
        {
            int index;
            if ( 0 < ( index = localeCode.indexOf( '-' ) ) || 0 < ( index = localeCode.indexOf( '_' ) ) )
            {
                final String languageCode = localeCode.substring( 0, index );
                final String countryCode = localeCode.substring( index + 1 );
                return new Locale( languageCode, countryCode );
            }

            return new Locale( localeCode );
        }

        /**
         * Change language for this application only (not system-wide).
         *
         * @param localeCode something like "es" or "en_CA".
         * @throws IOException on error.
         */
        public static void setLocale( final String localeCode )
            throws IOException
        {
            Android.App.INSTANCE.setLocale( getLocale( localeCode ) );
        }

        /**
         * Change language for this application only (not system-wide).
         *
         * @param locale the {@link Locale} instance.
         * @throws IOException on error.
         */
        public static void setLocale( final Locale locale )
            throws IOException
        {
            Android.App.INSTANCE.setLocale( locale );
        }

        // --------------
        // Initialization
        // --------------

        /**
         * Static initialization
         */
        static
        {
            // setup StrictMode validations
            if ( BuildManifest.DEBUG_ENABLED )
            {
                // enable StrictMode
                Android.App.maybeSetupStrictMode();
                // enable ` traceview ` - see
                // http://developer.android.com/guide/developing/debugging/debugging-tracing.html
                android.os.Debug.enableEmulatorTraceOutput();
            }

            // setup crash error handler
            // // NOTE: we do not add the crash error handler anymore since it is now built-in and fully
            // // integrated with the Android Market
            // new ErrorReporter().init( Android.App.INSTANCE );

            // verify all initializations
            if ( BuildManifest.DEBUG_ENABLED )
            {
                // checks that everything was initialized
                if ( Android.App.MAIN_ACTIVITY_CLASS == null )
                {
                    throw new RuntimeException( "HORROR: mainActivityClass not initialized!" );
                }
                if ( Android.App.ABOUT_ACTIVITY_CLASS == null )
                {
                    throw new RuntimeException( "HORROR: aboutActivityClass not initialized!" );
                }
                if ( Android.App.DATA_DIRECTORY_NAME == null )
                {
                    throw new RuntimeException( "HORROR: dataDirectoryName not initialized!" );
                }
            }
        };

        // ----------
        // StrictMode
        // ----------

        /**
         * Setup StrictMode on this application. StrictMode was introduced in Gingerbread. It detects all cases where an
         * ANR might occur. For example, it will detect and log to LogCat all database reads and writes that happen on
         * the main thread (i.e. the GUI thread).
         *
         * @see http ://developer.android.com/reference/android/os/StrictMode. html
         */
        private static void maybeSetupStrictMode()
        {
            Debug.enter();
            try
            {
                final Class<?> strictMode = Class.forName( "android.os.StrictMode" );
                final Method enableDefaults = strictMode.getMethod( "enableDefaults" );
                enableDefaults.invoke( strictMode );
            }
            // CHECKSTYLE:OFF: Illegal Catch: Catching 'Exception' is not allowed.
            catch ( final Exception ex )
            // CHECKSTYLE:ON: Illegal Catch: Catching 'Exception' is not allowed.
            {
                Debug.print( "not running on a device with strict mode: sdk version", Build.VERSION.SDK_INT );
            }
            Debug.leave();
        }
    }

    /**
     * Constructor.
     */
    protected Android()
    {
        // save the singleton
        Android.Stage.instance = this;
    }

    // --------------
    // Home directory
    // --------------

    /**
     * Initialize and return path, relative from the "home" directory.
     *
     * @param path inside $HOME
     * @return File($HOME, path) properly initialized
     * @throws IOException on error
     */
    protected abstract File getHome( final String path )
        throws IOException;

    // ----------------
    // Database methods
    // ----------------

    /**
     * Initialize and return this.database object.
     *
     * @return SQLiteDatabase properly initialized.
     * @throws IOException on error.
     */
    protected abstract SQLiteDatabase getDatabase()
        throws IOException;

    // --------------
    // Locale methods
    // --------------

    /**
     * Change language for this application only (not system-wide).
     *
     * @param locale the new {@link Locale} instance.
     * @throws IOException on error.
     */
    protected abstract void setLocale( final Locale locale )
        throws IOException;
}
