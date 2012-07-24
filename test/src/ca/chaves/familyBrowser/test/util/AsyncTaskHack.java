package ca.chaves.familyBrowser.test.util;

import android.app.Instrumentation;
import android.os.AsyncTask;

/**
 * Hacks to use AsyncTask objects in unit tests.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class AsyncTaskHack
{
    /**
     * AsyncTask needs to be instantiated from the main UI thread. JUnit tests run in non-UI threads, causing AsyncTasks
     * to be loaded in non-UI threads. Therefore, AsyncTasks normally produce unexpected exceptions when they are used
     * in JUnit tests. However, as long as you create your process' first AsyncTask in a UI-thread, all next ones can be
     * created in non-UI threads. This function allows to use AsyncTasks in JUnit tests.
     *
     * @warning you must not call this function!
     * @param inst instrumentation handle
     */
    public static synchronized void initialize( final Instrumentation inst )
    {
        final Runnable hacker = new Runnable()
        {
            @Override
            public void run()
            {
                /**
                 * This AsyncTask is never executed. It just makes sure that the Looper for every AsyncTask is created
                 * in a UI-thread.
                 */
                @SuppressWarnings( "unused" )
                final Object unused = new AsyncTask<Void, Void, Void>()
                {
                    @Override
                    protected Void doInBackground( final Void... params )
                    {
                        return null;
                    }
                };
            }
        };

        inst.runOnMainSync( hacker );
    }
}
