package ca.chaves.android.app;

import ca.chaves.android.util.AbstractController;
import ca.chaves.android.util.Controllable;
import ca.chaves.android.util.Debug;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

/**
 * {@link Activity} base class. All activities in the final application should be derived from this class.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public abstract class AbstractActivity
    extends Activity
    implements Controllable
{
    /**
     * Internal name, used to distinguish which instance is being used in log messages.
     */
    protected String tag = Debug.getUniqueTag( AbstractActivity.class );

    /**
     * Handler for posting actions.
     */
    protected final Handler handler = new Handler();

    /**
     * Has {@link AbstractActivity#onDestroy} been called?
     */
    private volatile boolean wasDestroyed;

    /**
     * The Activity controller.
     */
    private AbstractController controller;

    // -------
    // Setters
    // -------

    /**
     * Setter - set {@link AbstractController}.
     *
     * @param controller to setup.
     */
    protected void setController( final AbstractController controller )
    {
        Debug.enter( tag, controller );
        this.controller = controller;
        Debug.leave( tag );
    }

    // --------------------
    // Controller interface
    // --------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminated()
    {
        Debug.enter( tag );
        boolean terminated = false;
        if ( wasDestroyed || isFinishing() )
        {
            Debug.print( tag, "activity terminated" );
            terminated = wasDestroyed = true;
        }
        Debug.leave( tag, terminated );
        return terminated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onControllerUpdated()
    {
        Debug.print( tag, "model/view/controller updated" );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flash( final String message )
    {
        Debug.enter( tag, message );
        if ( message != null )
        {
            Toast.makeText( this, message, Toast.LENGTH_LONG ).show();
        }
        Debug.leave( tag );
    }

    // ----------
    // Life Cycle
    // ----------

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate( final Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        {
            Debug.print( tag, "create" );
            final Window window = getWindow();
            // increase the color range a lot
            window.setFormat( PixelFormat.RGBA_8888 );
            // activate dithering for all your activity
            window.addFlags( WindowManager.LayoutParams.FLAG_DITHER );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        {
            Debug.print( tag, "start" );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        {
            Debug.print( tag, "resume" );
            if ( controller != null )
            {
                controller.register( this );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause()
    {
        Debug.print( tag, "pause" );
        {
            super.onPause();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop()
    {
        Debug.print( tag, "stop" );
        {
            super.onStop();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRestart()
    {
        super.onRestart();
        {
            Debug.print( tag, "restart" );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy()
    {
        {
            Debug.print( tag, "destroy" );
            wasDestroyed = true;
        }
        super.onDestroy();
    }

    // ----------
    // Call backs
    // ----------

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRestoreInstanceState( final Bundle savedInstanceState )
    {
        super.onRestoreInstanceState( savedInstanceState );
        {
            Debug.print( tag, "restoreInstanceState" );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState( final Bundle instanceState )
    {
        {
            Debug.print( tag, "saveInstanceState" );
        }
        super.onSaveInstanceState( instanceState );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged( final Configuration newConfig )
    {
        super.onConfigurationChanged( newConfig );
        {
            Debug.enter();
            try
            {
                Debug.print( tag, "configurationChanged", newConfig.locale );
                Android.App.setLocale( newConfig.locale );
            }
            catch ( final IOException ex )
            {
                Debug.error( ex, tag, "unable to set new locale", newConfig.locale );
            }
            Debug.leave();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onNewIntent( final Intent intent )
    {
        super.onNewIntent( intent );
        {
            Debug.print( tag, "new intent", intent );
        }
    }
}
