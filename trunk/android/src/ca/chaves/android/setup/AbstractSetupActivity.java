package ca.chaves.android.setup;

import ca.chaves.android.R;
import ca.chaves.android.app.AbstractActivity;
import ca.chaves.android.app.Android;
import ca.chaves.android.app.SettingsCRUD;
import ca.chaves.android.profile.AbstractProfile;
import ca.chaves.android.util.AbstractAsyncTask;
import ca.chaves.android.util.Debug;

import android.os.AsyncTask;
import android.os.Bundle;

import java.io.IOException;
import java.util.Locale;
import java.util.Stack;

/**
 * This is the "Setup Activity" to initialize all assets in this application. This activity is NOT meant to be used
 * normally - it is needed just to initialize the run-time environment prior running all unit-tests.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public abstract class AbstractSetupActivity
    extends AbstractActivity
{
    /**
     * This flag becomes true when the setup task is completed.
     */
    protected volatile boolean setupCompleted;

    /**
     * This will contain any error message.
     */
    protected String setupErrorMessage;

    /**
     * This flag is true when all files are properly installed for sure.
     */
    protected boolean setupOkay;

    /**
     * The stack of {@link AsyncTask}(s) to be executed for the application setup process.
     */
    private final Stack<AbstractAsyncTask<Void, Void, Void>> steps = new Stack<AbstractAsyncTask<Void, Void, Void>>();

    /**
     * Constructor.
     */
    protected AbstractSetupActivity()
    {
        super();
        pushSetupStep( new PreloadGlobalAssetsTask() );
        pushSetupStep( new LoadEulaStatusTask() );
    }

    /**
     * Add another setup step.
     *
     * @param step to add.
     */
    protected void pushSetupStep( final AbstractAsyncTask<Void, Void, Void> step )
    {
        Debug.enter();
        if ( step != null )
        {
            steps.push( step );
        }
        Debug.leave();
    }

    /**
     * Execute next setup step. This function should be executed on all the Step's {@link AsyncTask#onPostExecute}
     */
    protected void executeNextSetupStep()
    {
        Debug.enter();
        if ( !steps.isEmpty() )
        {
            steps.pop().execute();
        }
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
        super.onCreate( savedInstanceState );
        {
            Debug.enter();
            executeNextSetupStep();
            Debug.leave();
        }
    }

    // -----------------
    // Protected methods
    // -----------------

    /**
     * Show the EULA and wait for the end-user agreement. This function is executed from the UI thread.
     *
     * @param missingEula true if EULA must be accepted.
     */
    protected abstract void showEulaIfNeeded( final boolean missingEula );

    /**
     * Do after all time-consuming initializations are complete. This function is executed from the UI thread.
     */
    protected abstract void continueIfPossible();

    // --------------
    // Nested classes
    // --------------

    /**
     * Base {@link AsyncTask} class.
     */
    private abstract class AbstractSetupTask
        extends SettingsCRUD
    {
        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean isCanceling()
        {
            return isTerminated(); // AbstractSetupActivity
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute( final Void result )
        {
            Debug.enter();

            if ( errorMessage != null )
            {
                setupErrorMessage = errorMessage;
            }

            if ( canceled() )
            {
                Debug.print( "canceled - ignoring" );
            }
            else if ( failed() )
            {
                Debug.print( "failed - quiting", errorMessage );
                finish(); // AbstractSetupActivity
            }
            else
            {
                doInForeground( result );
                executeNextSetupStep();
            }

            Debug.leave();
        }

        /**
         * Function to be done in foreground.
         *
         * @param result result.
         */
        protected void doInForeground( final Void result )
        {
            Debug.enter();
            // do nothing
            Debug.leave();
        }
    }

    /**
     * {@link AsyncTask} to do the heavy-lifting initialization.
     */
    private final class PreloadGlobalAssetsTask
        extends AbstractSetupTask
    {
        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground( final Void... params )
        {
            Debug.enter();
            try
            {
                // be sure all files and assets are properly installed
                // getHome()/getDatabase() also restore all missing files
                if ( !canceled() && !failed() )
                {
                    Android.App.getHome();
                    Android.App.getDatabase();
                }

                if ( !canceled() && !failed() )
                {
                    setupOkay = true;
                }
                Debug.print( "all assets initialized", setupOkay );
            }
            catch ( final IOException ex )
            {
                Debug.error( ex, "unable to setup assets" );
                error( R.string.io_error );
            }
            Debug.leave();
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doInForeground( final Void result )
        {
            Debug.enter();
            setupCompleted = true;
            continueIfPossible();
            Debug.leave();
        }
    }

    /**
     * {@link AsyncTask} to load the EULA.
     */
    private final class LoadEulaStatusTask
        extends AbstractSetupTask
    {
        /**
         * Has EULA already been accepted?
         */
        private boolean eulaOkay;

        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground( final Void... params )
        {
            Debug.enter();
            try
            {
                // be sure we have setup the locale properly
                // this needs to be done before the EULA is shown,
                // in order to guarantee it uses the proper locale
                if ( !canceled() && !failed() )
                {
                    final AbstractProfile current = AbstractProfile.active();
                    final Locale locale = current.getDefaultLocale();

                    Debug.print( "setup locale", locale, "from profile", current.title );
                    Android.App.setLocale( locale );
                }

                // load the EULA flag
                if ( !canceled() && !failed() )
                {
                    eulaOkay = hasEula();
                }
            }
            catch ( final IOException ex )
            {
                Debug.error( ex, "unable to load profile" );
                error( R.string.io_error );
            }
            Debug.leave();
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doInForeground( final Void result )
        {
            Debug.enter();
            // show the EULA dialog if needed
            showEulaIfNeeded( !eulaOkay );
            Debug.leave();
        }
    }

    // -----------------
    // Protected members
    // -----------------

    /**
     * {@link AsyncTask} to save the EULA.
     */
    protected static final class SaveEulaTask
        extends SettingsCRUD
    {
        /**
         * Constructor.
         */
        public SaveEulaTask()
        {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean isCanceling()
        {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground( final Void... params )
        {
            Debug.enter();
            saveEula();
            Debug.leave();
            return null;
        }
    }
}
