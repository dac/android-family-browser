package ca.chaves.android.user;

import ca.chaves.android.R;
import ca.chaves.android.profile.AbstractProfile;
import ca.chaves.android.profile.AbstractProfileCRUD;
import ca.chaves.android.util.Debug;

import android.content.SharedPreferences;

/**
 * {@link UserProfile} CRUD operations.
 */
public abstract class UserProfileCRUD
    extends AbstractProfileCRUD
{
    // -----------------
    // Protected methods
    // -----------------

    /**
     * Load all {@link UserProfile} instances.
     *
     * @return list.
     */
    protected UserProfileList loadProfiles()
    {
        Debug.enter();

        final UserProfileList profiles = new UserProfileList();
        synchronized ( AbstractProfileCRUD.LOCK )
        {
            int index = 1;
            for ( ;; ++index )
            {
                final String basename = AbstractUserProfileStorage.getBasename( index );
                final SharedPreferences prefs = AbstractProfile.getSharedPreferences( basename );

                final String titleValue = prefs.getString( AbstractUserProfileStorage.FIELD_TITLE, null );
                if ( titleValue == null )
                {
                    Debug.print( "loaded", profiles.size(), "profiles" );
                    break; // got latest SharedPreferences object
                }
                else if ( AbstractUserProfileStorage.DELETED_TITLE.equals( titleValue ) )
                {
                    Debug.print( "ignore deleted title", basename, titleValue );
                    continue;
                }

                profiles.add( readProfile( prefs, basename, titleValue ) );
            }
        }

        Debug.leave( "loaded:", profiles.size() );
        return profiles;
    }

    /**
     * Delete {@link UserProfile} instance.
     *
     * @param profile to delete.
     * @return true on success, false otherwise.
     */
    protected boolean deleteProfile( final UserProfile profile )
    {
        Debug.enter();

        boolean success = false;
        if ( profile != null && profile.title != null )
        {
            // ensure this profile is not the "current profile"
            profile.deactivate();

            // then kill the associated SharedPreferences
            synchronized ( AbstractProfileCRUD.LOCK )
            {
                final SharedPreferences prefs = profile.getSharedPreferences();
                final SharedPreferences.Editor editor = prefs.edit();

                editor.clear();
                editor.putString( AbstractUserProfileStorage.FIELD_TITLE, AbstractUserProfileStorage.DELETED_TITLE );

                Debug.print( "delete", profile.tag, profile.basename, profile.title );
                success = check( editor.commit(), R.string.io_error );
            }
        }

        Debug.leave( "profile deleted?", success, ( profile != null ) ? profile.title : null );
        return success;
    }

    // --------------
    // I/O primitives
    // --------------

    /**
     * Load {@link UserProfile} instance from its {@link SharedPreferences}.
     *
     * @param prefs prefs.
     * @param basename basename.
     * @param title title.
     * @return UserProfile,
     */
    private UserProfile readProfile( final SharedPreferences prefs, final String basename, final String title )
    {
        final UserProfile profile = new UserProfile( basename, title );
        AbstractUserProfileStorage.readUserProfile( prefs, profile );
        return profile;
    }
}
