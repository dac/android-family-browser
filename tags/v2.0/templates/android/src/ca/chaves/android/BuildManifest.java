/*-
 * @(#) templates/src/ca/chaves/android/BuildManifest.java
 * THIS FILE MIGHT HAVE BEEN GENERATED FROM A TEMPLATE.
 * PLEASE EDIT ONLY THE ORIGINAL TEMPLATE FILE.
 */

package ca.chaves.android;

/**
 * Compilation-time constants for Debug/Release builds.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public final class BuildManifest
{
    /**
     * False on production/release builds, and true for debugging/troubleshooting builds.
     */
    public static final boolean DEBUG_ENABLED = {{if DROID_DEBUG_ENABLED}}true{{else}}false{{endif}};

    /**
     * The "android:versionCode" value in AndroidManifest.xml.
     */
    public static final int VERSION_CODE = {{DROID_APP_VERSION}};
}
