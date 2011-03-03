package ca.chaves.androidApp.familyBrowser.helpers;

import java.io.IOException;

import android.database.sqlite.SQLiteDatabase;

/**
 * The class DatabaseSingletonFactory defines a SQLiteDatabase factory.
 * 
 * @author david@chaves.ca
 */
public class DatabaseSingletonFactory implements DatabaseFactory {
    
    protected static final String TAG = DatabaseSingletonFactory.class.getSimpleName();
    
    /** Database Open helper */
    private final DatabaseHelper m_helper;
    /** SQLiteDatabase singleton instance */
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
     * @return the SQLiteDatabase instance
     */
    @Override
    public SQLiteDatabase getDatabase() {
        // @see http://en.wikipedia.org/wiki/Double-checked_locking
        // note the usage of the local variable 'result' which seems
        // unnecessary. For some versions of the Java VM, it will
        // make the code 25% faster and for others, it won't hurt
        SQLiteDatabase result = this.m_database;
        if (result == null) {
            synchronized (this.m_helper) {
                result = this.m_database;
                if (result == null) {
                    Log.d(TAG, "{ getDatabase");
                    this.createMissingDatabaseFiles();
                    this.m_database = this.m_helper.getWritableDatabase();
                    result = this.m_database;
                    Log.d(TAG, "} getDatabase", result.getPath());
                }
            }
        }
        return result;
    }
    
    /**
     * This function checks if the database did not exist initially, in which
     * case it creates a new database from the raw resources defined by the
     * database model.
     */
    private void createMissingDatabaseFiles() {
        
        this.m_database = null; // just in case
        
        final DatabaseSingletonHelper helper = new DatabaseSingletonHelper(this.m_helper);
        final SQLiteDatabase db = helper.getWritableDatabase();
        final String path = db.getPath();
        
        final boolean wasDatabaseCreated = helper.wasDatabaseCreated();
        
        db.close();
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
