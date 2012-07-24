package ca.chaves.android.user;

import java.util.Locale;

import ca.chaves.android.app.Android;
import ca.chaves.android.profile.AbstractProfile;
import ca.chaves.android.util.Debug;

/**
 * Abstract "user profile".
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public abstract class AbstractUserProfile
    extends AbstractProfile
{
    /**
     * Profile setting: user language.
     */
    public String localeCode;

    /**
     * Constructor.
     *
     * @param basename basename.
     * @param title title.
     */
    protected AbstractUserProfile( final String basename, final String title )
    {
        super( basename, title );
    }

    // ------------------
    // Profile attributes
    // ------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getDefaultLocale()
    {
        Debug.enter();
        Locale locale = null;
        if ( localeCode != null )
        {
            locale = Android.App.getLocale( localeCode );
        }
        if ( locale == null )
        {
            locale = super.getDefaultLocale();
        }
        Debug.leave( locale );
        return locale;
    }
}
