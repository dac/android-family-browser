package ca.chaves.android.util;

/*-
 * @see http://code.google.com/p/zippy-android/source/browse/trunk/examples/SharedPreferencesCompat.java
 * @see http://android-developers.blogspot.com/2010/12/new-gingerbread-api-strictmode.html
 */

/*-
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection utils to call SharedPreferences$Editor.apply when possible, falling back to commit when apply isn't
 * available.
 */
public class SharedPreferencesCompat
{
    private static final Method APPLY_METHOD = SharedPreferencesCompat.findApplyMethod();

    private static Method findApplyMethod()
    {
        try
        {
            final Class<Editor> cls = SharedPreferences.Editor.class;
            return cls.getMethod( "apply" );
        }
        catch ( final NoSuchMethodException ex )
        {
            Debug.print( "NoSuchMethodException - SharedPreferences.Editor.apply" );
        }
        return null;
    }

    /**
     * Wrapper for {@link android.content.SharedPreferences.Editor#commit()} and SharedPreferences.Editor.apply().
     *
     * @param editor the {@link android.content.SharedPreferences.Editor} to commit.
     * @return true on success.
     */
    public static boolean apply( final SharedPreferences.Editor editor )
    {
        if ( SharedPreferencesCompat.APPLY_METHOD != null )
        {
            try
            {
                SharedPreferencesCompat.APPLY_METHOD.invoke( editor );
                return true;
            }
            catch ( final InvocationTargetException ex )
            {
                Debug.error( ex, "InvocationTargetException" ); // fall through
            }
            catch ( final IllegalAccessException ex )
            {
                Debug.error( ex, "IllegalAccessException" ); // fall through
            }
        }
        return editor.commit();
    }
}
