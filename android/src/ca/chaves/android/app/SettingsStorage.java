package ca.chaves.android.app;

import ca.chaves.android.profile.AbstractProfileStorage;

/**
 * {@link Settings} storage.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class SettingsStorage
    extends AbstractProfileStorage
{
    /**
     * The basename for the application-wide SharedPreferences.
     */
    public static final String BASENAME = "app";

    /**
     * Key for the latest's extracted tarball version.
     */
    public static final String FIELD_TARBALL = "tarball";

    /**
     * Key for the latest's EULA accepted by the end-user. EULA = End User License Agreement
     */
    public static final String FIELD_EULA = "eula";

    /**
     * Key for the initial profile title.
     */
    public static final String FIELD_PROFILE = "profile";
}
