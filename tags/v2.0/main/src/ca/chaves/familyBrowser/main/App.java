/*-
 * @(#) templates/main/src/ca/chaves/familyBrowser/main/App.java
 * THIS FILE MIGHT HAVE BEEN GENERATED FROM A TEMPLATE.
 * PLEASE EDIT ONLY THE ORIGINAL TEMPLATE FILE.
 */

package ca.chaves.familyBrowser.main;

import ca.chaves.android.app.AbstractApplication;
import ca.chaves.android.app.Android;
import ca.chaves.android.util.Debug;
import ca.chaves.familyBrowser.app.about.AboutActivity;
import ca.chaves.familyBrowser.app.browser.BrowserActivity;

/**
 * The "Main Application" object, where we define the "data directory" and the "main activity".
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public final class App
    extends AbstractApplication
{
    /**
     * The package name for this application.
     */
    public static final String PACKAGE_NAME = "ca.chaves.familyBrowser";

    /**
     * The data directory of this application.
     */
    private static final String DATA_DIRECTORY = "/data/data/" + PACKAGE_NAME;

    static
    {
        /**
         * The data directory.
         */
        Android.Stage.dataDirectoryName = DATA_DIRECTORY;

        /**
         * The current EULA version, or null if no EULA is needed.
         */
        Android.Stage.eulaVersion = "2"; // GPL v2

        /**
         * The main activity class.
         */
        Android.Stage.mainActivityClass = BrowserActivity.class;

        /**
         * The about activity class.
         */
        Android.Stage.aboutActivityClass = AboutActivity.class;

        // debugging log

        Debug.print( "package name", PACKAGE_NAME );
        Debug.print( "data directory", DATA_DIRECTORY );
    }
}
