package ca.chaves.androidApp.familyBrowser.helpers;

import android.database.sqlite.SQLiteDatabase;

/**
 * The DatabaseFactory interface.
 * 
 * @author david@chaves.ca
 */
public interface DatabaseFactory {
    public SQLiteDatabase getDatabase();
}
