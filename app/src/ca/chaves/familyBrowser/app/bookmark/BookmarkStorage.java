package ca.chaves.familyBrowser.app.bookmark;

import ca.chaves.android.graph.GraphNode;
import ca.chaves.android.user.AbstractUserProfileStorage;
import ca.chaves.android.util.Debug;

import android.content.SharedPreferences;

/**
 * {@link Bookmark} storage.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class BookmarkStorage
    extends AbstractUserProfileStorage
{
    /**
     * Key for the default {@link GraphNode}'s nodeId.
     */
    public static final String FIELD_NODE_ID = "node";

    // --------------
    // I/O primitives
    // --------------

    /**
     * Read {@link Bookmark} from {@link SharedPreferences}.
     *
     * @param prefs prefs.
     * @param bookmark bookmark
     */
    public static void readBookmark( final SharedPreferences prefs, final Bookmark bookmark )
    {
        try
        {
            bookmark.nodeId = Integer.valueOf( prefs.getString( BookmarkStorage.FIELD_NODE_ID, null ) );
        }
        catch ( final NumberFormatException ex )
        {
            bookmark.nodeId = null;
        }

        Debug.print( "load", bookmark.tag, bookmark.title, bookmark.nodeId );
    }

    /**
     * Save {@link Bookmark} instance into its {@link SharedPreferences}.
     *
     * @param editor editor.
     * @param bookmark bookmark.
     */
    public static void writeBookmark( final SharedPreferences.Editor editor, final Bookmark bookmark )
    {
        if ( bookmark.nodeId != null )
        {
            editor.putString( BookmarkStorage.FIELD_NODE_ID, bookmark.nodeId.toString() );
        }
        else
        {
            editor.putString( BookmarkStorage.FIELD_NODE_ID, null );
        }

        Debug.print( "save", bookmark.tag, bookmark.title, bookmark.nodeId );
    }
}
