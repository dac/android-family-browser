package ca.chaves.android.app;

import ca.chaves.android.R;
import ca.chaves.android.util.Debug;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Small dialog to let the user enter a single-line text.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class InputTextDialog
    extends Dialog
{
    /**
     * Dialog listener.
     */
    public interface OnInputTextListener
    {
        /**
         * Action after the input text had been completed.
         *
         * @param inputText the text just entered.
         */
        void onInputText( String inputText );
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
    private final OnInputTextListener onInputTextListener;

    /**
     * Accept the input text with [ENTER] too.
     */
    private final View.OnKeyListener onInputKeyListener = new View.OnKeyListener()
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onKey( final View view, final int keyCode, final KeyEvent event )
        {
            boolean handled = false;
            Debug.enter( keyCode );
            if ( KeyEvent.KEYCODE_ENTER == keyCode )
            {
                handled = actionInput();
            }
            Debug.leave( handled );
            return handled;
        }
    };

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
            actionInput();
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
     * The input text area.
     */
    private EditText inputText;

    /**
     * The input text action, calling the onInputTextListener if necessary.
     *
     * @return true if input text was handled, false otherwise.
     */
    private boolean actionInput()
    {
        Debug.enter();
        boolean handled = false;
        final String text = inputText.getText().toString().trim();
        if ( ( text != null ) && ( 0 < text.length() ) )
        {
            onInputTextListener.onInputText( text );
            handled = true;

            dismiss();
        }
        Debug.leave( handled );
        return handled;
    }

    /**
     * Cancel this dialog.
     *
     * @return true if this event was handled, false otherwise.
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
     * @param parentActivity the dialog parent.
     * @param titleResId the dialog title.
     * @param messageResId the dialog message.
     * @param onInputTextListener the action to be executed after the input is complete.
     */
    public InputTextDialog( final Activity parentActivity, final int titleResId, final int messageResId,
                            final OnInputTextListener onInputTextListener )
    {
        this( parentActivity, parentActivity.getString( titleResId ), parentActivity.getString( messageResId ),
              onInputTextListener );
    }

    /**
     * Constructor.
     *
     * @param parentActivity the dialog parent.
     * @param title the dialog title.
     * @param message the dialog message.
     * @param onInputTextListener the action to be executed after the input is complete.
     */
    public InputTextDialog( final Activity parentActivity, final String title, final String message,
                            final OnInputTextListener onInputTextListener )
    {
        super( parentActivity );
        Debug.enter();

        setOwnerActivity( parentActivity );

        this.title = title;
        this.message = message;
        this.onInputTextListener = onInputTextListener;
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

            setCanceledOnTouchOutside( true );
            setContentView( R.layout.dialog_inputtext );
            setTitle( title );

            final TextView messageText = (TextView) findViewById( R.id.text );
            messageText.setText( message );

            inputText = (EditText) findViewById( R.id.input_text );
            inputText.setOnKeyListener( onInputKeyListener );

            final Button okButton = (Button) findViewById( R.id.ok_button );
            okButton.setOnClickListener( okButtonListener );

            final Button cancelButton = (Button) findViewById( R.id.cancel_button );
            cancelButton.setOnClickListener( cancelButtonListener );

            inputText.requestFocus();
            Debug.leave();
        }
    }
}
