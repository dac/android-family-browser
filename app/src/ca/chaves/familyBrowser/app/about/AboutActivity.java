package ca.chaves.familyBrowser.app.about;

import ca.chaves.android.BuildManifest;
import ca.chaves.android.app.AbstractActivity;
import ca.chaves.android.util.Debug;
import ca.chaves.android.util.ErrorReporter;
import ca.chaves.familyBrowser.app.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * The "About" activity.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class AboutActivity
    extends AbstractActivity
{
    /**
     * Button: [Send Crash Reports].
     */
    private final OnClickListener onCrashClick = new OnClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick( final View view )
        {
            Debug.enter();
            ErrorReporter.checkErrorAndReport( AboutActivity.this );
            Debug.leave();
        }
    };

    /**
     * Button: [Close].
     */
    private final OnClickListener onOkayClick = new OnClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick( final View view )
        {
            Debug.enter();
            finish();
            Debug.leave();
        }
    };

    // ------------------
    // Life cycle methods
    // ------------------

    /**
     * The activity is being created.
     *
     * @param savedInstanceState saved instance state.
     */
    @Override
    public void onCreate( final Bundle savedInstanceState )
    {
        Debug.enter();
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_about );

        final String buildVersion = getString( R.string.build_version );
        final String buildUuid = getString( R.string.build_uuid );

        final String versionCode = Integer.toString( BuildManifest.VERSION_CODE );
        final String debugOrRelease = ( BuildManifest.DEBUG_ENABLED ? "DEBUG" : "release" );

        final TextView versionText = (TextView) findViewById( R.id.version_text );
        versionText.setText( String.format( "Version %1$s_%2$s-%3$s", buildVersion, versionCode, debugOrRelease ) );

        final TextView buildText = (TextView) findViewById( R.id.build_text );
        buildText.setText( String.format( "Build %1$s", buildUuid ) );

        final Button crashButton = (Button) findViewById( R.id.crash_button );
        crashButton.setOnClickListener( onCrashClick );

        final Button okButton = (Button) findViewById( R.id.ok_button );
        okButton.setOnClickListener( onOkayClick );

        Debug.leave();
    }
}
