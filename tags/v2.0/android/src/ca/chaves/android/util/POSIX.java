package ca.chaves.android.util;

/**
 * POSIX functions for Android.
 *
 * @see "http://code.google.com/p/android-family-browser/"
 * @author <a href="mailto:david@chaves.ca">David A Chaves</a>
 */
public class POSIX
{
    /**
     * Set the user and group of a file.
     *
     * @param path the pathname of the file.
     * @param mode the new posix permission mask.
     * @return 0 on success or errno on failure.
     */
    public static int chmod( final String path, final int mode )
    {
        if ( loaded )
        {
            return native_chmod( path, mode );
        }
        Debug.print( "skip chmod", path, "0" + Integer.toOctalString( mode ) );
        return ENOEXEC;
    }

    // ---------
    // <errno.h>
    // ---------

    /** No error. */
    public static final int EZERO = 0;

    /** Operation not permitted. */
    public static final int EPERM = 1;

    /** No such file or directory. */
    public static final int ENOENT = 2;

    /** No such process. */
    public static final int ESRCH = 3;

    /** Interrupted system call. */
    public static final int EINTR = 4;

    /** I/O error. */
    public static final int EIO = 5;

    /** No such device or address. */
    public static final int ENXIO = 6;

    /** Argument list too long. */
    public static final int E2BIG = 7;

    /** Exec format error. */
    public static final int ENOEXEC = 8;

    /** Bad file number. */
    public static final int EBADF = 9;

    /** No child processes. */
    public static final int ECHILD = 10;

    /** Try again. */
    public static final int EAGAIN = 11;

    /** Out of memory. */
    public static final int ENOMEM = 12;

    /** Permission denied. */
    public static final int EACCES = 13;

    /** Bad address. */
    public static final int EFAULT = 14;

    /** Block device required. */
    public static final int ENOTBLK = 15;

    /** Device or resource busy. */
    public static final int EBUSY = 16;

    /** File exists. */
    public static final int EEXIST = 17;

    /** Cross-device link. */
    public static final int EXDEV = 18;

    /** No such device. */
    public static final int ENODEV = 19;

    /** Not a directory. */
    public static final int ENOTDIR = 20;

    /** Is a directory. */
    public static final int EISDIR = 21;

    /** Invalid argument. */
    public static final int EINVAL = 22;

    /** File table overflow. */

    public static final int ENFILE = 23;

    /** Too many open files. */
    public static final int EMFILE = 24;

    /** Not a typewriter. */
    public static final int ENOTTY = 25;

    /** Text file busy. */
    public static final int ETXTBSY = 26;

    /** File too large. */
    public static final int EFBIG = 27;

    /** No space left on device. */
    public static final int ENOSPC = 28;

    /** Illegal seek. */
    public static final int ESPIPE = 29;

    /** Read-only file system. */
    public static final int EROFS = 30;

    /** Too many links. */
    public static final int EMLINK = 31;

    /** Broken pipe. */
    public static final int EPIPE = 32;

    /** Math argument out of domain of func. */
    public static final int EDOM = 33;

    /** Math result not representable. */
    public static final int ERANGE = 34;

    // ----------------
    // Native functions
    // ----------------

    /**
     * This flag becomes true when the native functions are available.
     */
    private static boolean loaded;

    /**
     * Set the user and group of a file.
     *
     * @param path the pathname of the file.
     * @param mode the new posix permission mask.
     * @return 0 on success or errno on failure.
     */
    private static native int native_chmod( String path, int mode );

    // --------------
    // Initialization
    // --------------

    static
    {
        final String libraryName = "ca.chaves.android";
        try
        {
            Debug.print( "loading POSIX.java native library", libraryName );
            System.loadLibrary( libraryName );
            loaded = true;
        }
        catch ( final UnsatisfiedLinkError ex )
        {
            Debug.error( ex, "POSIX.java native library not found" );
        }
    }
}
