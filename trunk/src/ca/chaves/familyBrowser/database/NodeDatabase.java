package ca.chaves.familyBrowser.database;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * This class encapsulates the creation of DatabaseFactory instances. It defines
 * a database according to NodeModel.
 *
 * @author "David Chaves <david@chaves.ca>"
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
     * This class is the NodeDatabase Helper class used to open the SQLite
     * database. It manages versions and database creation.
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
         * This is an internal function used to ensure that all the database
         * objects (tables, indexes) are created.
         *
         * @param db
         */
        private void setupDatabaseObjects(final SQLiteDatabase db) {
            // just ensure that this database has the INDEXes we need...
            db.execSQL(NodeModel.CREATE_NODES_INDEX_SQL);
        }

        /**
         * This call-back function is called when the database is being created.
         * This function should really never be called, since database must
         * always exist!
         */
        @Override
        public void onCreate(final SQLiteDatabase db) {
            this.setupDatabaseObjects(db);
        }

        /**
         * This call-back is called when the existing database is open.
         */
        @Override
        public void onOpen(final SQLiteDatabase db) {
            this.setupDatabaseObjects(db);
        }

        /**
         * This call-back is called when the existing database is upgraded.
         */
        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            this.setupDatabaseObjects(db);
        }
    }
}
