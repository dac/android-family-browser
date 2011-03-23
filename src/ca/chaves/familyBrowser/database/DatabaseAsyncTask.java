package ca.chaves.familyBrowser.database;

import ca.chaves.familyBrowser.helpers.Log;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * This class defines a database-specific AsyncTask. It exists because all
 * database I/O operations are performed asynchronously in this application.
 *
 * @see "http://developer.android.com/resources/articles/painless-threading.html"
 * @author "David Chaves <david@chaves.ca>"
 */
public abstract class DatabaseAsyncTask extends AsyncTask<Void, Void, Void> {

    protected static final String TAG = DatabaseAsyncTask.class.getSimpleName();

    /** This is the database factory */
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
     * This function returns the SQLiteDatabase instance for this database.
     *
     * @return the SQLiteDatabase database
     */
    protected SQLiteDatabase getDatabase() {
        return this.m_databaseFactory.getDatabase();
    }

    /**
     * This call-back is used to process the executed database cursor.
     *
     * @param cursor
     */
    abstract protected void doProcessDatabaseResult(final Cursor cursor);

    /**
     * This call-back is used to refresh the user-interface, once the database
     * cursor has been executed and processed.
     */
    abstract protected void doRefreshUserInterface();

    /**
     * This is the AsyncTask call-back.
     */
    @Override
    protected void onPostExecute(Void result) {
        Log.d(TAG, "{ doRefreshUserInterface");
        this.doRefreshUserInterface();
        Log.d(TAG, "} doRefreshUserInterface");
    }
}
