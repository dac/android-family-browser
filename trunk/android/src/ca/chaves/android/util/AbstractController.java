package ca.chaves.android.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Abstract controller.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public abstract class AbstractController
    implements Controllable
{
    /**
     * Listener list - we use {@link WeakReference}s to allow garbage collection.
     */
    private final transient LinkedList<WeakReference<Controllable>> listeners =
        new LinkedList<WeakReference<Controllable>>();

    /**
     * True if this controller has been updated at least once.
     */
    private boolean wasUpdated;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTerminated()
    {
        return filterOut().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flash( final String message )
    {
        Debug.enter( message );
        for ( final WeakReference<Controllable> item : filterOut() )
        {
            final Controllable listener = item.get();
            if ( listener != null )
            {
                listener.flash( message );
            }
        }
        Debug.leave( message );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onControllerUpdated()
    {
        Debug.enter();
        wasUpdated = true;
        for ( final WeakReference<Controllable> item : filterOut() )
        {
            final Controllable listener = item.get();
            if ( listener != null )
            {
                listener.onControllerUpdated();
            }
        }
        Debug.leave();
    }

    /**
     * Filter out all terminated elements.
     *
     * @return this delegate list, filtered out.
     */
    private LinkedList<WeakReference<Controllable>> filterOut()
    {
        Debug.enter();

        // remove all terminated items.
        final Iterator<WeakReference<Controllable>> it = listeners.iterator();
        while ( it.hasNext() )
        {
            final WeakReference<Controllable> item = it.next();
            final Controllable listener = item.get();
            if ( listener == null || listener.isTerminated() )
            {
                it.remove();
            }
        }

        Debug.leave();
        return listeners;
    }

    /**
     * Add the specified object at the end of this delegate list, if it does not exist yet.
     *
     * @param listener to add.
     * @return true if the listener was added and updated.
     */
    public boolean register( final Controllable listener )
    {
        Debug.enter();
        boolean updated = false;
        if ( listener != null )
        {
            // was 'listener' already added to our list?
            for ( final WeakReference<Controllable> item : filterOut() )
            {
                if ( item.get() == listener ) // must compare references, here
                {
                    Debug.leave( false, wasUpdated );
                    return false;
                }
            }
            // new listener - add it
            listeners.add( new WeakReference<Controllable>( listener ) );
            if ( wasUpdated )
            {
                listener.onControllerUpdated();
                updated = true;
            }
        }
        Debug.leave( updated, wasUpdated );
        return updated;
    }
}
