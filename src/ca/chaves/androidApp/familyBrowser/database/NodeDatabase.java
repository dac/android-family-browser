package ca.chaves.androidApp.familyBrowser.database;

import ca.chaves.androidApp.familyBrowser.helpers.DatabaseFactory;
import ca.chaves.androidApp.familyBrowser.helpers.DatabaseHelper;
import ca.chaves.androidApp.familyBrowser.helpers.DatabaseSingletonFactory;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * The class NodeDatabase encapsulates the creation of DatabaseFactory. This
 * class defines a database according to the NodeModel.
 * 
 * @author david@chaves.ca
 */
public class NodeDatabase {
    
    /**
     * Create a DatabaseFactory instance.
     * 
     * @param activity
     * @return a new DatabaseFactory instance
     */
    public static DatabaseFactory getDatabaseFactory(final Activity activity) {
        final Context context = activity.getApplicationContext();
        final NodeDatabaseHelper helper = new NodeDatabaseHelper(context);
        return new DatabaseSingletonFactory(helper);
    }
    
    /**
     * Helper to the database, manages versions and creation.
     */
    private static class NodeDatabaseHelper extends DatabaseHelper {
        
        /**
         * Constructor.
         * 
         * @param context
         */
        public NodeDatabaseHelper(final Context context) {
            super(context, NodeModel.DATABASE_NAME, NodeModel.DATABASE_VERSION, NodeModel.DATABASE_RAW_FILES);
        }
        
        /**
         * Internal function to ensure that all the database objects (tables,
         * indexes) are created.
         * 
         * @param db
         */
        private void setupDatabaseObjects(final SQLiteDatabase db) {
            db.execSQL(NodeModel.CREATE_NODES_INDEX_SQL);
        }
        
        /**
         * Call-back function to be called when the database is being created.
         * This function should really never be called, since database must
         * always exist!
         */
        @Override
        public void onCreate(final SQLiteDatabase db) {
            this.setupDatabaseObjects(db);
        }
        
        /**
         * Call-back function to be called when the existing database is open.
         */
        @Override
        public void onOpen(final SQLiteDatabase db) {
            this.setupDatabaseObjects(db);
        }
        
        /**
         * Call-back function to be called when the existing database is
         * upgraded.
         */
        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            this.setupDatabaseObjects(db);
        }
    }
}
