package ca.chaves.familyBrowser.helpers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

import android.content.Context;
import android.content.res.Resources;

/**
 * This class is our utility belt. It contains all static functions which do not
 * fit anywhere else.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class Utils {

    protected static final String TAG = Utils.class.getSimpleName();

    /**
     * Create an output file from raw resources.
     *
     * @param outputFile
     * @param context
     * @param inputRawResources
     * @throws IOException
     */
    public static void createFile(final String outputFile, final Context context, final Integer[] inputRawResources)
                    throws IOException {

        Log.d(TAG, "{ createFile", outputFile);
        final OutputStream outputStream = new FileOutputStream(outputFile);

        final Resources resources = context.getResources();
        final byte[] largeBuffer = new byte[1024 * 4];
        int totalBytes = 0;
        int bytesRead = 0;

        for (Integer resource : inputRawResources) {
            final InputStream inputStream = resources.openRawResource(resource.intValue());
            while ((bytesRead = inputStream.read(largeBuffer)) > 0) {
                if (largeBuffer.length == bytesRead) {
                    outputStream.write(largeBuffer);
                }
                else {
                    final byte[] shortBuffer = new byte[bytesRead];
                    System.arraycopy(largeBuffer, 0, shortBuffer, 0, bytesRead);
                    outputStream.write(shortBuffer);
                }
                totalBytes += bytesRead;
            }
            inputStream.close();
        }

        outputStream.flush();
        outputStream.close();
        Log.d(TAG, "} createFile", outputFile, totalBytes);
    }

    /**
     * Resize a Java array
     *
     * @param oldArray
     * @param minimumSize
     * @return the new Java array
     */
    public static Object resizeArray(final Object oldArray, final int minimumSize) {
        final Class<?> cls = oldArray.getClass();
        if (!cls.isArray()) {
            return null;
        }
        final int oldLength = Array.getLength(oldArray);
        int newLength = oldLength + (oldLength / 2); // 50% more
        if (newLength < minimumSize) {
            newLength = minimumSize;
        }
        final Class<?> componentType = oldArray.getClass().getComponentType();
        final Object newArray = Array.newInstance(componentType, newLength);
        System.arraycopy(oldArray, 0, newArray, 0, oldLength);
        return newArray;
    }
}
