package ca.chaves.familyBrowser.test.util;

import ca.chaves.familyBrowser.main.App;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Config;
import android.util.Log;

/**
 * Utility functions for unit-tests.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class Utils
{
    /**
     * Tag for logging.
     */
    private static final String TAG = "Test";

    /**
     * Get package {@link Context}.
     *
     * @param context the "test" context.
     * @return the {@link Context} for the given package.
     */
    private static Context getContext( final Context context, final String pkg )
    {
        try
        {
            final Context ctx = context.createPackageContext( pkg, 0 );
            if ( Config.LOGV )
            {
                Log.v( Utils.TAG, "created context for " + ctx.getPackageName() );
            }
            return ctx;
        }
        catch ( final NameNotFoundException ex )
        {
            Log.e( Utils.TAG, "unable to create context for " + pkg, ex );
            return null;
        }
    }

    /**
     * Get main application {@link Context}.
     *
     * @param context the "test" context.
     * @return the {@link Context} for the "main" application
     */
    public static Context getMainContext( final Context context )
    {
        return Utils.getContext( context, App.PACKAGE_NAME );
    }

    /**
     * Get test application {@link Context}.
     *
     * @param context the "main" context.
     * @return the {@link Context} for the "test" application.
     */
    public static Context getTestContext( final Context context )
    {
        return Utils.getContext( context, App.PACKAGE_NAME + ".test" );
    }
}
