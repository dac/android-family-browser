package ca.chaves.familyBrowser.app.bookmark;

import ca.chaves.android.graph.GraphNode;
import ca.chaves.android.user.AbstractUserProfile;

/**
 * Bookmark. A bookmark also includes all the user settings.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class Bookmark
    extends AbstractUserProfile
{
    /**
     * The default {@link GraphNode}'s nodeId for this bookmark.
     */
    public Integer nodeId;

    /**
     * Constructor.
     *
     * @param basename basename.
     * @param title title.
     */
    Bookmark( final String basename, final String title )
    {
        super( basename, title );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getGraphNodeId()
    {
        if ( nodeId != null )
        {
            return nodeId;
        }
        return super.getGraphNodeId();
    }
}
