package ca.chaves.android.profile;

import ca.chaves.android.BuildManifest;
import ca.chaves.android.R;
import ca.chaves.android.app.AbstractPreferenceActivity;
import ca.chaves.android.app.Android;
import ca.chaves.android.util.Debug;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * {@link PreferenceActivity} with field validation primitives.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public abstract class AbstractProfileActivity
    extends AbstractPreferenceActivity
{
    /**
     * The res/xml/resource containing the {@link AbstractPreferenceActivity} screen definition.
     */
    private final int xmlId;

    /**
     * The profile preferences to edit.
     *
     * @see {@link SharedPreferences}
     */
    private final AbstractProfile profile;

    /**
     * The profile keys to edit. The key values will be loaded from the preferences .xml file if this value is left
     * null.
     */
    private final LinkedList<String> preferenceKeys = new LinkedList<String>();

    /**
     * The map of key-value validators. These validators are called on each key stroke.
     */
    private final HashMap<String, Validator> validatorMap = new HashMap<String, Validator>();

    /**
     * Constructor.
     *
     * @param xmlId the {@link PreferenceActivity} xml id
     * @param profile the {@link AbstractProfile}'s preferences to edit
     */
    protected AbstractProfileActivity( final int xmlId, final AbstractProfile profile )
    {
        Debug.enter();

        this.xmlId = xmlId;
        this.profile = profile;

        // sanity checks
        if ( BuildManifest.DEBUG_ENABLED )
        {
            if ( profile == null )
            {
                throw new RuntimeException( "HORROR: no profile" );
            }
            if ( preferenceKeys == null )
            {
                throw new RuntimeException( "HORROR: no preferenceKeys" );
            }
            if ( validatorMap == null )
            {
                throw new RuntimeException( "HORROR: no validatorMap" );
            }
        }

        Debug.leave( profile.tag, profile.basename );
    }

    /**
     * Add preference field.
     *
     * @param key for the preference to add
     * @param validator the validator for the 'key' preference
     */
    protected final void addField( final String key, final Validator validator )
    {
        Debug.enter( profile.tag, profile.basename, key, validator );
        preferenceKeys.add( key );
        validatorMap.put( key, validator );
        Debug.leave( profile.tag, profile.basename );
    }

    // ----------
    // Life Cycle
    // ----------

    /**
     * The activity is being created.
     *
     * @param savedInstanceState saved instance state.
     */
    @Override
    public void onCreate( final Bundle savedInstanceState )
    {
        Debug.enter( profile.tag, profile.basename );

        super.onCreate( savedInstanceState );
        setupProfileActivity();

        // sanity checks
        if ( BuildManifest.DEBUG_ENABLED )
        {
            for ( final String key : preferenceKeys )
            {
                if ( validatorMap.get( key ) == null )
                {
                    throw new RuntimeException( "HORROR: no validator: " + key );
                }
                if ( oldPreferenceSummaryMap.get( key ) == null )
                {
                    throw new RuntimeException( "HORROR: no old summary: " + key );
                }
                if ( getPreference( key ) == null )
                {
                    throw new RuntimeException( "HORROR: no preference: " + key );
                }
            }
        }

        Debug.leave( profile.tag, profile.basename );
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting
     * with the user.
     */
    @Override
    public void onResume()
    {
        Debug.enter( profile.tag, profile.basename );
        super.onResume();
        validateProfileValues();
        // set nicer "summary" lines if possible
        setupBetterPreferenceSummaryValues();
        // set listeners up whenever a key changes
        prefs.registerOnSharedPreferenceChangeListener( onChangeListener );
        Debug.leave( profile.tag, profile.basename );
    }

    /**
     * The activity is going into the background, but has not (yet) been killed.
     */
    @Override
    protected void onPause()
    {
        Debug.enter( profile.tag, profile.basename );
        super.onPause();
        // unregister the listener whenever a key changes
        prefs.unregisterOnSharedPreferenceChangeListener( onChangeListener );
        Debug.leave( profile.tag, profile.basename );
    }

    // ----------
    // Call backs
    // ----------

    /**
     * This function is called when Configuration changes, as defined by android:configChanges for this activity in
     * AndroidManifest.xml.
     *
     * @param newConfig new configuration.
     */
    @Override
    public void onConfigurationChanged( final Configuration newConfig )
    {
        Debug.enter( profile.tag, profile.basename );
        super.onConfigurationChanged( newConfig );
        addPreferencesFromResource( xmlId );
        Debug.leave( profile.tag, profile.basename );
    }

    // -----------------
    // Protected Methods
    // -----------------

    /**
     * The {@link SharedPreferences} for the profile being edited.
     */
    protected SharedPreferences prefs;

    /**
     * The {@link PreferenceScreen} for the profile being edited.
     */
    protected PreferenceScreen screen;

    /**
     * Load the value associated to the given 'key', from the SharedPreferences 'prefs'.
     *
     * @param key the preference key to get the value for.
     * @return value for key, or "" if not defined yet.
     */
    protected String getValue( final String key )
    {
        final String value = prefs.getString( key, "" ).trim();
        Debug.print( profile.tag, profile.basename, "read", key, value );
        return value;
    }

    /**
     * Return the {@link Preference} for the given 'key'.
     *
     * @param key the preference key to get.
     * @return valid {@link Preference}, or null if no {@link Preference} found.
     */
    protected Preference getPreference( final String key )
    {
        try
        {
            return screen.findPreference( key );
        }
        catch ( final NullPointerException ex )
        {
            Debug.print( profile.tag, profile.basename, "preference not found:", key );
            return null;
        }
    }

    /**
     * Enable/disable a {@link Preference} based on a given 'condition'.
     *
     * @param key preference to enable or disable
     * @param condition to be true in order to enable the 'key' preference
     */
    protected void enableIf( final String key, final boolean condition )
    {
        Debug.print( profile.tag, profile.basename, ( condition ? "enable" : "disable" ), "preference", key );
        getPreference( key ).setEnabled( condition );
    }

    /**
     * Change language for this application only.
     *
     * @param localeCode something like "de" or "en".
     * @throws IOException on error.
     * @see "http://adrianvintu.com/blogengine/post/Force-Locale-on-Android.aspx"
     * @see "http://developer.android.com/guide/topics/resources/localization.html"
     * @see "http://code.google.com/p/languagepickerwidget/"
     */
    protected void setLocale( final String localeCode )
        throws IOException
    {
        Debug.enter( profile.tag, profile.basename, localeCode );
        Android.App.setLocale( localeCode );
        // Reload all components without "closing" the application. This is
        // necessary when the user language changes, in order to update the
        // display language on all existent activities
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_SENSOR );
        Debug.leave( profile.tag, profile.basename, localeCode );
    }

    // ---------------
    // Private methods
    // ---------------

    /**
     * This function is called from {@link onCreate} to load the preference .xml file and setup all preference
     * listeners.
     */
    private void setupProfileActivity()
    {
        Debug.enter( profile.tag, profile.basename );
        final PreferenceManager preferenceManager = getPreferenceManager();

        preferenceManager.setSharedPreferencesName( profile.basename );
        preferenceManager.setSharedPreferencesMode( AbstractProfileStorage.MODE );

        setContentView( R.layout.activity_profile );
        addPreferencesFromResource( xmlId );

        Debug.print( "editing profile preferences", profile.tag, profile.basename, profile.title );

        prefs = profile.getSharedPreferences();
        screen = getPreferenceScreen();

        setupPreferenceValidators();
        saveOldPreferenceSummaryValues();
        Debug.leave( profile.tag, profile.basename );
    }

    /**
     * Setup {@link Preference} listeners to support preference validation.
     */
    private void setupPreferenceValidators()
    {
        Debug.enter( profile.tag, profile.basename );
        for ( final String key : preferenceKeys )
        {
            final Preference item = getPreference( key );
            final Validator validator = validatorMap.get( key );
            if ( validator != null )
            {
                Debug.print( profile.tag, profile.basename, "preference validator", key, validator );
                item.setOnPreferenceChangeListener( validator );
            }
            else
            {
                Debug.print( profile.tag, profile.basename, "preference never validated", key );
            }
        }
        Debug.leave( profile.tag, profile.basename );
    }

    /**
     * Validate all preference values.
     */
    private void validateProfileValues()
    {
        Debug.enter( profile.tag, profile.basename );
        for ( final String key : preferenceKeys )
        {
            final String value = getValue( key );
            final Validator validator = validatorMap.get( key );
            if ( validator != null )
            {
                Debug.print( profile.tag, profile.basename, "validate", key, value );
                validator.validateValue( value );
            }
            else
            {
                Debug.print( profile.tag, profile.basename, "skip", key );
            }
        }
        Debug.leave( profile.tag, profile.basename );
    }

    // --------------
    // Summary values
    // --------------

    /**
     * The map of key-summary original texts. These summaries are used to restore the original summary, when the
     * preference value is not set.
     */
    private final HashMap<String, CharSequence> oldPreferenceSummaryMap = new HashMap<String, CharSequence>();

    /**
     * Listen to changes in the SharedPreferences.
     */
    private final OnSharedPreferenceChangeListener onChangeListener = new OnSharedPreferenceChangeListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onSharedPreferenceChanged( final SharedPreferences sharedPreferences, final String key )
        {
            Debug.enter( profile.tag, profile.basename, key );
            setupPreferenceSummaryValue( key );
            Debug.leave( profile.tag, profile.basename, key );
        }
    };

    /**
     * Display the current values in the Summary lines, if possible.
     */
    private void setupBetterPreferenceSummaryValues()
    {
        Debug.enter( profile.tag, profile.basename );

        // set up initial summary values for all preferences
        for ( final String key : preferenceKeys )
        {
            setupPreferenceSummaryValue( key );
        }

        Debug.leave( profile.tag, profile.basename );
    }

    /**
     * Display the current value in the Preference Summary line.
     */
    private void setupPreferenceSummaryValue( final String key )
    {
        Debug.enter( profile.tag, profile.basename );
        final Preference preference = getPreference( key );
        if ( preference == null )
        {
            Debug.print( profile.tag, profile.basename, "ignore", key );
        }
        else if ( preference instanceof EditTextPreference )
        {
            final EditTextPreference pref = (EditTextPreference) preference;
            setupPreferenceSummaryValue( preference, key, pref.getText() );
        }
        else if ( preference instanceof ListPreference )
        {
            final ListPreference pref = (ListPreference) preference;
            setupPreferenceSummaryValue( preference, key, pref.getEntry() );
        }
        else
        {
            Debug.print( profile.tag, profile.basename, "skip", key, preference.getClass() );
        }
        Debug.leave();
    }

    /**
     * Display the 'Summary' value.
     */
    private void setupPreferenceSummaryValue( final Preference preference, final String key, final CharSequence value )
    {
        Debug.enter( profile.tag, profile.basename, key, value );
        CharSequence summary = value;
        if ( ( summary == null ) || ( summary.length() <= 0 ) )
        {
            summary = oldPreferenceSummaryMap.get( key );
        }
        preference.setSummary( ( summary == null ) ? "" : summary );
        Debug.leave( profile.tag, profile.basename );
    }

    /**
     * Save original 'Summary' values.
     */
    private void saveOldPreferenceSummaryValues()
    {
        Debug.enter( profile.tag, profile.basename );
        // save all predefined summary values
        for ( final String key : preferenceKeys )
        {
            final Preference preference = getPreference( key );
            if ( preference != null )
            {
                oldPreferenceSummaryMap.put( key, preference.getSummary() );
            }
        }
        Debug.leave( profile.tag, profile.basename );
    }

    // ------------------
    // Validation classes
    // ------------------

    /**
     * The {@link Preference} validation base class.
     */
    public abstract class Validator
        implements Preference.OnPreferenceChangeListener
    {
        /**
         * Validate the given value.
         *
         * @param value to validate.
         * @return error message if the value is invalid, or null if no errors.
         */
        public abstract String validateValue( final String value );

        /**
         * Return resource string.
         *
         * @param resId the resource id for the string to return.
         * @return the resource string for 'resId'.
         */
        public final String getString( final int resId )
        {
            return AbstractProfileActivity.this.getString( resId );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onPreferenceChange( final Preference preference, final Object newValue )
        {
            Debug.enter( profile.tag, profile.basename );
            final String value = ( ( newValue == null ) ? "" : newValue.toString() );
            final String error = validateValue( value );
            final boolean success = ( error == null );
            if ( !success )
            {
                final String text = String.format( this.getString( R.string.profile_error ), error );
                flash( text );
            }
            Debug.leave( profile.tag, profile.basename, success );
            return success;
        }
    }

    /**
     * Validate field must not be empty.
     */
    public class NonEmptyValidator
        extends Validator
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public String validateValue( final String value )
        {
            if ( ( value == null ) || ( value.length() <= 0 ) )
            {
                return getString( R.string.empty_value_error );
            }
            return null; // success - no error
        }
    }

    /**
     * Validate nothing. This is needed {@link ListPreference} and some passwords.
     */
    public class DoNothingValidator
        extends Validator
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public String validateValue( final String value )
        {
            return null; // success - no error
        }
    }
}
