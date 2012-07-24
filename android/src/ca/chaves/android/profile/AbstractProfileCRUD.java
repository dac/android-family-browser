package ca.chaves.android.profile;

import ca.chaves.android.util.AbstractAsyncTask;

/**
 * {@link AbstractProfile} CRUD operations.
 */
public abstract class AbstractProfileCRUD
    extends AbstractAsyncTask<Void, Void, Void>
{
    /**
     * Global lock for CRUD operations.
     */
    protected static final Object LOCK = new Object();
}
