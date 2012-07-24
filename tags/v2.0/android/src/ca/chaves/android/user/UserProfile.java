package ca.chaves.android.user;

/**
 * Simplified "user profile".
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class UserProfile
    extends AbstractUserProfile
{
    /**
     * Constructor.
     *
     * @param basename basename.
     * @param title title.
     */
    UserProfile( String basename, String title )
    {
        super( basename, title );
    }
}
