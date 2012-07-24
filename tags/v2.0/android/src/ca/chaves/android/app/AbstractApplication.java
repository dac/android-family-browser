package ca.chaves.android.app;

import ca.chaves.android.R;
import ca.chaves.android.util.Debug;
import ca.chaves.android.util.TarArchive;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Locale;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * {@link Android} class implementation.
 */
public class AbstractApplication
    extends Android
{
    /**
     * The global locker. This object guarantees that some objects are not created/initialized by multiple threads at
     * the same time.
     */
    private static final Object LOCK = new Object();

    /**
     * The application home directory.
     *
     * @warning you must call getHome() at least once, in order to have this variable initialized
     */
    private File home;

    /**
     * Called when the application is created. Override this method to initialize our application singleton and to
     * create and initialize any application state variables or shared resources.
     */
    @Override
    public void onCreate()
    {
        super.onCreate();
        {
            Debug.print( "create" );
        }
    }

    /**
     * Called when the application object is terminated. There is no guarantee of this method being called.
     */
    @Override
    public void onTerminate()
    {
        {
            Debug.print( "terminate" );
            DatabaseSession.close();
        }
        super.onTerminate();
    }

    /**
     * Called when a background process have already been terminated and the current foreground applications are still
     * low on memory. Override this method to clear caches or release unnecessary resources.
     */
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        {
            Debug.print( "lowMemory" );
            DatabaseSession.close(); // to free buffers
        }
    }

    /**
     * Override this function if it is necessary to handle configuration changes at an application level.
     *
     * @param newConfig new configuration.
     */
    @Override
    public void onConfigurationChanged( final Configuration newConfig )
    {
        super.onConfigurationChanged( newConfig );
        {
            Debug.enter();
            try
            {
                Debug.print( "configurationChanged", newConfig.locale );
                Android.App.setLocale( newConfig.locale );
            }
            catch ( IOException ex )
            {
                Debug.error( ex, "unable to set new locale", newConfig.locale );
            }
            Debug.leave();
        }
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
    @Override
    protected File getHome( final String path )
        throws IOException
    {
        // @see http://en.wikipedia.org/wiki/Double-checked_locking. The use of local variable 'directory' seems
        // unnecessary. It will make the code 25% faster for some Java VM, and it won't hurt for others
        File directory = home;
        if ( directory != null )
        {
            Debug.print( "home", directory.getAbsolutePath() );
            return ( ( path == null ) || ( path.length() <= 0 ) ) ? directory : new File( directory, path );
        }
        Debug.enter();
        synchronized ( LOCK )
        {
            directory = home;
            if ( directory != null )
            {
                Debug.print( "lost getHome singleton race" );
            }
            else
            {
                // initialize home
                directory = new File( Android.App.DATA_DIRECTORY_NAME );
                // next will be very slow
                restoreMissingFiles( directory );
                // finally, home
                home = directory;
            }
        }
        Debug.leave( directory.getAbsolutePath() );
        return ( ( path == null ) || ( path.length() <= 0 ) ) ? directory : new File( directory, path );
    }

    /**
     * Setup missing application files into the "home directory".
     *
     * @param targetDir
     * @throws IOException
     * @return true on success
     */
    private boolean restoreMissingFiles( final File targetDir )
        throws IOException
    {
        Debug.enter();
        boolean success = false;
        final SharedPreferences prefs = Settings.INSTANCE.getSharedPreferences();
        final String oldTarball = prefs.getString( SettingsStorage.FIELD_TARBALL, null );
        final String newTarball = getString( R.string.build_tarball );
        final boolean keepOldFiles = newTarball.equals( oldTarball );
        try
        {
            // extract the tar-ball to recover any missing file, if any
            final TarArchive tarball = new TarArchive( new GZIPInputStream( tarballFile() ) );
            tarball.keepOldFiles = keepOldFiles;
            tarball.extract( targetDir );
            tarball.close();
            // once the restore is completed successfully,
            // we need to update the shared preference, if necessary
            if ( !keepOldFiles )
            {
                final SharedPreferences.Editor edit = prefs.edit();
                edit.putString( SettingsStorage.FIELD_TARBALL, newTarball );
                success = edit.commit();
            }
            else
            {
                success = true;
            }
        }
        catch ( final Resources.NotFoundException ex )
        {
            // this exception usually would happen from the unit-tests
            Debug.error( ex, "tarball resources not found", keepOldFiles );
            // not really a fatal error if keepOldFiles is true
            if ( !keepOldFiles )
            {
                Debug.leave();
                throw new IOException( "Tarball resources missing" );
            }
        }
        catch ( final TarArchive.TarHeaderException ex )
        {
            Debug.error( ex, "invalid tarball header", keepOldFiles );
            Debug.leave();
            throw new IOException( "Invalid tarball format" );
        }
        catch ( final IOException ex )
        {
            Debug.error( ex, "unable to install tarball", keepOldFiles );
            // not really a fatal error if keepOldFiles is true
            if ( !keepOldFiles )
            {
                Debug.leave();
                throw ex;
            }
        }
        Debug.leave( success );
        return success;
    }

    /**
     * Return the tarball file as an InputStream.
     */
    private InputStream tarballFile()
    {
        final Resources resources = Android.App.INSTANCE.getResources();
        final Vector<InputStream> list = new Vector<InputStream>();
        for ( final Integer resourceId : AbstractApplication.TARBALL_RAW_RESOURCES )
        {
            list.add( resources.openRawResource( resourceId ) );
        }
        return new SequenceInputStream( list.elements() );
    }

    /**
     * The raw resources needed to install the tar-ball.
     */
    private static final Integer[] TARBALL_RAW_RESOURCES = new Integer[]{ //
        Integer.valueOf( R.raw.tarball_0 ), //
            Integer.valueOf( R.raw.tarball_1 ), //
            Integer.valueOf( R.raw.tarball_2 ), //
            Integer.valueOf( R.raw.tarball_3 ), //
            Integer.valueOf( R.raw.tarball_4 ), //
            Integer.valueOf( R.raw.tarball_5 ), //
            Integer.valueOf( R.raw.tarball_6 ), //
            Integer.valueOf( R.raw.tarball_7 ), //
        };

    // ----------------
    // Database methods
    // ----------------

    /**
     * Initialize and return this.database object.
     *
     * @return SQLiteDatabase properly initialized.
     * @throws IOException on error.
     */
    @Override
    protected SQLiteDatabase getDatabase()
        throws IOException
    {
        Debug.enter();
        final SQLiteDatabase database = DatabaseSession.getDatabase( Android.App.INSTANCE );
        Debug.leave();
        return database;
    }

    // --------------
    // Locale methods
    // --------------

    /**
     * Change language for this application only (not system-wide).
     *
     * @param locale the new {@link Locale} instance.
     * @throws IOException on error.
     */
    @Override
    protected void setLocale( final Locale locale )
        throws IOException
    {
        Debug.enter( locale );
        DatabaseSession.setLocale( Android.App.INSTANCE, locale );
        Debug.leave( locale );
    }
}
