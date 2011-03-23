package ca.chaves.familyBrowser.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class is the open helper base class we use in this application. It is
 * needed because we need to have access to the SQLiteOpenHelper constructor
 * parameters later on.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public abstract class DatabaseHelper extends SQLiteOpenHelper {

    protected static final String TAG = DatabaseHelper.class.getSimpleName();

    /** This is the Application Context */
    private final Context m_context;
    /** This is the database name */
    private final String m_name;
    /** This is the database version */
    private final int m_version;
    /**
     * This is the raw resources to be used to copy the database file from, when
     * the database does not exist initially.
     */
    private final Integer[] m_rawResources;

    /**
     * Constructor.
     *
     * @param context
     * @param name
     * @param version
     * @param rawResources
     */
    public DatabaseHelper(final Context context, final String name, final int version, final Integer[] rawResources) {
        super(context, name, null, version);
        this.m_context = context;
        this.m_name = name;
        this.m_version = version;
        this.m_rawResources = rawResources;
    }

    /**
     * @return the application context
     */
    protected Context getDatabaseContext() {
        return this.m_context;
    }

    /**
     * @return the database name
     */
    protected String getDatabaseName() {
        return this.m_name;
    }

    /**
     * @return the database version
     */
    protected int getDatabaseVersion() {
        return this.m_version;
    }

    /**
     * @return the raw resources to be used to copy the database file from, when
     *         the database does not exist initially.
     */
    protected Integer[] getRawResources() {
        return this.m_rawResources;
    }
}
