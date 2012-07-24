package ca.chaves.android.util;

import ca.chaves.android.app.Android;

import android.os.AsyncTask;

/**
 * AsyncTask wrapper.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 * @param <ParamsType> parameters type.
 * @param <ProgressType> progress type.
 * @param <ResultType> result.
 */
public abstract class AbstractAsyncTask<ParamsType, ProgressType, ResultType>
    extends AsyncTask<ParamsType, ProgressType, ResultType>
{
    /**
     * Last error reported. We only keep the last error - don't bother the user with more errors that just one.
     */
    protected String errorMessage;

    /**
     * This flag becomes true as soon as this task is canceled.
     */
    private volatile boolean wasCanceled;

    /**
     * Check if the 'condition' is true.
     *
     * @param condition to check for.
     * @param errorResId error message to be reported if 'condition' is false.
     * @return condition.
     */
    public final boolean check( final boolean condition, final int errorResId )
    {
        Debug.enter( condition );
        if ( !condition )
        {
            error( errorResId );
        }
        Debug.leave();
        return condition;
    }

    /**
     * Check if the 'condition' is true.
     *
     * @param condition to check for.
     * @param message to report if condition is false.
     * @return condition.
     */
    public final boolean check( final boolean condition, final String message )
    {
        Debug.enter( condition, message );
        if ( !condition )
        {
            check( message );
        }
        Debug.leave();
        return condition;
    }

    /**
     * Check if there is a new error message.
     *
     * @param message the error message, or null if no error.
     * @return true if no error.
     */
    public final boolean check( final String message )
    {
        Debug.enter( message );
        final boolean result = ( message == null ) || ( message.length() <= 0 );
        if ( result )
        {
            // all fine
        }
        else if ( errorMessage != null )
        {
            Debug.print( "ignore:", message, "; keep error:", errorMessage );
        }
        else
        {
            Debug.print( "error:", message );
            errorMessage = message;
        }
        Debug.leave( message );
        return result;
    }

    /**
     * Signal a new error message.
     *
     * @param errorResId error message to report.
     * @return null.
     */
    public final ResultType error( final int errorResId )
    {
        Debug.enter();
        check( Android.App.INSTANCE.getString( errorResId ) );
        Debug.leave();
        return null;
    }

    /**
     * Get the error message.
     *
     * @return the error message, or null if not failed yet.
     */
    public final String error()
    {
        return errorMessage;
    }

    /**
     * Has this task failed?
     *
     * @return true if there are any error so far.
     */
    public final boolean failed()
    {
        Debug.enter( errorMessage );
        final boolean result = ( errorMessage != null );
        Debug.leave( result );
        return result;
    }

    /**
     * Is this task canceled?
     *
     * @warning use this function instead of {@link AsyncTask#isCancelled()}.
     * @return true if this was has been canceled.
     */
    public final boolean canceled()
    {
        Debug.enter();
        boolean result = false;
        if ( wasCanceled || isCanceling() || isCancelled() )
        {
            Debug.print( "canceled", wasCanceled );
            result = wasCanceled = true;
        }
        Debug.leave( result );
        return result;
    }

    /**
     * Should task be canceled?
     *
     * @return true if task must be canceled.
     */
    protected abstract boolean isCanceling();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCancelled()
    {
        Debug.enter( "canceled", wasCanceled );
        wasCanceled = true;
        Debug.leave();
    }
}
