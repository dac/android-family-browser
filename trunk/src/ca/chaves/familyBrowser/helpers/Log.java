package ca.chaves.familyBrowser.helpers;

/**
 * This class makes it easy to
 * "deactivate any calls to Log methods in the source code" before publishing
 * this application in the Android Market.
 *
 * @warning the Makefile file updates this file automatically while building
 *          targets "debug" and "release".
 * @author "David Chaves <david@chaves.ca>"
 */
public final class Log {

    /**
     * This constant must be false on production/release builds, and false for
     * debugging/troubleshooting builds
     */
    private static final boolean isEnabled = true;

    /**
     * Log.i()
     *
     * @param tag
     * @param msg
     */
    public static final void i(final String tag, final String msg) {
        if (isEnabled) {
            android.util.Log.i(tag, msg);
        }
    }

    /**
     * Log.e()
     *
     * @param tag
     * @param msg
     */
    public static final void e(final String tag, final String msg) {
        if (isEnabled) {
            android.util.Log.e(tag, msg);
        }
    }

    /**
     * Log.e()
     *
     * @param tag
     * @param msg
     * @param tr
     */
    public static final void e(final String tag, final String msg, final Throwable tr) {
        if (isEnabled) {
            android.util.Log.e(tag, msg, tr);
        }
    }

    /**
     * Log.d()
     *
     * @param tag
     * @param msg
     */
    public static final void d(final String tag, final String msg) {
        if (isEnabled) {
            android.util.Log.d(tag, msg);
        }
    }

    /**
     * Log.d()
     *
     * @param tag
     * @param msg
     * @param msg2
     */
    public static final void d(final String tag, final String msg, final String msg2) {
        if (isEnabled) {
            android.util.Log.d(tag, msg + " " + msg2);
        }
    }

    /**
     * Log.d()
     *
     * @param tag
     * @param msg
     * @param msg2
     * @param msg3
     */
    public static final void d(final String tag, final String msg, final String msg2, final String msg3) {
        if (isEnabled) {
            android.util.Log.d(tag, msg + " " + msg2 + " " + msg3);
        }
    }

    /**
     * Log.d()
     *
     * @param tag
     * @param msg
     * @param msg2
     * @param msg3
     */
    public static final void d(final String tag, final String msg, final String msg2, final int msg3) {
        if (isEnabled) {
            android.util.Log.d(tag, msg + " " + msg2 + " " + msg3);
        }
    }

    /**
     * Log.d()
     *
     * @param tag
     * @param msg
     * @param msg2
     * @param msg3
     */
    public static final void d(final String tag, final String msg, final int msg2, final int msg3) {
        if (isEnabled) {
            android.util.Log.d(tag, msg + " " + msg2 + " " + msg3);
        }
    }

    /**
     * Log.v()
     *
     * @param tag
     * @param msg
     */
    public static final void v(final String tag, final String msg) {
        if (isEnabled) {
            android.util.Log.v(tag, msg);
        }
    }

    /**
     * Log.w()
     *
     * @param tag
     * @param msg
     */
    public static final void w(final String tag, final String msg) {
        if (isEnabled) {
            android.util.Log.w(tag, msg);
        }
    }
}
