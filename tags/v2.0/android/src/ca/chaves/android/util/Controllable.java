package ca.chaves.android.util;

import ca.chaves.android.app.AbstractActivity;
import ca.chaves.android.app.AbstractPreferenceActivity;

/**
 * Interface for controllable actors in the user-interface. For example, {@link AbstractActivity} and
 * {@link AbstractPreferenceActivity} are 'controllable's.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public interface Controllable
{
    /**
     * Is this finished or destroyed?
     *
     * @return true if this work is gone.
     */
    boolean isTerminated();

    /**
     * Callback: the model/view/controller had been updated.
     */
    void onControllerUpdated();

    /**
     * Flash an error or alert message.
     *
     * @param message to be displayed.
     */
    void flash( final String message );
}
