package ca.chaves.familyBrowser.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * This interface creates and maintains SQLiteDatabase instances.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public interface DatabaseFactory {

    /**
     * Get an SQLiteDatabase instance.
     *
     * @return SQLiteDatabase
     */
    public SQLiteDatabase getDatabase();

    /**
     * Close all SQLiteDatabase instances that were returned by getDatabase().
     */
    public void closeDatabases();
}
