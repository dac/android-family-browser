package ca.chaves.android.app;

import ca.chaves.android.profile.AbstractProfile;

/**
 * The application-wide profile singleton.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public final class Settings
    extends AbstractProfile
{
    /**
     * The application profile singleton.
     */
    public static final Settings INSTANCE = new Settings();

    /**
     * Constructor.
     */
    private Settings()
    {
        super( SettingsStorage.BASENAME, null );
    }
}
