package ca.chaves.familyBrowser.test;

import ca.chaves.android.util.AbstractAsyncTask;
import ca.chaves.android.util.Debug;
import ca.chaves.familyBrowser.app.bookmark.Bookmark;
import ca.chaves.familyBrowser.app.bookmark.BookmarkCRUD;
import ca.chaves.familyBrowser.test.util.AsyncCondition;

import android.content.Context;
import android.os.AsyncTask;
import android.test.AndroidTestCase;

import junit.framework.Assert;

/**
 * Test for {@link BookmarkCRUD}.
 *
 * @see "http://stackoverflow.com/questions/2321829/android-asynctask-testing-problem-with-android-test-framework"
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public abstract class UserProfileCRUDTest
    extends AndroidTestCase
{
    private transient Context context;

    /**
     * This value must not have leading nor trailing blanks.
     */
    private static final String TEST_PROFILE_TITLE = "Test profile use for jUnit Tests";

    // ----------
    // Unit Tests
    // ----------

    /**
     * Test function.
     */
    public void testNotNulls()
    {
        Assert.assertNotNull( context );
    }

    /**
     * Test function.
     */
    public void testNullTitle()
    {
        final AsyncCondition semaphore = new AsyncCondition();

        final class Task
            extends GetUserProfileAsyncTask
        {
            public Task()
            {
                super( null );
                Assert.assertNull( title );
            }

            @Override
            protected boolean isCanceling()
            {
                return false;
            }

            @Override
            protected void onPostExecute( final Void result )
            {
                Assert.assertNull( title );
                doAssertFailed( this );

                Assert.assertNull( profile );

                semaphore.signal(); // notify the count down latch
            }
        }

        new Task().execute();
        semaphore.await(); // wait for the AsyncTask
    }

    /**
     * Test function.
     */
    public void testEmptyTitle()
    {
        final AsyncCondition semaphore = new AsyncCondition();

        final String testTitle = "";

        final class Task
            extends GetUserProfileAsyncTask
        {
            public Task( final String title )
            {
                super( title );
                Assert.assertEquals( testTitle, title );
            }

            @Override
            protected void onPostExecute( final Void result )
            {
                Assert.assertEquals( testTitle, title );
                doAssertFailed( this );

                Assert.assertNull( profile );

                semaphore.signal(); // notify the count down latch
            }
        }

        new Task( testTitle ).execute();
        semaphore.await(); // wait for the AsyncTask
    }

    /**
     * Test function.
     */
    public void testSpacesTitle()
    {
        final AsyncCondition semaphore = new AsyncCondition();

        final String testTitle = "    "; // many spaces

        final class Task
            extends GetUserProfileAsyncTask
        {
            public Task( final String title )
            {
                super( title );
                Assert.assertEquals( testTitle, title );
            }

            @Override
            protected void onPostExecute( final Void result )
            {
                Assert.assertEquals( testTitle, title );
                doAssertFailed( this );

                Assert.assertNull( profile );

                semaphore.signal(); // notify the count down latch
            }
        }

        new Task( testTitle ).execute();
        semaphore.await(); // wait for the AsyncTask
    }

    /**
     * Test function.
     */
    public void testSpacesInTitle()
    {
        final AsyncCondition semaphore = new AsyncCondition();

        final String testTitleMinusSpaces = UserProfileCRUDTest.TEST_PROFILE_TITLE;
        final String testTitlePlusSpaces = "   " + testTitleMinusSpaces + "  ";

        final class Task
            extends GetUserProfileAsyncTask
        {
            public Task( final String title )
            {
                super( title );
                Assert.assertEquals( testTitlePlusSpaces, title );
            }

            @Override
            protected void onPostExecute( final Void result )
            {
                Assert.assertEquals( testTitlePlusSpaces, title );
                doAssertSuccess( this );

                Assert.assertNotNull( profile );

                Assert.assertEquals( testTitleMinusSpaces, profile.title );

                semaphore.signal(); // notify the count down latch
            }
        }

        new Task( testTitlePlusSpaces ).execute();
        semaphore.await(); // wait for the AsyncTask
    }

    /**
     * Test function.
     */
    public void testValidTask()
    {
        final AsyncCondition semaphore = new AsyncCondition();

        final String testTitle = UserProfileCRUDTest.TEST_PROFILE_TITLE;

        final class Task
            extends GetUserProfileAsyncTask
        {
            public Task( final String title )
            {
                super( title );
                Assert.assertEquals( testTitle, title );
            }

            @Override
            protected void onPostExecute( final Void result )
            {
                Assert.assertEquals( testTitle, title );
                doAssertSuccess( this );

                Assert.assertNotNull( profile );
                Assert.assertNotNull( profile.basename );
                Assert.assertNotNull( profile.title );
                Assert.assertNotNull( profile.localeCode );

                Assert.assertEquals( 3, profile.basename.length() );
                Assert.assertEquals( 2, profile.localeCode.length() );

                Assert.assertEquals( testTitle, profile.title );

                semaphore.signal(); // notify the count down latch
            }
        }

        new Task( testTitle ).execute();
        semaphore.await(); // wait for the AsyncTask
    }

    // ------
    // Set Up
    // ------

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        context = super.getContext();
    }

    // -------
    // Helpers
    // -------

    /**
     * Some common assertions.
     *
     * @param task
     */
    @SuppressWarnings( "hiding" )
    private <P, Q, R> void doAssertFailed( final AbstractAsyncTask<P, Q, R> task )
    {
        final String errorMessage = task.error();

        Assert.assertTrue( task.failed() );
        Assert.assertNotNull( errorMessage );
        Assert.assertTrue( 0 < errorMessage.trim().length() );
    }

    /**
     * Some common assertions.
     *
     * @param task
     */
    @SuppressWarnings( "hiding" )
    private <P, Q, R> void doAssertSuccess( final AbstractAsyncTask<P, Q, R> task )
    {
        final String errorMessage = task.error();

        Assert.assertFalse( task.failed() );
        Assert.assertNull( errorMessage );
    }

    /**
     * {@link AsyncTask} to load {@link Bookmark}.
     */
    private abstract class GetUserProfileAsyncTask
        extends BookmarkCRUD
    {
        /**
         * The {@link Bookmark} title to get.
         */
        protected final String title;

        /**
         * The resulting {@link Bookmark} loaded for the given 'title'.
         */
        protected Bookmark profile;

        /**
         * Constructor.
         *
         * @param title the {@link Bookmark} title to get.
         */
        public GetUserProfileAsyncTask( final String title )
        {
            super();
            this.title = title;
        }

        @Override
        protected boolean isCanceling()
        {
            return false;
        }

        @Override
        protected Void doInBackground( final Void... params )
        {
            Debug.enter();
            profile = loadBookmark( title );
            Debug.leave();
            return null;
        }
    }
}
