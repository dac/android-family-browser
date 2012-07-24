package ca.chaves.android.profile;

import ca.chaves.android.R;
import ca.chaves.android.app.Android;
import ca.chaves.android.app.Settings;
import ca.chaves.android.graph.GraphNode;
import ca.chaves.android.graph.GraphStorage;
import ca.chaves.android.util.Debug;

import android.content.SharedPreferences;
import android.content.res.Resources;

import java.util.Locale;

/**
 * The base "profile" class. We have "application" profiles and "user" profiles, derived from this class.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public abstract class AbstractProfile
{
    /**
     * Internal name, used to distinguish which instance is being used in log messages.
     */
    public String tag = Debug.getUniqueTag( AbstractProfile.class );

    /**
     * The internal name used with getSharedPreferences().
     */
    public final String basename;

    /**
     * The public name - the profile title, from the end-user point of view.
     */
    public final String title;

    /**
     * Global lock.
     */
    private static final Object LOCK = new Object();

    /**
     * The "current/active" profile must be initialized before any {@link AbstractProfile} singleton.
     */
    private static final class Active
    {
        /**
         * The "current" profile, globally. This value must be overwritten using {@Link Profile#activate()}
         */
        public static AbstractProfile current = Settings.INSTANCE;
    }

    /**
     * Lazy initialization of default values.
     */
    public static final class Default
    {
        /**
         * The cached "default language code".
         */
        public static final Locale LOCALE = getDefaultLocale();

        /**
         * Get the default locale, according to the supported languages.
         *
         * @return the "default 2-chars language code"
         */
        private static Locale getDefaultLocale()
        {
            final Resources resources = Android.App.INSTANCE.getResources();

            final Locale defaultLocale = Locale.getDefault();
            final String defaultLocaleCode = defaultLocale.getLanguage();

            if ( defaultLocaleCode != null )
            {
                final String[] validCodes = resources.getStringArray( R.array.profile_language_values );

                for ( final String code : validCodes )
                {
                    if ( code.startsWith( defaultLocaleCode ) )
                    {
                        Debug.print( "default language code", code, defaultLocaleCode );
                        return defaultLocale;
                    }
                }
            }
            Debug.print( "invalid language code", defaultLocaleCode );

            // the defaultCode is not one of the valid codes - therefore, return our own default
            final String localeCode = Android.App.INSTANCE.getString( R.string.profile_language_default );
            return Android.App.getLocale( localeCode );
        }
    }

    /**
     * Constructor.
     *
     * @param basename the {@link SharedPreferences} filename
     * @param title the end-user name
     */
    protected AbstractProfile( final String basename, final String title )
    {
        this.basename = basename;
        this.title = title;
    }

    /**
     * Deactivate this profile if it is the "current/active one".
     */
    public final void deactivate()
    {
        Debug.enter();
        // ensure this profile is not the "current profile"
        synchronized ( AbstractProfile.LOCK )
        {
            assert ( AbstractProfile.Active.current != null );

            if ( title != null && title.equals( AbstractProfile.Active.current.title ) )
            {
                Debug.print( "reset current profile", AbstractProfile.Active.current.tag,
                             AbstractProfile.Active.current.title );

                // the ApplicationProfile is a final singleton
                AbstractProfile.Active.current = Settings.INSTANCE;
            }
        }
        Debug.leave();
    }

    /**
     * Make this instance the "current/active one".
     */
    public final void activate()
    {
        Debug.enter();
        synchronized ( AbstractProfile.LOCK )
        {
            AbstractProfile.Active.current = this;
            Debug.print( "current profile", tag, title );
        }
        Debug.leave();
    }

    /**
     * Return the current/active profile.
     *
     * @return current profile.
     */
    public static AbstractProfile active()
    {
        synchronized ( AbstractProfile.LOCK )
        {
            return AbstractProfile.Active.current;
        }
    }

    // ------------------
    // Profile attributes
    // ------------------

    /**
     * Return the default {@link Locale} for this profile.
     *
     * @return default locale.
     */
    public Locale getDefaultLocale()
    {
        return AbstractProfile.Default.LOCALE;
    }

    /**
     * Return the default {@link GraphNode}'s nodeId for this profile.
     *
     * @return node_id
     */
    public Integer getGraphNodeId()
    {
        return Integer.valueOf( GraphStorage.DEFAULT_NODE_ID );
    }

    // -----------------
    // SharedPreferences
    // -----------------

    /**
     * Return the {@link SharedPreferences} for this 'basename'.
     *
     * @param basename the {@link SharedPreferences} filename
     * @return SharedPreferences
     */
    public static final SharedPreferences getSharedPreferences( final String basename )
    {
        Debug.print( "open profile", basename );
        return Android.App.INSTANCE.getSharedPreferences( basename, AbstractProfileStorage.MODE );
    }

    /**
     * Return the {@link SharedPreferences} for this profile.
     *
     * @return SharedPreferences
     */
    public final SharedPreferences getSharedPreferences()
    {
        return AbstractProfile.getSharedPreferences( basename );
    }
}
