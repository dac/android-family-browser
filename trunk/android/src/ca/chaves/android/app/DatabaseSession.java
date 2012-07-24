package ca.chaves.android.app;

import ca.chaves.android.BuildManifest;
import ca.chaves.android.R;
import ca.chaves.android.graph.GraphStorage;
import ca.chaves.android.util.Debug;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * This class handles database sessions and the global "database storage" instance.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class DatabaseSession
{
    /**
     * The application database version. We use the same number as the android package version number.
     */
    private static final int DATABASE_VERSION = BuildManifest.VERSION_CODE;

    /**
     * The application database name; note it is dependent on the database version number.
     */
    private static final String DATABASE_NAME = "v" + DatabaseSession.DATABASE_VERSION + ".db";

    /**
     * The global locker. It guarantees that some objects are not created/initialized by multiple threads at the same
     * time.
     */
    private static final Object LOCK = new Object();

    /**
     * The application database.
     *
     * @warning you must call getDatabase() at least once, in order to have this variable initialized
     */
    private static SQLiteDatabase databaseInstance;

    /**
     * The graph storage.
     */
    public static final GraphStorage GRAPH_STORE = new GraphStorage();

    /**
     * Initialize and return this.database object.
     *
     * @param app the application context.
     * @return databaseInstance properly initialized.
     * @throws IOException on error.
     */
    public static SQLiteDatabase getDatabase( final Android app )
        throws IOException
    {
        SQLiteDatabase database = DatabaseSession.databaseInstance;
        if ( database != null )
        {
            return database;
        }
        Debug.enter();
        final Resources resources = Android.App.INSTANCE.getResources();
        // database file
        final File filename = new File( app.getHome( "databases" ), DatabaseSession.DATABASE_NAME );
        // database settings
        final int openFlags =
            ( SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY );
        synchronized ( DatabaseSession.LOCK )
        {
            database = DatabaseSession.databaseInstance;
            if ( database != null )
            {
                Debug.print( "lost getDatabase singleton race" );
            }
            else
            {
                // get current locale
                final Locale locale = Locale.getDefault();
                Debug.print( "open database: language", locale.getLanguage(), "country", locale.getCountry() );
                // open database
                database = SQLiteDatabase.openDatabase( filename.getAbsolutePath(), null, openFlags );
                database.setVersion( DatabaseSession.DATABASE_VERSION );
                database.setLockingEnabled( true );
                database.setLocale( locale );
                // run initialization SQL statements
                // NOTE: this must run inside the synchronized block
                final String[] stmts = resources.getStringArray( R.array.database_sql_init );
                for ( final String stmt : stmts )
                {
                    Debug.print( stmt );
                    database.execSQL( stmt );
                }
                // load database initial data
                // NOTE: this must run inside the synchronized block
                DatabaseSession.resetStorage( app, DatabaseSession.GRAPH_STORE, database, locale );
                // finally, this.database
                DatabaseSession.databaseInstance = database;
                Debug.leave( "open database" );
            }
        }
        Debug.leave( database.getPath() );
        return database;
    }

    /**
     * Close this database.
     */
    public static void close()
    {
        Debug.enter();
        SQLiteDatabase database;
        synchronized ( DatabaseSession.LOCK )
        {
            database = DatabaseSession.databaseInstance;
            DatabaseSession.databaseInstance = null;
        }
        if ( database != null )
        {
            database.close();
        }
        Debug.leave();
    }

    /**
     * Change language for this application only.
     *
     * @param context the application context.
     * @param locale the new {@link Locale} instance.
     * @throws IOException on error.
     * @see "http ://adrianvintu.com/blogengine/post/Force-Locale-on-Android .aspx"
     * @see "http://code.google.com/p/languagepickerwidget/"
     */
    public static void setLocale( final Context context, final Locale locale )
        throws IOException
    {
        Debug.enter( locale );
        final Resources resources = context.getResources();

        // reload the database locale as well
        synchronized ( DatabaseSession.LOCK )
        {
            final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            final Configuration configuration = resources.getConfiguration();

            Debug.print( locale, "language:", locale.getLanguage(), "country:", locale.getCountry() );
            Locale.setDefault( locale );

            configuration.locale = locale;
            resources.updateConfiguration( configuration, displayMetrics );

            // reset database locale as well
            // NOTE: this must run inside the synchronized block
            final SQLiteDatabase database = DatabaseSession.databaseInstance;
            if ( database != null )
            {
                DatabaseSession.resetStorage( context, DatabaseSession.GRAPH_STORE, database, locale );
            }
        }
        Debug.leave( locale );
    }

    /**
     * Re-open the given database storage with the given locale value. This function must be called during
     * initialization, or when the database locale must change.
     *
     * @param context the application context
     * @param graphStorage the graph model
     * @param database the database where the 'graphStorage' lives in
     * @param locale the new language locale
     * @throws IOException on error
     */
    private static void resetStorage( final Context context, final GraphStorage graphStorage,
                                      final SQLiteDatabase database, final Locale locale )
        throws IOException
    {
        Debug.enter();
        database.setLocale( locale );
        // setup application-specific databases
        graphStorage.resetStorage( context, database, locale );
        Debug.leave();
    }
}
