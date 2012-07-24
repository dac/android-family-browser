package ca.chaves.android.app;

import ca.chaves.android.R;
import ca.chaves.android.util.Debug;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Delete Confirmation dialog.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class DeleteDialog
    extends Dialog
{
    /**
     * Dialog listener.
     */
    public interface OnDeleteListener
    {
        /**
         * Action after the "delete" had been confirmed.
         */
        void onDelete();
    };

    /**
     * The dialog title.
     */
    private final String title;

    /**
     * The dialog's main text / message.
     */
    private final String message;

    /**
     * The caller's callback, to be executed to accept the input text.
     */
    private final OnDeleteListener onDeleteListener;

    /**
     * The [OK] button action.
     */
    private final View.OnClickListener okButtonListener = new View.OnClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick( final View view )
        {
            Debug.enter();
            actionDelete();
            Debug.leave();
        }
    };

    /**
     * The [CANCEL] button action.
     */
    private final View.OnClickListener cancelButtonListener = new View.OnClickListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick( final View view )
        {
            Debug.enter();
            actionCancel();
            Debug.leave();
        }
    };

    /**
     * The delete action, calling the onDeleteListener if necessary.
     *
     * @return true if this event was handled, false otherwise
     */
    private boolean actionDelete()
    {
        Debug.enter();
        onDeleteListener.onDelete();
        dismiss();
        Debug.leave();
        return true;
    }

    /**
     * Cancel this dialog.
     *
     * @return true if this event was handled, false otherwise
     */
    private boolean actionCancel()
    {
        Debug.enter();
        dismiss();
        Debug.leave();
        return true;
    }

    /**
     * Constructor.
     *
     * @param parentActivity dialog parent
     * @param onDeleteListener action to be done when the end-user confirms the "delete" message
     */
    public DeleteDialog( final Activity parentActivity, final OnDeleteListener onDeleteListener )
    {
        this( parentActivity, Android.App.INSTANCE.getString( R.string.delete_title_text ), null, onDeleteListener );
    }

    /**
     * Constructor.
     *
     * @param parentActivity the dialog parent
     * @param titleResId the dialog title
     * @param onDeleteListener action to be done when the end-user confirms the "delete" message
     */
    public DeleteDialog( final Activity parentActivity, final int titleResId, final OnDeleteListener onDeleteListener )
    {
        this( parentActivity, //
              Android.App.INSTANCE.getString( titleResId ), //
              null, //
              onDeleteListener );
    }

    /**
     * Constructor.
     *
     * @param parentActivity the dialog parent
     * @param titleResId the dialog title
     * @param messageResId the "delete" message
     * @param onDeleteListener action to be done when the end-user confirms the "delete" message
     */
    public DeleteDialog( final Activity parentActivity, final int titleResId, final int messageResId,
                         final OnDeleteListener onDeleteListener )
    {
        this( parentActivity, //
              Android.App.INSTANCE.getString( titleResId ), //
              Android.App.INSTANCE.getString( messageResId ), //
              onDeleteListener );
    }

    /**
     * Constructor.
     *
     * @param parentActivity the dialog parent
     * @param message the "delete" message
     * @param onDeleteListener action to be done when the end-user confirms the "delete" message
     */
    public DeleteDialog( final Activity parentActivity, final String message, final OnDeleteListener onDeleteListener )
    {
        this( parentActivity, //
              Android.App.INSTANCE.getString( R.string.delete_title_text ), //
              message, //
              onDeleteListener );
    }

    /**
     * Constructor.
     *
     * @param parentActivity the dialog parent
     * @param title the dialog title
     * @param message the "delete" message
     * @param onDeleteListener action to be done when the end-user confirms the "delete" message
     */
    public DeleteDialog( final Activity parentActivity, //
                         final String title, //
                         final String message, //
                         final OnDeleteListener onDeleteListener )
    {
        super( parentActivity );
        Debug.enter();

        setOwnerActivity( parentActivity );

        this.title = title;
        this.message = message;
        this.onDeleteListener = onDeleteListener;
        Debug.leave();
    }

    // ------------------
    // Life cycle methods
    // ------------------

    /**
     * The dialog is being created.
     *
     * @param savedInstanceState saved instance state.
     */
    @Override
    public void onCreate( final Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        {
            Debug.enter();

            setContentView( R.layout.dialog_delete );
            setCanceledOnTouchOutside( true );

            setTitle( title );

            if ( message != null )
            {
                final TextView messageText = (TextView) findViewById( R.id.text );
                final String messageFormat = Android.App.INSTANCE.getString( R.string.delete_that_message_text );
                messageText.setText( String.format( messageFormat, message ) );
            }

            final Button okButton = (Button) findViewById( R.id.ok_button );
            okButton.setOnClickListener( okButtonListener );

            final Button cancelButton = (Button) findViewById( R.id.cancel_button );
            cancelButton.setOnClickListener( cancelButtonListener );
            super.onCreate( savedInstanceState );

            cancelButton.requestFocus();
            Debug.leave();
        }
    }
}
