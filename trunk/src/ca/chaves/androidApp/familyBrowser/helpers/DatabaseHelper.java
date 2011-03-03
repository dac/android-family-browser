package ca.chaves.androidApp.familyBrowser.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The DatabaseHelper base class.
 * 
 * @author david@chaves.ca
 */
public abstract class DatabaseHelper extends SQLiteOpenHelper {
    
    protected static final String TAG = DatabaseHelper.class.getSimpleName();
    
    /** Application Context */
    private final Context m_context;
    /** Database name */
    private final String m_name;
    /** Database version */
    private final int m_version;
    /**
     * The raw resources to be used to copy the database file from, when the
     * database does not exist initially.
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
