package ca.chaves.familyBrowser.test.database;

import java.util.concurrent.Semaphore;

import ca.chaves.familyBrowser.database.NodeController;
import ca.chaves.familyBrowser.database.NodeDatabase;
import ca.chaves.familyBrowser.helpers.Log;
import ca.chaves.familyBrowser.helpers.NodeObject;

import android.app.Activity;

/**
 * NodeController class for unit testing.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeControllerSample extends NodeController {

    /**
     * Counting semaphore to wait for "load complete" event.
     *
     * @see "http://en.wikipedia.org/wiki/Semaphore_(programming)"
     */
    private final Semaphore m_countingSemaphore = new Semaphore(0);

    /**
     * Constructor.
     *
     * @param activity
     */
    public NodeControllerSample(final Activity activity) {
        super(NodeDatabase.getDatabaseFactory(activity));
        // wait for all initial i/o to be complete, since the controller
        // automatically starts loading the default node during construction
        this.waitForLoadComplete();
    }

    /**
     * Wait for the "load complete" event.
     */
    private void waitForLoadComplete() {
        Log.d(TAG, "{ m_countingSemaphore.acquire");
        try {
            this.m_countingSemaphore.acquire();
        }
        catch (InterruptedException exc) {
            exc.printStackTrace();
        }
        Log.d(TAG, "} m_countingSemaphore.acquire");
    }

    /**
     * Call-back for the "load complete" event.
     */
    @Override
    protected void onLoadCompleteRefreshUserInterface(final NodeObject node) {
        Log.d(TAG, "{ m_countingSemaphore.release");
        this.m_countingSemaphore.release();
        Log.d(TAG, "} m_countingSemaphore.release");
    }

    /**
     * Load the NodeObject with this nodeId.
     *
     * @param nodeId
     * @return NodeObject
     */
    public NodeObject loadNodeObject(final Integer nodeId) {
        super.startLoadingNode(nodeId, null);
        this.waitForLoadComplete();
        return super.getUserNode();
    }
}
