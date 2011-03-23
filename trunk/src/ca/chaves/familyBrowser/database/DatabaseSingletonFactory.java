package ca.chaves.familyBrowser.database;

import java.io.IOException;

import ca.chaves.familyBrowser.helpers.Log;
import ca.chaves.familyBrowser.helpers.Utils;

import android.database.sqlite.SQLiteDatabase;

/**
 * This class defines a SQLiteDatabase pool. It implements the DatabaseFactory
 * interface.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class DatabaseSingletonFactory implements DatabaseFactory {

    protected static final String TAG = DatabaseSingletonFactory.class.getSimpleName();

    /**
     * This is the global mutex. It guarantees that the same database file is
     * not being created/initialized by multiple threads at the same time.
     */
    private static final Object g_mutex = new Object();

    /** This is the Database Open singleton helper */
    private final DatabaseHelper m_helper;
    /** This is the SQLiteDatabase singleton instance */
    private volatile SQLiteDatabase m_database;

    /**
     * Constructor.
     *
     * @param helper
     */
    public DatabaseSingletonFactory(final DatabaseHelper helper) {
        this.m_helper = helper;
    }

    /**
     * Close all SQLiteDatabase instances that had been used by getDatabase().
     */
    @Override
    public void closeDatabases() {
        SQLiteDatabase database;
        synchronized (g_mutex) {
            database = this.m_database;
            this.m_database = null;
        }
        if (database != null) {
            database.close();
        }
    }

    /**
     * Open, create or reuse a SQLiteDatabase instance.
     *
     * @return SQLiteDatabase
     */
    @Override
    public SQLiteDatabase getDatabase() {
        // @see http://en.wikipedia.org/wiki/Double-checked_locking
        // note the usage of the local variable 'database' which seems
        // unnecessary. For some versions of the Java VM, it will
        // make the code 25% faster and for others, it won't hurt
        SQLiteDatabase database = this.m_database;
        if (database != null) {
            return database;
        }
        synchronized (g_mutex) {
            database = this.m_database;
            if (database == null) {
                Log.d(TAG, "{ createMissingDatabaseFiles");
                this.createMissingDatabaseFiles();
                Log.d(TAG, "} createMissingDatabaseFiles");
                Log.d(TAG, "{ getWritableDatabase");
                this.m_database = this.m_helper.getWritableDatabase();
                Log.d(TAG, "} getWritableDatabase", this.m_database.getPath());
                database = this.m_database;
            }
            return database;
        }
    }

    /**
     * This function checks if the database did not exist initially, in which
     * case it creates a new database from the raw resources defined by the
     * database model.
     */
    private void createMissingDatabaseFiles() {
        final DatabaseSingletonHelper helper = new DatabaseSingletonHelper(this.m_helper);
        final SQLiteDatabase database = helper.getWritableDatabase();
        final String path = database.getPath();

        final boolean wasDatabaseCreated = helper.wasDatabaseCreated();

        database.close();
        helper.close();

        // if the database file did exist previously, then do not
        // create/overwrite it again. Note that the database file always exist
        // at this point, as a side-effect of DatabaseSingletonHelper. However,
        // DatabaseSingletonHelper also tell us if the database was just created
        // [empty], in which case we proceed to overwrite it with the data
        // stored in the raw resources.
        if (!wasDatabaseCreated) {
            return;
        }

        try {
            Utils.createFile(path, this.m_helper.getDatabaseContext(), this.m_helper.getRawResources());
        }
        catch (IOException exc) {
            Log.e(TAG, "could not create " + path, exc);
            // delete the database file - it might be corrupted
            this.m_helper.getDatabaseContext().deleteFile(path);
        }
    }
}
