package ca.chaves.familyBrowser.app.splash;

import ca.chaves.android.app.SettingsCRUD;
import ca.chaves.android.splash.AbstractSplashActivity;
import ca.chaves.android.util.Debug;
import ca.chaves.familyBrowser.app.bookmark.Bookmark;
import ca.chaves.familyBrowser.app.bookmark.BookmarkCRUD;

import android.os.AsyncTask;

/**
 * The Splash / Greetings activity.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class SplashActivity
    extends AbstractSplashActivity
{
    private String defaultBookmarkTitle;

    private Bookmark defaultActiveBookmark;

    /**
     * Constructor.
     */
    public SplashActivity()
    {
        super();
        pushSetupStep( new LoadDefaultActiveBookmarkTask() );
        pushSetupStep( new LoadDefaultBookmarkTitleTask() );
    }

    /**
     * {@link AsyncTask} to load the default profile.
     */
    private final class LoadDefaultActiveBookmarkTask
        extends BookmarkCRUD
    {
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
        protected Void doInBackground( Void... params )
        {
            Debug.print( "setup default profile", defaultBookmarkTitle );
            if ( defaultBookmarkTitle != null )
            {
                defaultActiveBookmark = loadBookmarkIfExists( defaultBookmarkTitle );
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute( final Void result )
        {
            Debug.enter();
            if ( defaultActiveBookmark != null )
            {
                defaultActiveBookmark.activate();
            }
            executeNextSetupStep();
            Debug.leave();
        }
    }

    /**
     * {@link AsyncTask} to start all setup.
     */
    private final class LoadDefaultBookmarkTitleTask
        extends SettingsCRUD
    {
        private String title;

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
        protected Void doInBackground( Void... params )
        {
            // load the default "active" profile
            title = getDefaultProfileTitle();
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute( final Void result )
        {
            Debug.enter();
            defaultBookmarkTitle = title;
            Debug.print( "default bookmark", defaultBookmarkTitle );
            executeNextSetupStep();
            Debug.leave();
        }
    }
}
