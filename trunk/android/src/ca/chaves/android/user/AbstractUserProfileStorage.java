package ca.chaves.android.user;

import ca.chaves.android.profile.AbstractProfile;
import ca.chaves.android.profile.AbstractProfileStorage;
import ca.chaves.android.util.Debug;

import android.content.SharedPreferences;

/**
 * {@link AbstractUserProfile} storage.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class AbstractUserProfileStorage
    extends AbstractProfileStorage
{
    /**
     * Profile title - read only.
     */
    public static final String FIELD_TITLE = "title";

    /**
     * Profile locale code - read write.
     */
    public static final String FIELD_LOCALE = "lang";

    /**
     * The title value for "deleted" profiles. This is normally an illegal title, since all valid titles have spaces
     * trim'ed.
     */
    public static final String DELETED_TITLE = " ? ";

    // ----------------------
    // SharedPreferences name
    // ----------------------

    /**
     * Get {@link SharedPreferences}'s basename.
     *
     * @param index index.
     * @return the {@link SharedPreferences} name for the profile with the given <code>index</code>.
     */
    public static String getBasename( final int index )
    {
        final String decimal = Integer.toString( index );
        final int length = decimal.length();
        return ( length <= 1 ) ? ( "p0" + decimal ) : ( "p" + decimal );
    }

    /**
     * Get {@link SharedPreferences}'s basename.
     *
     * @param title title.
     * @return the {@link SharedPreferences} name for the profile with the given <code>title</code>.
     */
    public static String getBasename( final String title )
    {
        int index = 1;
        for ( ;; ++index )
        {
            final String basename = AbstractUserProfileStorage.getBasename( index );
            final SharedPreferences prefs = AbstractProfile.getSharedPreferences( basename );

            final String titleValue = prefs.getString( AbstractUserProfileStorage.FIELD_TITLE, null );
            if ( titleValue == null )
            {
                Debug.print( "profile title not found", basename, title );
                return null; // got latest SharedPreferences object
            }
            else if ( titleValue.equals( title ) )
            {
                Debug.print( "profile title found", basename, title );
                return basename;
            }
        }
    }

    /**
     * Get {@link SharedPreferences}'s basename.
     *
     * @return the {@link SharedPreferences} name for a brand-new profile.
     */
    public static String getBasename()
    {
        int index = 1;
        for ( ;; ++index )
        {
            final String basename = AbstractUserProfileStorage.getBasename( index );
            final SharedPreferences prefs = AbstractProfile.getSharedPreferences( basename );

            final String titleValue = prefs.getString( AbstractUserProfileStorage.FIELD_TITLE, null );
            if ( ( titleValue == null ) || DELETED_TITLE.equals( titleValue ) )
            {
                Debug.print( "reuse or add profile", basename, titleValue );
                return basename;
            }
        }
    }

    // --------------
    // I/O primitives
    // --------------

    /**
     * Read {@link AbstractUserProfile} from {@link SharedPreferences}.
     *
     * @param prefs prefs.
     * @param profile profile.
     */
    public static void readUserProfile( final SharedPreferences prefs, final AbstractUserProfile profile )
    {
        Debug.enter();
        profile.localeCode = prefs.getString( AbstractUserProfileStorage.FIELD_LOCALE, null );
        Debug.print( "load", profile.tag, profile.basename, profile.title, profile.localeCode );
        Debug.leave();
    }

    /**
     * Save {@link AbstractUserProfile} instance into its {@link SharedPreferences}.
     *
     * @param editor editor.
     * @param profile profile.
     */
    public static void writeUserProfile( final SharedPreferences.Editor editor, final AbstractUserProfile profile )
    {
        Debug.enter();
        editor.putString( AbstractUserProfileStorage.FIELD_TITLE, profile.title );
        editor.putString( AbstractUserProfileStorage.FIELD_LOCALE, profile.localeCode );
        Debug.print( "save", profile.tag, profile.basename, profile.title, profile.localeCode );
        Debug.leave();
    }
}
