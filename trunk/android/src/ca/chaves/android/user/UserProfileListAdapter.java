package ca.chaves.android.user;

import ca.chaves.android.widget.TextListAdapter;

/**
 * The base list adapter for the user-profile list screen.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class UserProfileListAdapter
    extends TextListAdapter<UserProfile>
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getItemText( final UserProfile item )
    {
        return item.title;
    }
}
