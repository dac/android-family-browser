package ca.chaves.android.util;

/*-
 * @see https://github.com/johannilsson/sthlmtraveling/blob/master/src/com/markupartist/sthlmtraveling/utils/ErrorReporter.java
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */

import ca.chaves.android.R;
import ca.chaves.android.app.Android;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Random;

/**
 * Error reporter.
 *
 * @see "http://androidblogger.blogspot.com/2009/12/how-to-improve-your-application-crash.html"
 */
public class ErrorReporter
    implements Thread.UncaughtExceptionHandler
{
    private Thread.UncaughtExceptionHandler previousHandler;

    // --------------
    // Static members
    // --------------

    private static String packageVersion;

    private static String packageName;

    private static String errorFilesPath;

    private static String buildVersion;

    private static String buildBoard;

    private static String buildBrand;

    private static String buildDevice;

    private static String buildDisplay;

    private static String buildFingerprint;

    private static String buildHost;

    private static String buildID;

    private static String buildModel;

    private static String buildProduct;

    private static String buildTags;

    private static long buildTime;

    private static String buildType;

    private static String buildUser;

    private static long getAvailableInternalMemorySize()
    {
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs( path.getPath() );
        final long blockSize = stat.getBlockSize();
        final long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    private static long getTotalInternalMemorySize()
    {
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs( path.getPath() );
        final long blockSize = stat.getBlockSize();
        final long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    private static void recoltInformations( final Context context )
    {
        try
        {
            final PackageManager pm = context.getPackageManager();
            final PackageInfo pi = pm.getPackageInfo( context.getPackageName(), 0 );

            // Version
            ErrorReporter.packageVersion = pi.versionName;
            // Package name
            ErrorReporter.packageName = pi.packageName;
            // Directory for storing the stack traces
            ErrorReporter.errorFilesPath = context.getFilesDir().getAbsolutePath();

            // Android version
            ErrorReporter.buildVersion = android.os.Build.VERSION.RELEASE;

            ErrorReporter.buildBoard = android.os.Build.BOARD;
            ErrorReporter.buildBrand = android.os.Build.BRAND;
            ErrorReporter.buildDevice = android.os.Build.DEVICE;
            ErrorReporter.buildDisplay = android.os.Build.DISPLAY;
            ErrorReporter.buildFingerprint = android.os.Build.FINGERPRINT;
            ErrorReporter.buildHost = android.os.Build.HOST;
            ErrorReporter.buildID = android.os.Build.ID;
            ErrorReporter.buildModel = android.os.Build.MODEL;
            ErrorReporter.buildProduct = android.os.Build.PRODUCT;
            ErrorReporter.buildTags = android.os.Build.TAGS;
            ErrorReporter.buildTime = android.os.Build.TIME;
            ErrorReporter.buildType = android.os.Build.TYPE;
            ErrorReporter.buildUser = android.os.Build.USER;
        }
        catch ( final NameNotFoundException ex )
        {
            Debug.print( "name not found", ex );
        }
    }

    private static final char NL = '\n';

    private static final String NL_NL = "\n\n";

    private static void appendInformations( final StringBuilder report )
    {
        report.append( "Version : " ).append( ErrorReporter.packageVersion );
        report.append( NL );
        report.append( "Package : " ).append( ErrorReporter.packageName );
        report.append( NL );
        report.append( "Error Files : " ).append( ErrorReporter.errorFilesPath );
        report.append( NL );
        report.append( "Android Version : " ).append( ErrorReporter.buildVersion );
        report.append( NL );
        report.append( "Board : " ).append( ErrorReporter.buildBoard );
        report.append( NL );
        report.append( "Brand : " ).append( ErrorReporter.buildBrand );
        report.append( NL );
        report.append( "Device : " ).append( ErrorReporter.buildDevice );
        report.append( NL );
        report.append( "Display : " ).append( ErrorReporter.buildDisplay );
        report.append( NL );
        report.append( "Finger Print : " ).append( ErrorReporter.buildFingerprint );
        report.append( NL );
        report.append( "Host : " ).append( ErrorReporter.buildHost );
        report.append( NL );
        report.append( "ID : " ).append( ErrorReporter.buildID );
        report.append( NL );
        report.append( "Model : " ).append( ErrorReporter.buildModel );
        report.append( NL );
        report.append( "Product : " ).append( ErrorReporter.buildProduct );
        report.append( NL );
        report.append( "Tags : " ).append( ErrorReporter.buildTags );
        report.append( NL );
        report.append( "Time : " ).append( ErrorReporter.buildTime );
        report.append( NL );
        report.append( "Type : " ).append( ErrorReporter.buildType );
        report.append( NL );
        report.append( "User : " ).append( ErrorReporter.buildUser );
        report.append( NL );
        report.append( "Total Internal memory : " ).append( Long.toString( ErrorReporter.getTotalInternalMemorySize() ) );
        report.append( NL );
        report.append( "Available Internal memory : " ).append( Long.toString( ErrorReporter.getAvailableInternalMemorySize() ) );
        report.append( NL );
    }

    private static void appendStackTrace( final StringBuilder report, final Throwable ex )
    {
        final Writer writer = new StringWriter();

        final PrintWriter printWriter = new PrintWriter( writer );
        ex.printStackTrace( printWriter );
        printWriter.close();

        report.append( writer.toString() );
    }

    private static void sendErrorMail( final Context context, final String content )
    {
        final Resources resources = context.getResources();

        final String[] sendto = resources.getStringArray( R.array.crash_report_mail_sendto );
        final String subject = resources.getString( R.string.crash_report_mail_subject );
        final String body = resources.getString( R.string.crash_report_mail_body );

        if ( sendto.length <= 0 )
        {
            Debug.print( "will not send error email to anybody" );
        }
        else
        {
            final Intent sendIntent = new Intent( Intent.ACTION_SEND );
            sendIntent.setType( "message/rfc822" );

            sendIntent.putExtra( Intent.EXTRA_EMAIL, sendto );
            sendIntent.putExtra( Intent.EXTRA_TEXT, body + NL_NL + content + NL_NL );
            sendIntent.putExtra( Intent.EXTRA_SUBJECT, subject );

            context.startActivity( Intent.createChooser( sendIntent, "Title:" ) );
        }
    }

    private static void saveAsFile( final String content )
    {
        final int random = new Random().nextInt( 99999 );
        final String filename = "stack-" + random + ".stacktrace";
        try
        {
            final FileOutputStream trace = Android.App.INSTANCE.openFileOutput( filename, Context.MODE_PRIVATE );
            trace.write( content.getBytes() );
            trace.close();
        }
        catch ( final IOException ex )
        {
            Debug.error( ex, "error saving stacktrace", filename );
        }
    }

    private static final FilenameFilter STACK_TRACE_FILENAME_FILTER = new FilenameFilter()
    {
        @Override
        public boolean accept( final File dir, final String name )
        {
            return name.endsWith( ".stacktrace" );
        }
    };

    private static String[] getErrorFileList()
    {
        final File dir = new File( ErrorReporter.errorFilesPath + "/" );
        try
        {
            // Try to create the files folder if it doesn't exist
            dir.mkdir();
            // Filter for ".stacktrace" files
            return dir.list( ErrorReporter.STACK_TRACE_FILENAME_FILTER );
        }
        catch ( final SecurityException ex )
        {
            Debug.error( ex, "mkdir failed", dir );
        }
        // must always return a valid array
        return new String[0];
    }

    private static boolean deleteFile( final String basename )
    {
        // DELETE FILES !!!!
        final File file = new File( ErrorReporter.errorFilesPath + "/" + basename );
        return file.delete();
    }

    // --------------
    // Public members
    // --------------

    /**
     * Initialize error reporter.
     *
     * @param context the {@link Application} context.
     */
    public void init( final Context context )
    {
        ErrorReporter.recoltInformations( context );

        previousHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uncaughtException( final Thread tr, final Throwable ex )
    {
        final Date today = new Date();
        final StringBuilder report = new StringBuilder( 2000 );

        report.append( "Error Report collected on : " ).append( today.toString() );
        report.append( NL_NL );
        report.append( "Information : \n" );
        report.append( "============= \n" );

        ErrorReporter.appendInformations( report );

        report.append( NL_NL );
        report.append( "Stack : \n" );
        report.append( "======= \n" );

        ErrorReporter.appendStackTrace( report, ex );

        report.append( NL_NL );
        report.append( "Cause : \n" );
        report.append( "======= \n" );

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        for ( Throwable cause = ex.getCause(); cause != null; cause = cause.getCause() )
        {
            ErrorReporter.appendStackTrace( report, cause );
        }

        report.append( "****  End of Report ***" );

        ErrorReporter.saveAsFile( report.toString() );

        // SendErrorMail( Report );
        previousHandler.uncaughtException( tr, ex );
    }

    /**
     * Send email with error logs.
     *
     * @param context the {@link Application} context.
     */
    public static void checkErrorAndSendMail( final Context context )
    {
        // We limit the number of crash reports to send ( in order not
        // to be too slow )
        final int maxSendMailCount = 5;

        try
        {
            final String[] errorFileList = ErrorReporter.getErrorFileList();
            if ( errorFileList == null || errorFileList.length <= 0 )
            {
                Debug.print( "no error files found" );
                return;
            }

            final StringBuilder report = new StringBuilder( 2000 );
            int count = 0;
            for ( final String basename : errorFileList )
            {
                if ( ++count <= maxSendMailCount )
                {
                    report.append( "New Trace collected :\n" );
                    report.append( "=====================\n " );

                    final String filename = ErrorReporter.errorFilesPath + "/" + basename;

                    final BufferedReader input = new BufferedReader( new FileReader( filename ) );
                    String line;
                    while ( null != ( line = input.readLine() ) )
                    {
                        report.append( line ).append( "\n" );
                    }
                    input.close();
                }

                // DELETE FILES !!!!
                ErrorReporter.deleteFile( basename );
            }
            ErrorReporter.sendErrorMail( context, report.toString() );
        }
        catch ( final IOException ex )
        {
            Debug.error( ex, "error sending email" );
        }
    }

    /**
     * Display dialog to send email logs.
     *
     * @param context the {@link Application} context.
     */
    public static void checkErrorAndReport( final Context context )
    {
        final OnClickListener okayListener = new OnClickListener()
        {
            @Override
            public void onClick( final DialogInterface dialog, final int which )
            {
                ErrorReporter.checkErrorAndSendMail( context );
            }
        };

        final OnClickListener cancelListener = new OnClickListener()
        {
            @Override
            public void onClick( final DialogInterface dialog, final int which )
            {
                final String[] errorFileList = ErrorReporter.getErrorFileList();
                if ( errorFileList != null )
                {
                    for ( final String filename : errorFileList )
                    {
                        ErrorReporter.deleteFile( filename );
                    }
                }
            }
        };

        final String message = context.getString( R.string.crash_report_dialog_prompt );

        final AlertDialog.Builder dialog = new AlertDialog.Builder( context );
        dialog.setTitle( context.getString( R.string.crash_report_dialog_title ) );
        dialog.setMessage( message );
        dialog.setPositiveButton( context.getString( android.R.string.ok ), okayListener );
        dialog.setNegativeButton( context.getString( android.R.string.cancel ), cancelListener );
        dialog.show();
    }
}
