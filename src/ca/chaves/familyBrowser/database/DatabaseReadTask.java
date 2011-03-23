package ca.chaves.familyBrowser.database;

import ca.chaves.familyBrowser.helpers.Log;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class defines a database-specific AsyncTask to retrieve data from the
 * database. It is used to execute all SELECT statements in this application.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public abstract class DatabaseReadTask extends DatabaseAsyncTask {

    /** This is the SQL SELECT statement */
    private final String m_selectStmt;
    /** This is the SELECT statement arguments */
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
     * This function is the AsyncTask call-back used to execute the SQL SELECT
     * statement in background.
     */
    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, this.m_selectStmt);
        final SQLiteDatabase database = this.getDatabase();
        final Cursor cursor = database.rawQuery(this.m_selectStmt, this.m_selectionArgs);
        this.doProcessDatabaseResult(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}
