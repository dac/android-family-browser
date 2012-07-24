package ca.chaves.familyBrowser.app.setup;

import ca.chaves.android.setup.AbstractSetupActivity;
import ca.chaves.android.util.Debug;

/**
 * This is the "Setup Activity" to initialize all assets in this application. This activity is NOT meant to be used
 * normally - it is needed just to initialize the run-time environment prior running all unit-tests.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class SetupActivity
    extends AbstractSetupActivity
{
    // -----------------
    // Protected methods
    // -----------------

    @Override
    protected void showEulaIfNeeded( final boolean missingEula )
    {
        // always accept the EULA automatically - needed for the unit-tests to work
        new SaveEulaTask().execute();
    }

    /**
     * Do after all time-consuming initializations are complete. This function is executed from the UI thread.
     */
    @Override
    protected void continueIfPossible()
    {
        Debug.enter();
        if ( !setupOkay || setupErrorMessage != null )
        {
            // some files not installed!
            flash( setupErrorMessage );
        }
        // bye, bye, Application!
        finish(); // SetupActivity
        Debug.leave();
    }
}
