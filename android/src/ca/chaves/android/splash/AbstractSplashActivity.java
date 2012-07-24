package ca.chaves.android.splash;

import ca.chaves.android.R;
import ca.chaves.android.app.Android;
import ca.chaves.android.setup.AbstractSetupActivity;
import ca.chaves.android.util.Debug;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * The Splash / Greetings Activity. It is meant to be short-lived.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public abstract class AbstractSplashActivity
    extends AbstractSetupActivity
{
    /**
     * This flag becomes true when the setup task is completed.
     */
    private boolean eulaAccepted;

    /**
     * The EULA view.
     */
    private View eulaSection;

    /**
     * The animation image.
     */
    private ImageView animationImage;

    /**
     * The animation widget.
     */
    private AnimationDrawable animationDrawable;

    /**
     * Action: View EULA. Start the Web Browser to view the EULA URI.
     */
    private OnClickListener onEulaViewClick = new OnClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick( View view )
        {
            Debug.enter();
            actionViewEula();
            Debug.leave();
        }
    };

    /**
     * Action: Accept EULA.
     */
    private OnClickListener onEulaAcceptClick = new OnClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick( View view )
        {
            Debug.enter();
            actionAcceptEula();
            Debug.leave();
        }
    };

    /**
     * Action: Decline EULA.
     */
    private OnClickListener onEulaDeclineClick = new OnClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick( View view )
        {
            Debug.enter();
            actionDeclineEula();
            Debug.leave();
        }
    };

    /**
     * View EULA online.
     */
    private void actionViewEula()
    {
        final String eulaUri = getString( R.string.eula_view_uri );
        final Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( Uri.parse( eulaUri ) );
        Debug.print( "eulaUri", eulaUri, intent );
        startActivity( intent );
    }

    /**
     * Accept EULA. Hide the EULA section and show the animation.
     */
    private void actionAcceptEula()
    {
        Debug.enter();
        // start a background task to save the EULA status
        new SaveEulaTask().execute();
        // continue our normal work-flow
        eulaAccepted = true;
        eulaSection.setVisibility( View.GONE );
        animationImage.setVisibility( View.VISIBLE );
        continueIfPossible();
        Debug.leave();
    }

    /**
     * Decline EULA.
     */
    private void actionDeclineEula()
    {
        Debug.enter();
        Debug.print( "decline EULA - quiting" );
        finish(); // AbstractSplashActivity
        Debug.leave();
    }

    // ------------------
    // Life cycle methods
    // ------------------

    /**
     * This function is called when the activity is being created.
     *
     * @param savedInstanceState saved instance state.
     */
    @Override
    public void onCreate( final Bundle savedInstanceState )
    {
        Debug.enter();
        super.onCreate( savedInstanceState );

        // setup the splash screen
        setContentView( R.layout.activity_splash );

        // setup the end-user license agreement (EULA) section
        eulaSection = findViewById( R.id.splash_eula_section );

        final TextView eulaContent = (TextView) findViewById( R.id.eula_content );
        eulaContent.setMovementMethod( ScrollingMovementMethod.getInstance() );

        final Button eulaView = (Button) findViewById( R.id.eula_view_button );
        eulaView.setOnClickListener( onEulaViewClick );

        final Button eulaAccept = (Button) findViewById( R.id.eula_accept_button );
        eulaAccept.setOnClickListener( onEulaAcceptClick );

        final Button eulaDecline = (Button) findViewById( R.id.eula_decline_button );
        eulaDecline.setOnClickListener( onEulaDeclineClick );

        // setup the animation section
        animationImage = (ImageView) findViewById( R.id.splash_animation );
        animationDrawable = (AnimationDrawable) animationImage.getDrawable();

        // are we there yet?
        continueIfPossible();
        Debug.leave();
    }

    /**
     * Called when the current Window of the activity gains or loses focus.
     *
     * @param hasFocus true if getting focus.
     */
    @Override
    public void onWindowFocusChanged( final boolean hasFocus )
    {
        Debug.enter();
        if ( hasFocus )
        {
            animationDrawable.start();
        }
        else
        {
            animationDrawable.stop();
        }
        Debug.leave();
    }

    // -----------------
    // Protected Methods
    // -----------------

    /**
     * {@inheritDoc}
     */
    @Override
    protected void showEulaIfNeeded( final boolean missingEula )
    {
        // adjust visibility
        if ( missingEula )
        {
            eulaSection.setVisibility( View.VISIBLE );
            animationImage.setVisibility( View.GONE );
        }
        else
        {
            eulaAccepted = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void continueIfPossible()
    {
        Debug.enter();
        if ( isTerminated() )
        {
            Debug.print( "terminated" );
        }
        else if ( !eulaAccepted )
        {
            // the background setup task is still working
            Debug.print( "eula not accepted yet" );
        }
        else if ( !setupCompleted )
        {
            // the background setup task is still working
            Debug.print( "setup still in progress" );
        }
        else if ( !setupOkay || setupErrorMessage != null )
        {
            Debug.print( "setup failed - quiting", setupErrorMessage );
            // some files could not be installed
            flash( setupErrorMessage );
            // bye, bye, SplashActivity
            finish();
        }
        else
        {
            // start the main activity
            startActivity( new Intent( this, Android.App.MAIN_ACTIVITY_CLASS ) );
            // bye, bye, SplashActivity
            finish();
        }
        Debug.leave();
    }
}
