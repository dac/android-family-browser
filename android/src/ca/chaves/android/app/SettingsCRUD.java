package ca.chaves.android.app;

import ca.chaves.android.R;
import ca.chaves.android.profile.AbstractProfile;
import ca.chaves.android.profile.AbstractProfileCRUD;
import ca.chaves.android.util.Debug;

import android.content.SharedPreferences;

/**
 * {@link Settings} CRUD operations.
 */
public abstract class SettingsCRUD
    extends AbstractProfileCRUD
{
    // ---------------
    // Current profile
    // ---------------

    /**
     * Return default {@link AbstractProfile} title.
     *
     * @return the default profile title.
     */
    protected String getDefaultProfileTitle()
    {
        Debug.enter();
        String title;
        synchronized ( AbstractProfileCRUD.LOCK )
        {
            final SharedPreferences prefs = Settings.INSTANCE.getSharedPreferences();
            title = prefs.getString( SettingsStorage.FIELD_PROFILE, null );
        }
        Debug.leave( "default profile:", title );
        return title;
    }

    /**
     * Save the new profile title.
     *
     * @param title to save.
     * @return true on success, false otherwise.
     */
    protected boolean saveDefaultProfileTitle( final String title )
    {
        Debug.enter();
        boolean success;
        synchronized ( AbstractProfileCRUD.LOCK )
        {
            final SharedPreferences prefs = Settings.INSTANCE.getSharedPreferences();
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putString( SettingsStorage.FIELD_PROFILE, title );
            success = check( editor.commit(), R.string.io_error );
        }
        Debug.leave( "profile saved?", success, title );
        return success;
    }

    // --------------------------
    // End User License Agreement
    // --------------------------

    /**
     * Has EULA been accepted?
     *
     * @return true if the user has already accorded on the EULA.
     */
    protected boolean hasEula()
    {
        Debug.enter();
        boolean accepted = true;
        if ( Android.App.EULA_VERSION != null )
        {
            synchronized ( AbstractProfileCRUD.LOCK )
            {
                final SharedPreferences prefs = Settings.INSTANCE.getSharedPreferences();
                final String oldEula = prefs.getString( SettingsStorage.FIELD_EULA, null );
                accepted = Android.App.EULA_VERSION.equals( oldEula );
            }
        }
        Debug.leave( "has eula?", accepted, Android.App.EULA_VERSION );
        return accepted;
    }

    /**
     * Save the EULA agreement status.
     *
     * @return true on success, false otherwise.
     */
    protected boolean saveEula()
    {
        Debug.enter();
        boolean success = true;
        if ( Android.App.EULA_VERSION != null )
        {
            synchronized ( AbstractProfileCRUD.LOCK )
            {
                final SharedPreferences prefs = Settings.INSTANCE.getSharedPreferences();
                final SharedPreferences.Editor editor = prefs.edit();

                editor.putString( SettingsStorage.FIELD_EULA, Android.App.EULA_VERSION );

                success = check( editor.commit(), R.string.io_error );
            }
        }
        Debug.leave( "eula saved?", success, Android.App.EULA_VERSION );
        return success;
    }
}
