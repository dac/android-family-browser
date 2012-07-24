package ca.chaves.familyBrowser.test.util;

import android.os.AsyncTask;

import java.util.concurrent.CountDownLatch;

/**
 * Simplified binary semaphore for testing {@link AsyncTask} instances.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class AsyncCondition
{
    private final transient CountDownLatch latch = new CountDownLatch( 1 );

    /**
     * This condition has become true.
     */
    public void signal()
    {
        latch.countDown();
    }

    /**
     * Wait for this condition to become true.
     */
    public void await()
    {
        try
        {
            latch.await();
        }
        catch ( final InterruptedException ex )
        {
            throw new RuntimeException( ex );
        }
    }

}
