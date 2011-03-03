package ca.chaves.androidApp.familyBrowser.helpers;

import android.database.sqlite.SQLiteDatabase;

/**
 * The class DatabaseWriteTask defines a database-specific AsyncTask to write
 * data into the database.
 * 
 * @author david@chaves.ca
 */
public abstract class DatabaseWriteTask extends DatabaseAsyncTask {
    
    /** SQL statement - something like INSERT/UPDATE/DELETE */
    private final String m_sqlStmt;
    /** SQL statement arguments */
    private final Object[] m_bindArgs;
    
    /**
     * Constructor.
     * 
     * @param databaseFactory
     * @param sqlStmt
     * @param bindArgs
     */
    public DatabaseWriteTask(final DatabaseFactory databaseFactory, final String sqlStmt, final Object[] bindArgs) {
        super(databaseFactory);
        this.m_sqlStmt = sqlStmt;
        this.m_bindArgs = bindArgs;
    }
    
    /**
     * AsyncTask call-back to execute the SQL statement in background.
     */
    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "{", this.m_sqlStmt);
        final SQLiteDatabase db = this.getDatabase();
        db.execSQL(this.m_sqlStmt, this.m_bindArgs);
        this.doProcessDatabaseResult(null);
        Log.d(TAG, "}", this.m_sqlStmt);
        return null;
    }
}
