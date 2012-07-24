package ca.chaves.familyBrowser.app.bookmark;

import ca.chaves.android.profile.AbstractProfile;
import ca.chaves.android.profile.AbstractProfileCRUD;
import ca.chaves.android.user.AbstractUserProfileStorage;
import ca.chaves.android.util.Debug;
import ca.chaves.familyBrowser.app.R;

import android.content.SharedPreferences;

/**
 * {@link Bookmark} CRUD operations.
 */
public abstract class BookmarkCRUD
    extends AbstractProfileCRUD
{
    // -----------------
    // Protected methods
    // -----------------

    /**
     * Load an existent {@link Bookmark} instance.
     *
     * @param title the {@link Bookmark}'s title to load.
     * @return the {@link Bookmark} for the given <code>title</code>, or null if there is no profile with this
     *         <code>title</code>.
     */
    protected Bookmark loadBookmarkIfExists( final String title )
    {
        Debug.enter( title );

        Bookmark bookmark = null;
        final String titleValue = ( title != null ) ? title.trim() : "";
        if ( 0 < titleValue.length() )
        {
            synchronized ( AbstractProfileCRUD.LOCK )
            {
                final String basename = AbstractUserProfileStorage.getBasename( titleValue );
                if ( basename != null )
                {
                    final SharedPreferences prefs = AbstractProfile.getSharedPreferences( basename );
                    bookmark = readBookmark( prefs, basename, titleValue );
                }
            }
        }

        Debug.leave( "loaded", titleValue, ( bookmark != null ) ? bookmark.title : null );
        return bookmark;
    }

    /**
     * Load {@link Bookmark} instance, creating it if it does not exist.
     *
     * @param title the {@link Bookmark}'s title to load.
     * @return the {@link Bookmark} for the given <code>title</code>, or null if <code>title</code> is null.
     */
    protected Bookmark loadBookmark( final String title )
    {
        Debug.enter( title );

        Bookmark bookmark = null;
        final String titleValue = ( title != null ) ? title.trim() : "";
        if ( 0 < titleValue.length() )
        {
            synchronized ( AbstractProfileCRUD.LOCK )
            {
                String basename = AbstractUserProfileStorage.getBasename( titleValue );
                final boolean adding = ( basename == null );

                if ( adding )
                {
                    basename = AbstractUserProfileStorage.getBasename();
                }

                final SharedPreferences prefs = AbstractProfile.getSharedPreferences( basename );
                bookmark = readBookmark( prefs, basename, titleValue );

                if ( adding )
                {
                    writeBookmark( prefs, bookmark );
                }
            }
        }

        Debug.leave( "loaded", titleValue, ( bookmark != null ) ? bookmark.title : null );
        return bookmark;
    }

    /**
     * Save {@link Bookmark} instance.
     *
     * @param bookmark to save.
     * @return true on success, false otherwise.
     */
    protected boolean saveBookmark( final Bookmark bookmark )
    {
        Debug.enter();

        boolean success = false;
        if ( bookmark != null && bookmark.title != null )
        {
            synchronized ( AbstractProfileCRUD.LOCK )
            {
                final SharedPreferences prefs = bookmark.getSharedPreferences();
                success = writeBookmark( prefs, bookmark );
            }
        }

        Debug.leave( "saved?", success, ( bookmark != null ) ? bookmark.title : null );
        return success;
    }

    // --------------
    // I/O primitives
    // --------------

    /**
     * Load {@link Bookmark} instance from its {@link SharedPreferences}.
     *
     * @param prefs prefs.
     * @param basename basename.
     * @param title title.
     * @return Profile.
     */
    protected Bookmark readBookmark( final SharedPreferences prefs, final String basename, final String title )
    {
        final Bookmark bookmark = new Bookmark( basename, title );

        AbstractUserProfileStorage.readUserProfile( prefs, bookmark );
        BookmarkStorage.readBookmark( prefs, bookmark );

        return bookmark;
    }

    /**
     * Save {@link Bookmark} instance into its {@link SharedPreferences}.
     *
     * @return true on success, false on failure.
     */
    private boolean writeBookmark( final SharedPreferences prefs, final Bookmark bookmark )
    {
        final SharedPreferences.Editor editor = prefs.edit();

        AbstractUserProfileStorage.writeUserProfile( editor, bookmark );
        BookmarkStorage.writeBookmark( editor, bookmark );

        return check( editor.commit(), R.string.io_error );
    }
}
