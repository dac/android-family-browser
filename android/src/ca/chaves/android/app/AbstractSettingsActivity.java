package ca.chaves.android.app;

import ca.chaves.android.R;
import ca.chaves.android.profile.AbstractProfileActivity;
import ca.chaves.android.user.AbstractUserProfile;
import ca.chaves.android.user.UserProfileList;
import ca.chaves.android.user.UserProfileCRUD;
import ca.chaves.android.util.Debug;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Base class for the {@link Settings} activity.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public abstract class AbstractSettingsActivity
    extends AbstractProfileActivity
{
    /**
     * Constructor.
     */
    protected AbstractSettingsActivity()
    {
        super( R.xml.screen_settings, Settings.INSTANCE );
        Debug.enter();
        addField( SettingsStorage.FIELD_PROFILE, new NonEmptyValidator() );
        Debug.leave();
    }

    // ------------------
    // Life cycle methods
    // ------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate( final Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        Debug.enter();

        // setup entries for the "list of user profiles"

        final class Task
            extends UserProfileCRUD
        {
            /**
             * The retrieved {@link UserProfileList}.
             */
            private UserProfileList profiles;

            /**
             * Load the list of profile titles.
             *
             * @return array of {@link AbstractUserProfile} titles loaded.
             */
            private String[] getUserProfileTitles()
            {
                Debug.enter();
                final String[] titles = new String[profiles.size()];
                int index = 0;
                for ( final AbstractUserProfile profile : profiles )
                {
                    titles[index++] = profile.title;
                }
                Debug.leave();
                return titles;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected boolean isCanceling()
            {
                return isTerminated();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected Void doInBackground( final Void... params )
            {
                Debug.enter();
                profiles = loadProfiles();
                Debug.leave();
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void onPostExecute( final Void result )
            {
                Debug.enter();
                if ( canceled() )
                {
                    Debug.print( "canceled", errorMessage );
                }
                else if ( failed() )
                {
                    flash( errorMessage );
                }
                else
                {
                    // setup title lists

                    final ListPreference preference = (ListPreference) getPreference( SettingsStorage.FIELD_PROFILE );

                    final String[] titles = getUserProfileTitles();

                    preference.setEntries( titles );
                    preference.setEntryValues( titles );
                }
                Debug.leave();
            }
        }

        new Task().execute();
        Debug.leave();
    }

    // ----------
    // Call backs
    // ----------

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu( final Menu menu )
    {
        Debug.enter();
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.activity_settings, menu );
        Debug.leave();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected( final MenuItem item )
    {
        Debug.enter();
        boolean handled = false;
        final int itemId = item.getItemId();
        if ( R.id.settings_about_menu_item == itemId )
        {
            final Intent intent = new Intent( this, Android.App.ABOUT_ACTIVITY_CLASS );
            startActivity( intent );
            handled = true;
        }
        else if ( R.id.settings_homepage_menu_item == itemId )
        {
            final Intent intent =
                new Intent( Intent.ACTION_VIEW, Uri.parse( getString( R.string.settings_homepage_menu_url ) ) );
            startActivity( intent );
            handled = true;
        }
        else if ( R.id.settings_language_menu_item == itemId )
        {
            // changes the system-wide language
            final Intent intent = new Intent( Intent.ACTION_MAIN );
            intent.setClassName( "com.android.settings", "com.android.settings.LanguageSettings" );
            startActivity( intent );
            handled = true;
        }
        else
        {
            Debug.print( "uknown itemId", itemId );
            handled = super.onOptionsItemSelected( item );
        }
        Debug.leave( handled );
        return handled;
    }
}
