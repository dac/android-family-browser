package ca.chaves.familyBrowser.database;

import ca.chaves.familyBrowser.helpers.Log;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is a SQLiteOpenHelper which tell us if the database was just
 * created in this process. This class is used by class DatabaseSingletonFactory
 * to determine if the database files must be copied from raw resources.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
class DatabaseSingletonHelper extends SQLiteOpenHelper {

    protected static final String TAG = DatabaseSingletonHelper.class.getSimpleName();

    /** This is true if the database was just created in this process */
    private boolean m_wasDatabaseCreated;

    /**
     * Constructor.
     *
     * @param helper
     */
    public DatabaseSingletonHelper(final DatabaseHelper helper) {
        super(helper.getDatabaseContext(), helper.getDatabaseName(), null, helper.getDatabaseVersion());
    }

    /**
     * @return true if the database was just created.
     */
    public boolean wasDatabaseCreated() {
        Log.d(TAG, "wasDatabaseCreated? " + this.m_wasDatabaseCreated);
        return this.m_wasDatabaseCreated;
    }

    /**
     * This call-back is called when the database is being created. Note that
     * the database is created as an empty database, with no tables nor indexes.
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        this.m_wasDatabaseCreated = true;
    }

    /**
     * This call-back is called when a existent database is being upgraded.
     */
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.d(TAG, "onUpgrade");
    }
}
