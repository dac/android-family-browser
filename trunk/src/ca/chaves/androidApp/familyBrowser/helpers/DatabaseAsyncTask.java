package ca.chaves.androidApp.familyBrowser.helpers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * The class DatabaseAsyncTask defines a database-specific AsyncTask. This class
 * exists because all database I/O operations are performed asynchronously in
 * this application.
 * 
 * @see "http://developer.android.com/resources/articles/painless-threading.html"
 * @author david@chaves.ca
 */
public abstract class DatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
    
    protected static final String TAG = DatabaseAsyncTask.class.getSimpleName();
    
    /** Database factory */
    private final DatabaseFactory m_databaseFactory;
    
    /**
     * Constructor
     * 
     * @param databaseFactory
     */
    public DatabaseAsyncTask(final DatabaseFactory databaseFactory) {
        this.m_databaseFactory = databaseFactory;
    }
    
    /**
     * Return the SQLiteDatabase instance for this database.
     * 
     * @return the SQLiteDatabase database
     */
    protected SQLiteDatabase getDatabase() {
        return this.m_databaseFactory.getDatabase();
    }
    
    /**
     * Call-back to process the executed database cursor.
     * 
     * @param cursor
     */
    abstract protected void doProcessDatabaseResult(Cursor cursor);
    
    /**
     * Call-back to refresh the user-interface, once the database cursor has
     * been executed and processed.
     */
    abstract protected void doRefreshUserInterface();
    
    /**
     * AsyncTask call-back.
     */
    @Override
    protected void onPostExecute(Void result) {
        this.doRefreshUserInterface();
    }
}
