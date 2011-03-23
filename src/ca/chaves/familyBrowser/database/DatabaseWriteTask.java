package ca.chaves.familyBrowser.database;

import ca.chaves.familyBrowser.helpers.Log;

import android.database.sqlite.SQLiteDatabase;

/**
 * This class defines a database-specific AsyncTask to write data into the
 * database. All non-SELECT statements should be executed using this class.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public abstract class DatabaseWriteTask extends DatabaseAsyncTask {

    /** This is the SQL statement - something like INSERT/UPDATE/DELETE */
    private final String m_sqlStmt;
    /** This is the SQL statement arguments */
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
     * This AsyncTask call-back is used to execute the SQL statement in
     * background.
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
