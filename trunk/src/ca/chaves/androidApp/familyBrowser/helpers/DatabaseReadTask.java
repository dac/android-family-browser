package ca.chaves.androidApp.familyBrowser.helpers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * The class DatabaseReadTask defines a database-specific AsyncTask to retrieve
 * data from the database.
 * 
 * @author david@chaves.ca
 */
public abstract class DatabaseReadTask extends DatabaseAsyncTask {
    
    /** SQL SELECT statement */
    private final String m_selectStmt;
    /** SELECT statement arguments */
    private final String[] m_selectionArgs;
    
    /**
     * Constructor.
     * 
     * @param databaseFactory
     * @param selectStmt
     * @param selectionArgs
     */
    public DatabaseReadTask(final DatabaseFactory databaseFactory, final String selectStmt, final String[] selectionArgs) {
        super(databaseFactory);
        this.m_selectStmt = selectStmt;
        this.m_selectionArgs = selectionArgs;
    }
    
    /**
     * AsyncTask call-back to execute the SQL SELECT statement in background.
     */
    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "{", this.m_selectStmt);
        final SQLiteDatabase db = this.getDatabase();
        final Cursor cursor = db.rawQuery(this.m_selectStmt, this.m_selectionArgs);
        this.doProcessDatabaseResult(cursor);
        if (cursor != null) {
            cursor.close();
        }
        Log.d(TAG, "}", this.m_selectStmt);
        return null;
    }
}
