package ca.chaves.familyBrowser.test.helpers;

import android.app.Instrumentation;
import android.os.AsyncTask;

/**
 * This class defines hacks needed to use AsyncTask objects in unit tests.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class AsyncTaskUiThreadHacks {

    /**
     * AsyncTask needs to be instantiated from the main UI thread. JUnit tests
     * run in non-UI threads, causing AsyncTasks to be loaded in non-UI threads.
     * Therefore, AsyncTasks normally produce unexpected exceptions when they
     * are used in JUnit tests. However, as long as you create your process'
     * first AsyncTask in a UI-thread, all next ones can be created in non-UI
     * threads. This function allows to use AsyncTasks in JUnit tests.
     *
     * @warning you must not call this function!
     * @param inst
     */
    public static synchronized void initialize(Instrumentation inst) {
        inst.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                /**
                 * This AsyncTask is never executed. It just makes sure that the
                 * Looper for every AsyncTask is created in a UI-thread.
                 */
                @SuppressWarnings("unused")
                Object o = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        return null;
                    }
                };
            }
        });
    }
}
