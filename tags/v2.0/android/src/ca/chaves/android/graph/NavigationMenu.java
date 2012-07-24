package ca.chaves.android.graph;

import ca.chaves.android.util.Debug;

/**
 * This class creates a navigation menu from a navigation path. This menu could be used to show the navigation history.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class NavigationMenu
{
    /**
     * The history of nodeId(s). The most recent is the first in this array.
     */
    public final Integer[] nodeIds;

    /**
     * The history of nodeLabel(s). The most recent is the first in this array.
     */
    public final String[] nodeLabels;

    /**
     * Constructor.
     *
     * @param path the navigation path where the menu will be created from.
     */
    public NavigationMenu( final NavigationPath path )
    {
        // how menu elements are we going to return?
        final int maxLen = 15;

        Debug.enter();

        if ( path.length <= maxLen )
        {
            nodeIds = new Integer[path.length];
            nodeLabels = new String[path.length];

            System.arraycopy( path.array_0, 0, nodeIds, 0, path.length );
            System.arraycopy( path.array_1, 0, nodeLabels, 0, path.length );
        }
        else
        {
            nodeIds = new Integer[maxLen];
            nodeLabels = new String[maxLen];

            nodeIds[0] = path.array_0[0];
            nodeLabels[0] = path.array_1[0];

            if ( path.length <= path.next )
            {
                System.arraycopy( path.array_0, path.length - maxLen - 1, nodeIds, 1, maxLen - 1 );
                System.arraycopy( path.array_1, path.length - maxLen - 1, nodeLabels, 1, maxLen - 1 );
            }
            else
            {
                nodeIds[maxLen - 1] = path.array_0[path.next];
                nodeLabels[maxLen - 1] = path.array_1[path.next];

                System.arraycopy( path.array_0, path.next - maxLen - 2, nodeIds, 1, maxLen - 2 );
                System.arraycopy( path.array_1, path.next - maxLen - 2, nodeLabels, 1, maxLen - 2 );
            }
        }

        // reverse menu items - we want to show in the top the most recent choice
        reverse( nodeIds );
        reverse( nodeLabels );

        // Debug.print( "MENU", Arrays.toString( nodeIds ), Arrays.toString( nodeLabels ) );
        Debug.leave();
    }

    /**
     * Reverse array in-place.
     *
     * @param Type the array element type.
     * @param array to be reversed.
     */
    private static <Type> void reverse( final Type[] array )
    {
        int head = -1;
        int tail = array.length;
        while ( ++head < --tail )
        {
            // swap them
            final Type elem = array[head];
            array[head] = array[tail];
            array[tail] = elem;
        }
    }
}
