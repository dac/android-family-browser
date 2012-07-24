package ca.chaves.familyBrowser.app.bookmark;

import ca.chaves.android.app.AbstractActivity;
import ca.chaves.android.app.DeleteDialog;
import ca.chaves.android.app.InputTextDialog;
import ca.chaves.android.user.AbstractUserProfile;
import ca.chaves.android.user.UserProfile;
import ca.chaves.android.user.UserProfileCRUD;
import ca.chaves.android.user.UserProfileList;
import ca.chaves.android.user.UserProfileListAdapter;
import ca.chaves.android.util.Debug;
import ca.chaves.familyBrowser.app.R;
import ca.chaves.familyBrowser.app.about.AboutActivity;
import ca.chaves.familyBrowser.app.browser.BrowserController;
import ca.chaves.familyBrowser.app.settings.SettingsActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;

/**
 * The Bookmarks Activity - to edit/delete/add Bookmark(s).
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class BookmarksActivity
    extends AbstractActivity
{
    /**
     * The {@link ListAdater}.
     */
    private final UserProfileListAdapter listAdapter = new UserProfileListAdapter();

    /**
     * Should add a new bookmark if the is no bookmarks created?
     */
    private boolean shouldAddIfNone = true;

    /**
     * Event handler for the "Edit Profile" button.
     */
    private final AdapterView.OnItemClickListener onEditItemClick = new AdapterView.OnItemClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onItemClick( final AdapterView<?> parent, final View view, final int position, final long id )
        {
            Debug.enter();
            actionEdit( listAdapter.getItem( position ) );
            Debug.leave();
        }
    };

    /**
     * Event handler for the "Add Profile" button.
     */
    private final OnClickListener addButtonListener = new OnClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick( final View view )
        {
            Debug.enter();
            actionAdd();
            Debug.leave();
        }
    };

    // ------------------
    // Life cycle methods
    // ------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate( final Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        {
            Debug.enter();
            setContentView( R.layout.activity_bookmarks );

            // setup the "list"
            final ListView listView = (ListView) findViewById( R.id.list );
            listView.setOnItemClickListener( onEditItemClick );
            listView.setAdapter( listAdapter );

            // setup the context menu
            super.registerForContextMenu( listView );

            // setup the "add" button
            final Button addButton = (Button) findViewById( R.id.add_button );
            addButton.setOnClickListener( addButtonListener );
            Debug.leave();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume()
    {
        Debug.enter();
        super.onResume();
        actionReload();
        Debug.leave();
    }

    // ------------------
    // Activity overrides
    // ------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu( final Menu menu )
    {
        Debug.enter();
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.activity_bookmarks, menu );
        Debug.leave();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected( final MenuItem item )
    {
        Debug.enter();
        // @see "http://ofps.oreilly.com/titles/9781449390501/Android_Preferences.html"
        boolean handled = false;
        final int itemId = item.getItemId();
        if ( R.id.bookmarks_about_menu_item == itemId )
        {
            startActivity( new Intent( this, AboutActivity.class ) );
            handled = true;
        }
        else if ( R.id.bookmarks_bookmarks_menu_item == itemId )
        {
            handled = true; // we are already here - nothing to do
        }
        else if ( R.id.bookmarks_settings_menu_item == itemId )
        {
            startActivity( new Intent( this, SettingsActivity.class ) );
            handled = true;
        }
        else
        {
            Debug.print( "unknown option", itemId );
            handled = super.onOptionsItemSelected( item );
        }
        Debug.leave( handled );
        return handled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateContextMenu( final ContextMenu menu, final View view, final ContextMenuInfo menuInfo )
    {
        Debug.enter();
        super.onCreateContextMenu( menu, view, menuInfo );
        final int itemId = view.getId();
        if ( R.id.list == itemId )
        {
            final MenuInflater inflater = getMenuInflater();
            inflater.inflate( R.menu.activity_bookmarks_by_bookmark, menu );
        }
        Debug.leave();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onContextItemSelected( final MenuItem item )
    {
        Debug.enter();
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        boolean handled = false;
        final int itemId = item.getItemId();
        if ( R.id.bookmarks_activate_by_bookmark_menu_item == itemId )
        {
            Debug.print( "activate" );
            handled = actionActivate( listAdapter.getItem( info.position ) );
        }
        else if ( R.id.bookmarks_edit_by_bookmark_menu_item == itemId )
        {
            Debug.print( "edit" );
            handled = actionEdit( listAdapter.getItem( info.position ) );
        }
        else if ( R.id.bookmarks_delete_by_bookmark_menu_item == itemId )
        {
            Debug.print( "delete" );
            handled = actionDelete( listAdapter.getItem( info.position ) );
        }
        else
        {
            Debug.print( "unknown option", itemId );
            handled = super.onContextItemSelected( item );
        }
        Debug.leave( handled );
        return handled;
    }

    // ---------------
    // Private members
    // ---------------

    /**
     * Action: reload user bookmark.
     *
     * @return true on success, false on failure.
     */
    private boolean actionReload()
    {
        final class Task
            extends UserProfileTask
        {
            /**
             * The retrieved {@link UserProfileList}.
             */
            private UserProfileList userProfiles;

            /**
             * {@inheritDoc}
             */
            @Override
            protected Void doInBackground( final Void... params )
            {
                Debug.enter();
                userProfiles = loadProfiles();
                Debug.leave();
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void doInForeground( final Void result )
            {
                Debug.enter();
                listAdapter.setList( userProfiles );
                // if the list is empty, go straight to add a new one
                if ( shouldAddIfNone && userProfiles.isEmpty() )
                {
                    shouldAddIfNone = false;
                    actionAdd();
                }
                Debug.leave();
            }
        }

        Debug.enter();
        new Task().execute();
        Debug.leave();
        return true;
    }

    /**
     * Action: delete bookmark.
     *
     * @param profile to delete.
     * @return true on success, false on failure.
     */
    private boolean actionDelete( final UserProfile profile )
    {
        final DeleteDialog.OnDeleteListener onDeleteListener = new DeleteDialog.OnDeleteListener()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onDelete()
            {
                /**
                 * Task to delete {@link AbstractUserProfile}.
                 */
                final class Task
                    extends UserProfileTask
                {
                    /**
                     * The retrieved {@link UserProfileList} after deleting 'bookmark'.
                     */
                    protected UserProfileList profiles;

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    protected Void doInBackground( final Void... params )
                    {
                        Debug.enter();
                        deleteProfile( profile );
                        profiles = loadProfiles();
                        Debug.leave();
                        return null;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    protected void doInForeground( final Void result )
                    {
                        Debug.enter();
                        listAdapter.setList( profiles );
                        Debug.leave();
                    }
                }

                Debug.enter();
                new Task().execute();
                Debug.leave();
            }
        };

        Debug.enter();
        new DeleteDialog( this, profile.title, onDeleteListener ).show();
        Debug.leave();
        return true;
    }

    /**
     * Action: Add new {@link AbstractUserProfile}.
     *
     * @return true on success, false on failure.
     */
    private boolean actionAdd()
    {
        /**
         * Add new {@link AbstractUserProfile}.
         */
        final InputTextDialog.OnInputTextListener onAddlistener = new InputTextDialog.OnInputTextListener()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onInputText( final String title )
            {
                final class Task
                    extends BookmarkTask
                {
                    /**
                     * The resulting {@link Bookmark} loaded for the given <code>title</code>.
                     */
                    private Bookmark bookmark;

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    protected Void doInBackground( final Void... params )
                    {
                        Debug.enter();
                        bookmark = loadBookmark( title );
                        Debug.leave();
                        return null;
                    }

                    /**
                     * {@inheritDoc}
                     */
                    @Override
                    protected void doInForeground( final Void result )
                    {
                        Debug.enter();
                        actionEdit( bookmark );
                        finish(); // UserProfileListActivity
                        Debug.leave();
                    }
                }

                Debug.enter();
                new Task().execute();
                Debug.leave();
            }
        };

        Debug.enter();
        new InputTextDialog( this, R.string.bookmarks_add_title, R.string.bookmarks_add_prompt, onAddlistener ).show();
        Debug.leave();
        return true;
    }

    /**
     * Action: edit {@link AbstractUserProfile}.
     *
     * @param bookmark to edit.
     * @return true on success, false on failure.
     */
    private boolean actionEdit( final AbstractUserProfile bookmark )
    {
        Debug.enter();
        bookmark.activate();
        startActivity( new Intent( this, BookmarkActivity.class ) );
        Debug.leave();
        return true;
    }

    /**
     * Action: activate {@link AbstractUserProfile}.
     *
     * @param profile to activate.
     * @return true on success, false on failure.
     */
    private boolean actionActivate( final AbstractUserProfile profile )
    {
        final class Task
            extends BookmarkTask
        {
            /**
             * The resulting {@link Bookmark} loaded for the given <code>title</code>.
             */
            private Bookmark bookmark;

            /**
             * {@inheritDoc}
             */
            @Override
            protected Void doInBackground( final Void... params )
            {
                Debug.enter();
                bookmark = loadBookmark( profile.title );
                Debug.leave();
                return null;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void doInForeground( final Void result )
            {
                Debug.enter();
                if ( bookmark != null )
                {
                    bookmark.activate();
                    BrowserController.stepForward( bookmark.getGraphNodeId(), null );
                }
                finish(); // UserProfileListActivity
                Debug.leave();
            }
        }

        Debug.enter();
        new Task().execute();
        Debug.leave();
        return true;
    }

    /**
     * Base class for all {@link AsyncTask}s in this class.
     */
    private abstract class UserProfileTask
        extends UserProfileCRUD
    {
        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean isCanceling()
        {
            return isTerminated();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute( final Void result )
        {
            Debug.enter();
            if ( canceled() )
            {
                Debug.print( "canceled", errorMessage );
            }
            else if ( failed() )
            {
                flash( errorMessage );
            }
            else
            {
                doInForeground( result );
            }
            Debug.leave();
        }

        /**
         * Function to be done in foreground.
         *
         * @param result result.
         */
        protected abstract void doInForeground( final Void result );
    }

    /**
     * Base class for all {@link AsyncTask}s in this class.
     */
    private abstract class BookmarkTask
        extends BookmarkCRUD
    {
        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean isCanceling()
        {
            return isTerminated();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute( final Void result )
        {
            Debug.enter();
            if ( canceled() )
            {
                Debug.print( "canceled", errorMessage );
            }
            else if ( failed() )
            {
                flash( errorMessage );
            }
            else
            {
                doInForeground( result );
            }
            Debug.leave();
        }

        /**
         * Function to be done in foreground.
         *
         * @param result result.
         */
        protected abstract void doInForeground( final Void result );
    }
}
