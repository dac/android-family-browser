package ca.chaves.familyBrowser.app.bookmark;

import ca.chaves.android.graph.NavigationMenu;
import ca.chaves.android.profile.AbstractProfile;
import ca.chaves.android.profile.AbstractProfileActivity;
import ca.chaves.android.user.AbstractUserProfileStorage;
import ca.chaves.android.util.Debug;
import ca.chaves.familyBrowser.app.R;
import ca.chaves.familyBrowser.app.browser.BrowserController;

import android.os.Bundle;
import android.preference.ListPreference;

/**
 * The {@link Bookmark} Activity.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class BookmarkActivity
    extends AbstractProfileActivity
{
    /**
     * Constructor.
     */
    public BookmarkActivity()
    {
        super( R.xml.screen_bookmark, AbstractProfile.active() );
        Debug.enter();
        addField( AbstractUserProfileStorage.FIELD_LOCALE, new NonEmptyValidator() );
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
        {
            Debug.enter();

            // setup entries for the "list of navigation nodeIds"

            final NavigationMenu menu = new NavigationMenu( BrowserController.NAVIGATION_PATH );

            // convert nodeIds[] into String[]
            final String[] values = new String[menu.nodeIds.length];
            for ( int index = 0; index < menu.nodeIds.length; ++index )
            {
                values[index] = menu.nodeIds[index].toString();
            }

            final ListPreference preference = (ListPreference) getPreference( BookmarkStorage.FIELD_NODE_ID );

            preference.setEntries( menu.nodeLabels );
            preference.setEntryValues( values );
        }

        Debug.leave();
    }
}
