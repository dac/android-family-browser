/*-
 ** Authored by Timothy Gerard Endres
 ** <mailto:time@gjt.org>  <http://www.trustice.com>
 **
 ** This work has been placed into the public domain.
 ** You may use this work in any way and for any purpose you wish.
 **
 ** THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND,
 ** NOT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR
 ** OF THIS SOFTWARE, ASSUMES _NO_ RESPONSIBILITY FOR ANY
 ** CONSEQUENCE RESULTING FROM THE USE, MODIFICATION, OR
 ** REDISTRIBUTION OF THIS SOFTWARE.
 */

package ca.chaves.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * The TarArchive class implements the concept of a tar archive. A tar archive is a series of entries, each of which
 * represents a file system object. Each entry in the archive consists of a header record. Directory entries consist
 * only of the header record, and are followed by entries for the directory's contents. File entries consist of a header
 * record followed by the number of records needed to contain the file's contents. All entries are written on record
 * boundaries. Records are 512 bytes long. TarArchives are instantiated in either read or write mode, based upon whether
 * they are instantiated with an InputStream or an OutputStream. Once instantiated TarArchives read/write mode can not
 * be changed. There is currently no support for random access to tar archives.
 *
 * @author Timothy Gerard Endres, <time@gjt.org>
 */
public final class TarArchive
{
    /**
     * {@link TarArchive} exception.
     */
    public static class TarException
        extends IOException
    {
        /**
         * Serial version UID.
         */
        private static final long serialVersionUID = -3304014646080720885L;

        /**
         * Constructor.
         *
         * @param args arguments.
         */
        public TarException( final Object... args )
        {
            super( t( args ) );
            Debug.print( "throw TarException", this );
        }

        /**
         * Build detail message.
         *
         * @param args arguments.
         * @return detail message.
         */
        public static String t( final Object[] args )
        {
            final StringBuilder builder = new StringBuilder();
            String delimiter = "";
            for ( final Object object : args )
            {
                builder.append( delimiter );
                builder.append( object.toString() );
                delimiter = " ";
            }
            return builder.toString();
        }
    }

    /**
     * This exception is used to indicate that there is a problem with a TAR archive header.
     */
    public static final class TarHeaderException
        extends TarException
    {
        /**
         * Serial version UID.
         */
        private static final long serialVersionUID = 814571440651488879L;

        /**
         * Constructor.
         *
         * @param args arguments.
         */
        public TarHeaderException( final Object... args )
        {
            super( args );
        }
    }

    /**
     * This flag must be false in order to overwrite any existing file. If this flag is true, then all existing files
     * are kept, while that all missing files are restored automatically.
     */
    public boolean keepOldFiles = true;

    /**
     * The input stream.
     */
    private final TarInputStream tarInputStream;

    /**
     * The output stream.
     */
    private final TarOutputStream tarOutputStream;

    /**
     * Constructor. The InputStream based constructors create a TarArchive for the purposes of e'x'tracting or lis't'ing
     * a tar archive. Thus, use these constructors when you wish to extract files from or list the contents of an
     * existing tar archive.
     *
     * @param inputStream the tar archive source.
     */
    public TarArchive( final InputStream inputStream )
    {
        this( inputStream, TarBuffer.DEFAULT_BLOCK_SIZE );
    }

    /**
     * Constructor.
     *
     * @param inputStream the tar archive source.
     * @param blockSize the block size.
     */
    public TarArchive( final InputStream inputStream, final int blockSize )
    {
        this( inputStream, blockSize, TarBuffer.DEFAULT_RECORD_SIZE );
    }

    /**
     * Constructor.
     *
     * @param inputStream the tar archive source.
     * @param blockSize the archive block size.
     * @param recordSize the archive record size.
     */
    public TarArchive( final InputStream inputStream, final int blockSize, final int recordSize )
    {
        tarInputStream = new TarInputStream( inputStream, blockSize, recordSize );
        tarOutputStream = null;
    }

    /**
     * Constructor. The OutputStream based constructors create a TarArchive for the purposes of 'c'reating a tar
     * archive. Thus, use these constructors when you wish to create a new tar archive and write files into it.
     *
     * @param outputStream the tar archive output.
     */
    public TarArchive( final OutputStream outputStream )
    {
        this( outputStream, TarBuffer.DEFAULT_BLOCK_SIZE );
    }

    /**
     * Constructor.
     *
     * @param outputStream the tar archive output.
     * @param blockSize the archive block size.
     */
    public TarArchive( final OutputStream outputStream, final int blockSize )
    {
        this( outputStream, blockSize, TarBuffer.DEFAULT_RECORD_SIZE );
    }

    /**
     * Constructor.
     *
     * @param outputStream the tar archive output.
     * @param blockSize the archive block size.
     * @param recordSize the archive record size.
     */
    public TarArchive( final OutputStream outputStream, final int blockSize, final int recordSize )
    {
        tarInputStream = null;
        tarOutputStream = new TarOutputStream( outputStream, blockSize, recordSize );
    }

    /**
     * Close the archive. This simply calls the underlying tar stream's close() method.
     *
     * @throws IOException on failure.
     */
    public void close()
        throws IOException
    {
        if ( tarInputStream != null )
        {
            tarInputStream.close();
        }
        if ( tarOutputStream != null )
        {
            tarOutputStream.close();
        }
    }

    /**
     * Perform the "extract" command and extract the contents of the archive.
     *
     * @param targetDirectory The destination directory into which to extract.
     * @return number of entries extracted.
     * @throws IOException on failure.
     */
    public int extract( final File targetDirectory )
        throws IOException
    {
        for ( int extractCount = 0;; ++extractCount )
        {
            final TarEntry entry = tarInputStream.getNextEntry();
            if ( entry == null )
            {
                return extractCount;
            }
            extractEntry( targetDirectory, entry );
        }
    }

    /**
     * Extract an entry from the archive. This method assumes that the tarIn stream has been properly set with a call to
     * getNextEntry().
     *
     * @param targetDirectory The destination directory into which to extract.
     * @param entry The TarEntry returned by tarIn.getNextEntry().
     */
    private void extractEntry( final File targetDirectory, final TarEntry entry )
        throws IOException
    {
        final String entryName = entry.getName().replace( '/', File.separatorChar );
        final File entryFile = new File( targetDirectory, entryName );

        if ( entry.isDirectory() )
        {
            Debug.print( "extract directory:", entryFile.getAbsolutePath() );
            if ( !entryFile.exists() )
            {
                if ( !entryFile.mkdirs() )
                {
                    throw new TarException( "could not make directory", entryFile.getPath() );
                }
                // fix directory permissions
                if ( POSIX.chmod( entryFile.getAbsolutePath(), entry.mode ) != POSIX.EZERO )
                {
                    Debug.print( "unable to fix directory permissions", entryFile, Integer.toOctalString( entry.mode ) );
                }
            }
            return;
        }

        final File parentFile = entryFile.getParentFile();
        if ( !parentFile.exists() && !parentFile.mkdirs() )
        {
            throw new TarException( "could not make directory", parentFile.getPath() );
        }

        if ( keepOldFiles && entryFile.exists() )
        {
            return; // file exists - do not overwrite
        }

        Debug.print( "extract file:", entryFile.getAbsolutePath() );
        final FileOutputStream os = new FileOutputStream( entryFile );
        try
        {
            final byte[] buffer = new byte[32 * 1024];
            for ( ;; )
            {
                final int count = tarInputStream.read( buffer );
                if ( count == -1 )
                {
                    break;
                }
                os.write( buffer, 0, count );
            }
        }
        finally
        {
            os.close();
        }

        // fix file permissions
        if ( POSIX.chmod( entryFile.getAbsolutePath(), entry.mode ) != POSIX.EZERO )
        {
            Debug.print( "unable to fix file permissions", entryFile, Integer.toOctalString( entry.mode ) );
        }
    }

    /**
     * Write an entry to the archive. This method will call the putNextEntry() and then write the contents of the entry,
     * and finally call closeEntry() for entries that are files. For directories, it will call putNextEntry(), and then,
     * if the recurse flag is true, process each entry that is a child of the directory.
     *
     * @param entry The TarEntry representing the entry to write to the archive.
     * @param recurse If true, process the children of directory entries.
     * @throws IOException on failure.
     */
    public void add( final TarEntry entry, final boolean recurse )
        throws IOException
    {
        tarOutputStream.putNextEntry( entry );

        if ( entry.isDirectory() )
        {
            if ( !recurse )
            {
                return;
            }
            for ( final TarEntry subentry : entry.getDirectoryEntries() )
            {
                if ( entry.isGnuTarFormat )
                {
                    subentry.setGnuTarFormat();
                }
                if ( entry.isUnixTarFormat )
                {
                    subentry.setUnixTarFormat();
                }
                if ( entry.isUsTarFormat )
                {
                    subentry.setUsTarFormat();
                }
                add( subentry, recurse );
            }
            return;
        }

        final FileInputStream is = new FileInputStream( entry.file );
        try
        {
            final byte[] buffer = new byte[32 * 1024];
            for ( ;; )
            {
                final int count = is.read( buffer, 0, buffer.length );
                if ( count == -1 )
                {
                    break;
                }
                tarOutputStream.write( buffer, 0, count );
            }
        }
        finally
        {
            is.close();
        }

        tarOutputStream.closeEntry();
    }

    /**
     * The TarOutputStream writes a UNIX tar archive as an OutputStream. Methods are provided to put entries, and then
     * write their contents by writing to this stream using write(). Kerry Menzel <kmenzel@cfl.rr.com> Contributed the
     * code to support file sizes greater than 2GB (longs versus ints).
     */
    private static final class TarOutputStream
        extends FilterOutputStream
    {
        private long currentSize;

        private long currentBytes;

        private int assemLength;

        private final byte[] assemBuffer;

        private final byte[] recordBuffer;

        private final byte[] tinyBuffer = new byte[1];

        private final TarBuffer tarBuffer;

        public TarOutputStream( final OutputStream outputStream, final int blockSize, final int recordSize )
        {
            super( outputStream );
            tarBuffer = new TarBuffer( null, outputStream, blockSize, recordSize );
            assemBuffer = new byte[recordSize];
            recordBuffer = new byte[recordSize];
        }

        /**
         * Ends the TAR archive without closing the underlying OutputStream. The result is that the EOF record of nulls
         * is written.
         *
         * @throws IOException on failure.
         */
        public void finish()
            throws IOException
        {
            writeEOFRecord();
        }

        /**
         * Ends the TAR archive and closes the underlying OutputStream. This means that finish() is called followed by
         * calling the TarBuffer's close().
         */
        @Override
        public void close()
            throws IOException
        {
            finish();
            tarBuffer.close();
        }

        /**
         * Put an entry on the output stream. This writes the entry's header record and positions the output stream for
         * writing the contents of the entry. Once this method is called, the stream is ready for calls to write() to
         * write the entry's contents. Once the contents are written, closeEntry() <B>MUST</B> be called to ensure that
         * all buffered data is completely written to the output stream.
         *
         * @param entry The TarEntry to be written to the archive.
         * @throws IOException on failure.
         */
        public void putNextEntry( final TarEntry entry )
            throws IOException
        {
            final StringBuffer name = entry.name;

            // NOTE
            // This check is not adequate, because the maximum file length that
            // can be placed into a POSIX (ustar) header depends on the precise
            // locations of the path elements (slashes) within the file's full
            // pathname. For this reason, writeEntryHeader() can still throw an
            // InvalidHeaderException if the file's full pathname will not fit
            // in the header.
            final int maxLength =
                entry.isUnixTarFormat ? TarEntry.NAME_LENGTH : ( TarEntry.NAME_LENGTH + TarEntry.PREFIX_LENGTH );

            if ( name.length() > maxLength )
            {
                throw new TarHeaderException( "file name", "'" + name + "'", //
                                              "is too long:", //
                                              name.length(), ">", +maxLength + "bytes" );
            }

            entry.writeEntryHeader( recordBuffer );
            tarBuffer.writeRecord( recordBuffer );

            currentBytes = 0;
            currentSize = ( entry.isDirectory() ? 0 : entry.size );
        }

        /**
         * Close an entry. This method MUST be called for all file entries that contain data. The reason is that we must
         * buffer data written to the stream in order to satisfy the buffer's record based writes. Thus, there may be
         * data fragments still being assembled that must be written to the output stream before this entry is closed
         * and the next entry written.
         *
         * @throws IOException on failure.
         */
        public void closeEntry()
            throws IOException
        {
            if ( assemLength > 0 )
            {
                for ( int i = assemLength; i < assemBuffer.length; ++i )
                {
                    assemBuffer[i] = 0;
                }
                tarBuffer.writeRecord( assemBuffer );
                currentBytes += assemLength;
                assemLength = 0;
            }

            if ( currentBytes < currentSize )
            {
                throw new TarException( "entry closed at", currentBytes, "before the", currentSize,
                                        "bytes specified in the header were written" );
            }
        }

        /**
         * Writes a byte to the current tar archive entry. This method simply calls read( byte[], int, int ).
         *
         * @param value The byte written.
         */
        @Override
        public void write( final int value )
            throws IOException
        {
            tinyBuffer[0] = (byte) value;
            this.write( tinyBuffer, 0, 1 );
        }

        /**
         * Writes bytes to the current tar archive entry. This method simply calls read( byte[], int, int ).
         *
         * @param buffer The buffer to write to the archive.
         */
        @Override
        public void write( final byte[] buffer )
            throws IOException
        {
            this.write( buffer, 0, buffer.length );
        }

        /**
         * Writes bytes to the current tar archive entry. This method is aware of the current entry and will throw an
         * exception if you attempt to write bytes past the length specified for the current entry. The method is also
         * (painfully) aware of the record buffering required by TarBuffer, and manages buffers that are not a multiple
         * of record size in length, including assembling records from small buffers. This method simply calls read(
         * byte[], int, int ).
         *
         * @param buffer The buffer to write to the archive.
         * @param offset The offset in the buffer from which to get bytes.
         * @param count The number of bytes to write.
         */
        @Override
        public void write( final byte[] buffer, final int offset, final int count )
            throws IOException
        {
            int index = offset;
            int nbytes = count;

            if ( ( currentBytes + nbytes ) > currentSize )
            {
                throw new TarException( "request to write", nbytes, "bytes", //
                                        "exceeds size in header of", currentSize, "bytes" );
            }

            //
            // We have to deal with assembly!!!
            // The programmer can be writing little 32 byte chunks for all
            // we know, and we must assemble complete records for writing.
            // REVIEW Maybe this should be in TarBuffer? Could that help to
            // eliminate some of the buffer copying.
            if ( assemLength > 0 )
            {
                if ( ( assemLength + nbytes ) >= recordBuffer.length )
                {
                    final int arrayLength = recordBuffer.length - assemLength;
                    System.arraycopy( assemBuffer, 0, recordBuffer, 0, assemLength );
                    System.arraycopy( buffer, index, recordBuffer, assemLength, arrayLength );

                    tarBuffer.writeRecord( recordBuffer );
                    currentBytes += recordBuffer.length;

                    index += arrayLength;
                    nbytes -= arrayLength;
                    assemLength = 0;
                }
                else
                {
                    System.arraycopy( buffer, index, assemBuffer, assemLength, nbytes );
                    index += nbytes;
                    assemLength += nbytes;
                    nbytes = 0;
                }
            }

            // When we get here we have EITHER:
            // o An empty "assemble" buffer.
            // o No bytes to write (numToWrite == 0)

            while ( nbytes > 0 )
            {
                if ( nbytes < recordBuffer.length )
                {
                    System.arraycopy( buffer, index, assemBuffer, assemLength, nbytes );
                    assemLength += nbytes;
                    break;
                }

                tarBuffer.writeRecord( buffer, index );

                final long num = recordBuffer.length;
                currentBytes += num;
                nbytes -= num;
                index += num;
            }
        }

        /**
         * Write an EOF (end of archive) record to the tar archive. An EOF record consists of a record of all zeros.
         */
        private void writeEOFRecord()
            throws IOException
        {
            for ( int index = 0; index < recordBuffer.length; ++index )
            {
                recordBuffer[index] = 0;
            }
            tarBuffer.writeRecord( recordBuffer );
        }
    }

    /**
     * The TarInputStream reads a UNIX tar archive as an InputStream. methods are provided to position at each
     * successive entry in the archive, and the read each entry as a normal input stream using read(). Kerry Menzel
     * <kmenzel@cfl.rr.com> contributed the code to support file sizes greater than 2GB (longs versus ints).
     */
    private static final class TarInputStream
        extends FilterInputStream
    {
        protected boolean hasHitEOF;

        protected long entrySize;

        protected long entryOffset;

        protected final byte[] tinyBuffer = new byte[1];

        protected byte[] readBuffer;

        protected TarBuffer tarBuffer;

        protected TarEntry currentEntry;

        public TarInputStream( final InputStream inputStream, final int blockSize, final int recordSize )
        {
            super( inputStream );
            tarBuffer = new TarBuffer( inputStream, null, blockSize, recordSize );
        }

        /**
         * Closes this stream. Calls the TarBuffer's close() method.
         */
        @Override
        public void close()
            throws IOException
        {
            tarBuffer.close();
        }

        /**
         * Get the available data that can be read from the current entry in the archive. This does not indicate how
         * much data is left in the entire archive, only in the current entry. This value is determined from the entry's
         * size header field and the amount of data already read from the current entry.
         *
         * @return The number of available bytes for the current entry.
         */
        @Override
        public int available()
        {
            return (int) ( entrySize - entryOffset );
        }

        /**
         * Skip bytes in the input buffer. This skips bytes in the current entry's data, not the entire archive, and
         * will stop at the end of the current entry's data if the number to skip extends beyond that point.
         *
         * @param numToSkip The number of bytes to skip.
         * @return The actual number of bytes skipped.
         */
        @Override
        public long skip( final long numToSkip )
            throws IOException
        {
            // This is horribly inefficient, but it ensures that we
            // properly skip over bytes via the TarBuffer...
            final byte[] skipBuffer = new byte[8 * 1024];
            long num = numToSkip;
            while ( num > 0 )
            {
                final int maxRead = ( num > skipBuffer.length ? skipBuffer.length : (int) num );
                final int numRead = this.read( skipBuffer, 0, maxRead );
                if ( numRead == -1 )
                {
                    break;
                }
                num -= numRead;
            }
            final long result = ( numToSkip - num );
            return result;
        }

        /**
         * Since we do not support marking just yet, we return false.
         *
         * @return False.
         */
        @Override
        public boolean markSupported()
        {
            return false;
        }

        /**
         * Since we do not support marking just yet, we do nothing.
         *
         * @param markLimit The limit to mark.
         */
        @Override
        public synchronized void mark( final int markLimit )
        {
            // nothing
        }

        /**
         * Since we do not support marking just yet, we do nothing.
         */
        @Override
        public synchronized void reset()
        {
            // nothing
        }

        /**
         * Get the next entry in this tar archive. This will skip over any remaining data in the current entry, if there
         * is one, and place the input stream at the header of the next entry, and read the header and instantiate a new
         * TarEntry from the header bytes and return that entry. If there are no more entries in the archive, null will
         * be returned to indicate that the end of the archive has been reached.
         *
         * @return The next TarEntry in the archive, or null.
         * @throws IOException on failure.
         */
        public TarEntry getNextEntry()
            throws IOException
        {
            if ( hasHitEOF )
            {
                return null;
            }
            if ( currentEntry != null )
            {
                final long numToSkip = ( entrySize - entryOffset );
                if ( numToSkip > 0 )
                {
                    @SuppressWarnings( "unused" )
                    long unused = skip( numToSkip );
                }
                readBuffer = null;
            }
            final byte[] headerBuffer = tarBuffer.readRecord();
            if ( headerBuffer == null )
            {
                hasHitEOF = true;
            }
            else if ( tarBuffer.isEOFRecord( headerBuffer ) )
            {
                hasHitEOF = true;
            }
            if ( hasHitEOF )
            {
                currentEntry = null;
                return null;
            }

            try
            {
                currentEntry = new TarEntry( headerBuffer );
                entryOffset = 0;
                entrySize = currentEntry.size;
            }
            catch ( final TarHeaderException ex )
            {
                entrySize = 0;
                entryOffset = 0;
                currentEntry = null;
                throw new TarHeaderException( "bad header in block", tarBuffer.getCurrentBlockNumber(), "record",
                                              tarBuffer.getCurrentRecordNumber(), //
                                              ":", ex.getMessage() );
            }

            return currentEntry;
        }

        /**
         * Reads a byte from the current tar archive entry. This method simply calls read( byte[], int, int ).
         *
         * @return The byte read, or -1 at EOF.
         */
        @Override
        public int read()
            throws IOException
        {
            final int count = this.read( tinyBuffer, 0, 1 );
            if ( count == -1 )
            {
                return count;
            }
            return tinyBuffer[0];
        }

        /**
         * Reads bytes from the current tar archive entry. This method simply calls read( byte[], int, int ).
         *
         * @param buffer The buffer into which to place bytes read.
         * @return The number of bytes read, or -1 at EOF.
         */
        @Override
        public int read( final byte[] buffer )
            throws IOException
        {
            return read( buffer, 0, buffer.length );
        }

        /**
         * Reads bytes from the current tar archive entry. This method is aware of the boundaries of the current entry
         * in the archive and will deal with them as if they were this stream's start and EOF.
         *
         * @param buffer The buffer into which to place bytes read.
         * @param offset The offset at which to place bytes read.
         * @param count The number of bytes to read.
         * @return The number of bytes read, or -1 at EOF.
         */
        @Override
        public int read( final byte[] buffer, final int offset, final int count )
            throws IOException
        {
            int index = offset;
            int nbytes = count;

            if ( entrySize <= entryOffset )
            {
                return -1;
            }

            if ( entrySize < ( nbytes + entryOffset ) )
            {
                nbytes = (int) ( entrySize - entryOffset );
            }

            int totalRead = 0;
            if ( readBuffer != null )
            {
                final int readLength = ( nbytes > readBuffer.length ) ? readBuffer.length : nbytes;

                System.arraycopy( readBuffer, 0, buffer, index, readLength );

                if ( readBuffer.length <= readLength )
                {
                    readBuffer = null;
                }
                else
                {
                    final int newLength = readBuffer.length - readLength;
                    final byte[] newBuffer = new byte[newLength];
                    System.arraycopy( readBuffer, readLength, newBuffer, 0, newLength );
                    readBuffer = newBuffer;
                }

                totalRead += readLength;
                nbytes -= readLength;
                index += readLength;
            }

            while ( 0 < nbytes )
            {
                final byte[] currentRecord = tarBuffer.readRecord();
                if ( currentRecord == null )
                {
                    // Unexpected EOF!
                    throw new TarException( "unexpected EOF with", nbytes, "bytes unread" );
                }

                int readLength = nbytes;
                final int recordLen = currentRecord.length;

                if ( readLength < recordLen )
                {
                    System.arraycopy( currentRecord, 0, buffer, index, readLength );
                    readBuffer = new byte[recordLen - readLength];
                    System.arraycopy( currentRecord, readLength, readBuffer, 0, recordLen - readLength );
                }
                else
                {
                    readLength = recordLen;
                    System.arraycopy( currentRecord, 0, buffer, index, recordLen );
                }

                totalRead += readLength;
                nbytes -= readLength;
                index += readLength;
            }

            entryOffset += totalRead;
            return totalRead;
        }
    }

    /**
     * The TarBuffer class implements the tar archive concept of a buffered input stream. This concept goes back to the
     * days of blocked tape drives and special io devices. In the Java universe, the only real function that this class
     * performs is to ensure that files have the correct "block" size, or other tars will complain.
     * <p>
     * You should never have a need to access this class directly. TarBuffers are created by Tar IO Streams.
     */
    private static final class TarBuffer
    {
        public static final int DEFAULT_RECORD_SIZE = ( 512 );

        public static final int DEFAULT_BLOCK_SIZE = ( TarBuffer.DEFAULT_RECORD_SIZE * 20 );

        private final InputStream inputStream;

        private final OutputStream outputStream;

        private final byte[] blockBuffer;

        private final int blockSize;

        private final int recordSize;

        private final int recordsPerBlock;

        private int currentBlockIndex;

        private int currentRecordIndex;

        public TarBuffer( final InputStream inputStream, final OutputStream outputStream, final int blockSize,
                          final int recordSize )
        {

            this.inputStream = inputStream;
            this.outputStream = outputStream;

            this.blockSize = blockSize;
            this.recordSize = recordSize;

            recordsPerBlock = ( blockSize / recordSize );
            blockBuffer = new byte[blockSize];

            if ( this.inputStream != null )
            {
                currentBlockIndex = -1;
                currentRecordIndex = recordsPerBlock;
            }
        }

        /**
         * Determine if an archive record indicate End of Archive. End of archive is indicated by a record that consists
         * entirely of null bytes.
         *
         * @param buffer The record data to check.
         * @return true on EOF.
         */
        public boolean isEOFRecord( final byte[] buffer )
        {
            for ( int index = 0; index < recordSize; ++index )
            {
                if ( buffer[index] != 0 )
                {
                    return false;
                }
            }
            return true;
        }

        /**
         * Read a record from the input stream and return the data.
         *
         * @return The record data.
         * @throws IOException on failure.
         */
        public byte[] readRecord()
            throws IOException
        {
            if ( inputStream == null )
            {
                throw new TarException( "reading from an output buffer" );
            }
            if ( recordsPerBlock <= currentRecordIndex && !readBlock() )
            {
                return null;
            }

            final byte[] buffer = new byte[recordSize];
            System.arraycopy( blockBuffer, ( currentRecordIndex * recordSize ), buffer, 0, recordSize );
            ++currentRecordIndex;
            return buffer;
        }

        /**
         * Read next block.
         *
         * @return false if End-Of-File, else true.
         */
        private boolean readBlock()
            throws IOException
        {
            if ( inputStream == null )
            {
                throw new TarException( "reading from an output buffer" );
            }

            currentRecordIndex = 0;
            int offset = 0;
            int bytesNeeded = blockSize;
            while ( 0 < bytesNeeded )
            {
                final long count = inputStream.read( blockBuffer, offset, bytesNeeded );
                // NOTE: We have fit EOF, and the block is not full!
                //
                // This is a broken archive. It does not follow the standard
                // blocking algorithm. However, because we are generous, and
                // it requires little effort, we will simply ignore the error
                // and continue as if the entire block were read. This does
                // not appear to break anything upstream. We used to return
                // false in this case.
                //
                // Thanks to 'Yohann.Roussel@alcatel.fr' for this fix.
                if ( count == -1 )
                {
                    break;
                }

                offset += count;
                bytesNeeded -= count;
                // if ( count != blockSize ), incomplete read numBytes of this.blockSize bytes read
            }

            ++currentBlockIndex;
            return true;
        }

        /**
         * Get the current block number, zero based.
         *
         * @return The current zero based block number.
         */
        public int getCurrentBlockNumber()
        {
            return currentBlockIndex;
        }

        /**
         * Get the current record number, within the current block, zero based. Thus, current offset = (currentBlockNum
         * * recsPerBlk) + currentRecNum.
         *
         * @return The current zero based record number.
         */
        public int getCurrentRecordNumber()
        {
            return currentRecordIndex - 1;
        }

        /**
         * Write an archive record to the archive.
         *
         * @param buffer The record data to write to the archive.
         * @throws IOException on failure.
         */
        public void writeRecord( final byte[] buffer )
            throws IOException
        {
            if ( outputStream == null )
            {
                throw new TarException( "writing to an input buffer" );
            }

            if ( buffer.length != recordSize )
            {
                throw new TarException( "record to write has length", buffer.length, //
                                        "which is not the record size of", recordSize );
            }

            if ( recordsPerBlock <= currentRecordIndex )
            {
                writeBlock();
            }

            System.arraycopy( buffer, 0, blockBuffer, ( currentRecordIndex * recordSize ), recordSize );

            ++currentRecordIndex;
        }

        /**
         * Write an archive record to the archive, where the record may be inside of a larger array buffer. The buffer
         * must be "offset plus record size" long.
         *
         * @param buffer The buffer containing the record data to write.
         * @param offset The offset of the record data within buffer.
         * @throws IOException on failure.
         */
        public void writeRecord( final byte[] buffer, final int offset )
            throws IOException
        {
            if ( outputStream == null )
            {
                throw new TarException( "writing to an input buffer" );
            }
            if ( buffer.length < ( offset + recordSize ) )
            {
                throw new TarException( "record has length", buffer.length, //
                                        "with offset", offset, //
                                        "which is less than the record size of", recordSize );
            }

            if ( recordsPerBlock <= currentRecordIndex )
            {
                writeBlock();
            }

            System.arraycopy( buffer, offset, blockBuffer, ( currentRecordIndex * recordSize ), recordSize );

            ++currentRecordIndex;
        }

        /**
         * Write a TarBuffer block to the archive.
         */
        private void writeBlock()
            throws IOException
        {
            if ( outputStream == null )
            {
                throw new TarException( "writing to an input buffer" );
            }

            outputStream.write( blockBuffer, 0, blockSize );
            outputStream.flush();

            currentRecordIndex = 0;
            ++currentBlockIndex;
        }

        /**
         * Flush the current data block if it has any data in it.
         */
        private void flushBlock()
            throws IOException
        {
            if ( outputStream == null )
            {
                throw new TarException( "writing to an input buffer" );
            }

            // Thanks to 'Todd Kofford <tkofford@bigfoot.com>' for this patch.
            // Use a buffer initialized with 0s to initialize everything in the
            // blockBuffer after the last current, complete record. This
            // prevents
            // any previous data that might have previously existed in the
            // blockBuffer from being written to the file.

            if ( 0 < currentRecordIndex )
            {
                final int offset = currentRecordIndex * recordSize;
                final byte[] zeroBuffer = new byte[blockSize - offset];
                System.arraycopy( zeroBuffer, 0, blockBuffer, offset, zeroBuffer.length );
                writeBlock();
            }
        }

        /**
         * Close the TarBuffer. If this is an output buffer, also flush the current block before closing.
         *
         * @throws IOException on failure.
         */
        public void close()
            throws IOException
        {
            if ( outputStream != null )
            {
                flushBlock();
                if ( ( outputStream != System.out ) && ( outputStream != System.err ) )
                {
                    outputStream.close();
                }
            }
            if ( inputStream != null && inputStream != System.in )
            {
                inputStream.close();
            }
        }
    }

    /**
     * This class represents an entry in a Tar archive. It consists of the entry's header, as well as the entry's File.
     * Entries can be instantiated in one of three ways, depending on how they are to be used.
     * <p>
     * TarEntries that are created from the header bytes read from an archive are instantiated with the TarEntry( byte[]
     * ) constructor. These entries will be used when extracting from or listing the contents of an archive. These
     * entries have their header filled in using the header bytes. They also set the File to null, since they reference
     * an archive entry not a file.
     * <p>
     * TarEntries that are created from Files that are to be written into an archive are instantiated with the TarEntry(
     * File ) constructor. These entries have their header filled in using the File's information. They also keep a
     * reference to the File for convenience when writing entries.
     * <p>
     * Finally, TarEntries can be constructed from nothing but a name. This allows the programmer to construct the entry
     * by hand, for instance when only an InputStream is available for writing to the archive, and the header
     * information is constructed from other information. In this case the header fields are set to defaults and the
     * File is set to null.
     *
     * <pre>
     *
     * Original Unix Tar Header:
     *
     * Field  Field     Field
     * Width  Name      Meaning
     * -----  --------- ---------------------------
     *   100  name      name of file
     *     8  mode      file mode
     *     8  uid       owner user ID
     *     8  gid       owner group ID
     *    12  size      length of file in bytes
     *    12  mtime     modify time of file
     *     8  chksum    checksum for header
     *     1  link      indicator for links
     *   100  linkname  name of linked file
     *
     * </pre>
     *
     * <pre>
     *
     * POSIX "ustar" Style Tar Header:
     *
     * Field  Field     Field
     * Width  Name      Meaning
     * -----  --------- ---------------------------
     *   100  name      name of file
     *     8  mode      file mode
     *     8  uid       owner user ID
     *     8  gid       owner group ID
     *    12  size      length of file in bytes
     *    12  mtime     modify time of file
     *     8  chksum    checksum for header
     *     1  typeflag  type of file
     *   100  linkname  name of linked file
     *     6  magic     USTAR indicator
     *     2  version   USTAR version
     *    32  uname     owner user name
     *    32  gname     owner group name
     *     8  devmajor  device major number
     *     8  devminor  device minor number
     *   155  prefix    prefix for file name
     *
     * struct posix_header
     *   {                     byte offset
     *   char name[100];            0
     *   char mode[8];            100
     *   char uid[8];             108
     *   char gid[8];             116
     *   char size[12];           124
     *   char mtime[12];          136
     *   char chksum[8];          148
     *   char typeflag;           156
     *   char linkname[100];      157
     *   char magic[6];           257
     *   char version[2];         263
     *   char uname[32];          265
     *   char gname[32];          297
     *   char devmajor[8];        329
     *   char devminor[8];        337
     *   char prefix[155];        345
     *   };                       500
     *
     * </pre>
     *
     * Note that while the class does recognize GNU formatted headers, it does not perform proper processing of GNU
     * archives. I hope to add the GNU support someday. Directory "size" fix contributed by: Bert Becker
     * <becker@informatik.hu-berlin.de>.
     *
     * @author Timothy Gerard Endres, <time@gjt.org>
     */

    private static final class TarEntry
    {
        /**
         * The length of the name field in a header buffer.
         */
        private static final int NAME_LENGTH = 100;

        /**
         * The offset of the name field in a header buffer.
         */
        private static final int NAME_OFFSET = 0;

        /**
         * The length of the name prefix field in a header buffer.
         */
        private static final int PREFIX_LENGTH = 155;

        /**
         * The offset of the name prefix field in a header buffer.
         */
        private static final int PREFIX_OFFSET = 345;

        /**
         * The length of the mode field in a header buffer.
         */
        private static final int MODE_LENGTH = 8;

        /**
         * The length of the user id field in a header buffer.
         */
        private static final int UID_LENGTH = 8;

        /**
         * The length of the group id field in a header buffer.
         */
        private static final int GID_LENGTH = 8;

        /**
         * The length of the checksum field in a header buffer.
         */
        private static final int CHECK_SUM_LENGTH = 8;

        /**
         * The length of the size field in a header buffer.
         */
        private static final int SIZE_LENGTH = 12;

        /**
         * The length of the magic field in a header buffer.
         */
        private static final int MAGIC_LENGTH = 8;

        /**
         * The length of the modification time field in a header buffer.
         */
        private static final int MODIFICATION_TIME_LENGTH = 12;

        /**
         * The length of the user name field in a header buffer.
         */
        private static final int USER_NAME_LENGTH = 32;

        /**
         * The length of the group name field in a header buffer.
         */
        private static final int GROUP_NAME_LENGTH = 32;

        /**
         * The length of the devices field in a header buffer.
         */
        private static final int DEVICE_FIELD_LENGTH = 8;

        /**
         * Normal file type.
         */
        private static final byte LF_NORMAL = (byte) '0';

        /**
         * Directory file type.
         */
        private static final byte LF_DIR = (byte) '5';

        /**
         * The magic tag representing a POSIX tar archive.
         */
        private static final String MAGIC_POSIX_TAR_ARCHIVE_TAG = "ustar";

        /**
         * The entry's name.
         */
        private StringBuffer name = new StringBuffer();

        /**
         * The entry's permission mode.
         */
        private int mode;

        /**
         * The entry's user id.
         */
        private int userId;

        /**
         * The entry's group id.
         */
        private int groupId;

        /**
         * The entry's size.
         */
        private long size;

        /**
         * The entry's modification time. To convert to {@link Date}: new Date( modificationTime * 1000 ). To convert
         * from {@link Date}: date.getTime() / 1000.
         */
        private long modificationTime;

        /**
         * The entry's link flag.
         */
        private byte linkFlag;

        /**
         * The entry's link name.
         */
        private StringBuffer linkName = new StringBuffer();

        /**
         * The entry's magic tag.
         */
        private StringBuffer magicTag = new StringBuffer( TarEntry.MAGIC_POSIX_TAR_ARCHIVE_TAG );

        /**
         * The entry's user name.
         */
        private StringBuffer userName = new StringBuffer();

        /**
         * The entry's group name.
         */
        private StringBuffer groupName = new StringBuffer();

        /**
         * The entry's major device number.
         */
        private int deviceMajorNumber;

        /**
         * The entry's minor device number.
         */
        private int deviceMinorNumber;

        /**
         * Get the name of this entry.
         *
         * @return The entry's name.
         */
        public String getName()
        {
            return name.toString();
        }

        /**
         * If this entry represents a File, this references it.
         */
        private final File file;

        /**
         * Set to true if this is a "old-unix" format entry.
         */
        private boolean isUnixTarFormat;

        /**
         * Set to true if this is a 'ustar' format entry.
         */
        private boolean isUsTarFormat = true;

        /**
         * Set to true if this is a GNU 'ustar' format entry.
         */
        private boolean isGnuTarFormat;

        /**
         * Construct an entry for a file. File is set to file, and the header is constructed from information from the
         * file.
         *
         * @param externalFile The file that the entry represents.
         */
        private TarEntry( final File externalFile )
        {
            file = initializeFromFile( externalFile );
        }

        /**
         * Construct an entry from an archive's header bytes. File is set to null.
         *
         * @param buffer The header bytes from a tar archive entry.
         * @throws TarHeaderException on invalid tar archive format.
         */
        private TarEntry( final byte[] buffer )
            throws TarHeaderException
        {
            file = initializeFromBuffer( buffer );
        }

        /**
         * Sets this entry's header format to "ustar".
         */
        private void setUsTarFormat()
        {
            isUsTarFormat = true;
            isGnuTarFormat = false;
            isUnixTarFormat = false;
        }

        /**
         * Sets this entry's header format to GNU "ustar".
         */
        private void setGnuTarFormat()
        {
            isGnuTarFormat = true;
            isUsTarFormat = false;
            isUnixTarFormat = false;
        }

        /**
         * Sets this entry's header format to "unix-style".
         */
        private void setUnixTarFormat()
        {
            isUnixTarFormat = true;
            isUsTarFormat = false;
            isGnuTarFormat = false;
        }

        /**
         * Return whether or not this entry represents a directory.
         *
         * @return True if this entry is a directory.
         */
        public boolean isDirectory()
        {
            if ( file != null )
            {
                return file.isDirectory();
            }
            if ( linkFlag == TarEntry.LF_DIR )
            {
                return true;
            }
            if ( name.toString().endsWith( "/" ) )
            {
                return true;
            }
            return false;
        }

        /**
         * If this entry represents a file, and the file is a directory, return an array of TarEntries for this entry's
         * children.
         *
         * @return An array of TarEntry's for this entry's children.
         */
        public TarEntry[] getDirectoryEntries()
        {
            if ( ( file == null ) || !file.isDirectory() )
            {
                return new TarEntry[0];
            }
            final String[] basenames = file.list();
            final TarEntry[] result = new TarEntry[basenames.length];
            int index = 0;
            for ( final String basename : basenames )
            {
                result[index++] = new TarEntry( new File( file, basename ) );
            }
            return result;
        }

        /**
         * Compute the checksum of a tar entry header.
         *
         * @param buffer The tar entry's header buffer.
         * @return The computed checksum.
         */
        private long computeCheckSum( final byte[] buffer )
        {
            long sum = 0;
            for ( int index = 0; index < buffer.length; ++index )
            {
                sum += ( 0x00FF & buffer[index] );
            }
            return sum;
        }

        /**
         * Write an entry's header information to a header buffer. This method can throw an {@link TarHeaderException}.
         *
         * @param buffer The tar entry header buffer to fill in.
         * @throws TarHeaderException If the name will not fit in the header.
         */
        private void writeEntryHeader( final byte[] buffer )
            throws TarHeaderException
        {
            if ( isUnixTarFormat && name.length() > 100 )
            {
                throw new TarHeaderException( "file path is greater than 100 characters:", name );
            }

            int offset = TarEntry.getFileNameBytes( name.toString(), buffer );
            offset = TarEntry.getOctalBytes( mode, buffer, offset, TarEntry.MODE_LENGTH );
            offset = TarEntry.getOctalBytes( userId, buffer, offset, TarEntry.UID_LENGTH );
            offset = TarEntry.getOctalBytes( groupId, buffer, offset, TarEntry.GID_LENGTH );

            offset = TarEntry.getLongOctalBytes( size, buffer, offset, TarEntry.SIZE_LENGTH );
            offset = TarEntry.getLongOctalBytes( modificationTime, buffer, offset, TarEntry.MODIFICATION_TIME_LENGTH );

            final int checkSumOffset = offset;
            for ( int checkSumIndex = 0; checkSumIndex < TarEntry.CHECK_SUM_LENGTH; ++checkSumIndex )
            {
                buffer[offset++] = (byte) ' ';
            }

            buffer[offset++] = linkFlag;

            offset = TarEntry.getNameBytes( linkName, buffer, offset, TarEntry.NAME_LENGTH );

            if ( isUnixTarFormat )
            {
                for ( int index = 0; index < TarEntry.MAGIC_LENGTH; ++index, ++offset )
                {
                    buffer[offset] = 0;
                }
            }
            else
            {
                offset = TarEntry.getNameBytes( magicTag, buffer, offset, TarEntry.MAGIC_LENGTH );
            }

            offset = TarEntry.getNameBytes( userName, buffer, offset, TarEntry.USER_NAME_LENGTH );

            offset = TarEntry.getNameBytes( groupName, buffer, offset, TarEntry.GROUP_NAME_LENGTH );

            offset = TarEntry.getOctalBytes( deviceMajorNumber, buffer, offset, TarEntry.DEVICE_FIELD_LENGTH );

            offset = TarEntry.getOctalBytes( deviceMinorNumber, buffer, offset, TarEntry.DEVICE_FIELD_LENGTH );

            for ( ; offset < buffer.length; ++offset )
            {
                buffer[offset] = 0;
            }

            final long checkSumValue = computeCheckSum( buffer );
            TarEntry.getCheckSumOctalBytes( checkSumValue, buffer, checkSumOffset, TarEntry.CHECK_SUM_LENGTH );
        }

        /**
         * Fill in with information from a File.
         *
         * @param externalFile The file from which to get the header information.
         */
        private File initializeFromFile( final File externalFile )
        {

            String entryName = externalFile.getPath().replace( File.separatorChar, '/' );
            // No absolute pathnames
            while ( entryName.startsWith( "/" ) )
            {
                entryName = entryName.substring( 1 );
            }

            name = new StringBuffer( entryName );

            if ( externalFile.isDirectory() )
            {
                mode = 040755;
                if ( name.charAt( name.length() - 1 ) != '/' )
                {
                    name.append( '/' );
                }
                linkFlag = TarEntry.LF_DIR;
            }
            else
            {
                size = externalFile.length();
                mode = 0100644;
                linkFlag = TarEntry.LF_NORMAL;
            }

            // UNDONE When File lets us get the userName, use it!
            modificationTime = externalFile.lastModified() / 1000;

            return externalFile;
        }

        /**
         * Parse an entry's TarHeader information from a header buffer. Old unix-style code contributed by David
         * Mehringer <dmehring@astro.uiuc.edu>.
         *
         * @param buffer The tar entry header buffer to get information from.
         */
        private File initializeFromBuffer( final byte[] buffer )
            throws TarHeaderException
        {

            // NOTE Recognize archive header format.

            if ( ( buffer[257] == 0 ) && //
                ( buffer[258] == 0 ) && //
                ( buffer[259] == 0 ) && //
                ( buffer[260] == 0 ) && //
                ( buffer[261] == 0 ) )
            {
                isUnixTarFormat = true;
            }
            else if ( ( buffer[257] == 'u' ) && //
                ( buffer[258] == 's' ) && //
                ( buffer[259] == 't' ) && //
                ( buffer[260] == 'a' ) && //
                ( buffer[261] == 'r' ) && //
                ( buffer[262] == 0 ) )
            {
                isUsTarFormat = true;
            }
            else if ( ( buffer[257] == 'u' ) && //
                ( buffer[258] == 's' ) && //
                ( buffer[259] == 't' ) && //
                ( buffer[260] == 'a' ) && //
                ( buffer[261] == 'r' ) && //
                ( buffer[262] != 0 ) && ( buffer[263] != 0 ) )
            {
                isGnuTarFormat = true;
            }
            else
            {
                throw new TarHeaderException( "header magic is not 'ustar' nor unix-style zeros:", //
                                              "it is (decimal)", //
                                              Integer.toString( buffer[257] ), //
                                              Integer.toString( buffer[258] ), //
                                              Integer.toString( buffer[259] ), //
                                              Integer.toString( buffer[260] ), //
                                              Integer.toString( buffer[261] ), //
                                              Integer.toString( buffer[262] ), //
                                              Integer.toString( buffer[263] ) );
            }

            name = TarEntry.parseFileName( buffer );
            int offset = TarEntry.NAME_LENGTH;

            mode = (int) TarEntry.parseOctal( buffer, offset, TarEntry.MODE_LENGTH );
            offset += TarEntry.MODE_LENGTH;

            userId = (int) TarEntry.parseOctal( buffer, offset, TarEntry.UID_LENGTH );
            offset += TarEntry.UID_LENGTH;

            groupId = (int) TarEntry.parseOctal( buffer, offset, TarEntry.GID_LENGTH );
            offset += TarEntry.GID_LENGTH;

            size = TarEntry.parseOctal( buffer, offset, TarEntry.SIZE_LENGTH );
            offset += TarEntry.SIZE_LENGTH;

            modificationTime = TarEntry.parseOctal( buffer, offset, TarEntry.MODIFICATION_TIME_LENGTH );
            offset += TarEntry.MODIFICATION_TIME_LENGTH;

            // checkSum = (int) TarEntry.parseOctal( buffer, offset, TarEntry.CHECK_SUM_LENGTH );
            offset += TarEntry.CHECK_SUM_LENGTH;

            linkFlag = buffer[offset++];

            linkName = TarEntry.parseName( buffer, offset, TarEntry.NAME_LENGTH );
            offset += TarEntry.NAME_LENGTH;

            if ( isUsTarFormat )
            {
                magicTag = TarEntry.parseName( buffer, offset, TarEntry.MAGIC_LENGTH );
                offset += TarEntry.MAGIC_LENGTH;

                userName = TarEntry.parseName( buffer, offset, TarEntry.USER_NAME_LENGTH );
                offset += TarEntry.USER_NAME_LENGTH;

                groupName = TarEntry.parseName( buffer, offset, TarEntry.GROUP_NAME_LENGTH );
                offset += TarEntry.GROUP_NAME_LENGTH;

                deviceMajorNumber = (int) TarEntry.parseOctal( buffer, offset, TarEntry.DEVICE_FIELD_LENGTH );
                offset += TarEntry.DEVICE_FIELD_LENGTH;

                deviceMinorNumber = (int) TarEntry.parseOctal( buffer, offset, TarEntry.DEVICE_FIELD_LENGTH );
            }

            return null;
        }

        /**
         * Parse an octal string from a header buffer. This is used for the file permission mode value.
         *
         * @param header The header buffer from which to parse.
         * @param offset The offset into the buffer from which to parse.
         * @param length The number of header bytes to parse.
         * @return The long value of the octal string.
         */
        private static long parseOctal( final byte[] header, final int offset, final int length )
        {
            long result = 0;
            boolean stillPadding = true;
            final int end = offset + length;
            for ( int index = offset; index < end; ++index )
            {
                if ( header[index] == 0 )
                {
                    break;
                }
                if ( ( header[index] == (byte) ' ' ) || ( header[index] == '0' ) )
                {
                    if ( stillPadding )
                    {
                        continue;
                    }
                    if ( header[index] == (byte) ' ' )
                    {
                        break;
                    }
                }
                stillPadding = false;
                result = ( result << 3 ) + ( header[index] - '0' );
            }
            return result;
        }

        /**
         * Parse a file name from a header buffer. This is different from parseName() in that is recognizes 'ustar'
         * names and will handle adding on the "prefix" field to the name. Contributed by Dmitri Tikhonov
         * <dxt2431@yahoo.com>.
         *
         * @param header The header buffer from which to parse.
         * @param offset The offset into the buffer from which to parse.
         * @param length The number of header bytes to parse.
         * @return The header's entry name.
         */
        private static StringBuffer parseFileName( final byte[] header )
        {
            final StringBuffer result = new StringBuffer( 256 );
            // If header[345] is not equal to zero, then it is the "prefix"
            // that 'ustar' defines. It must be prepended to the "normal"
            // name field. We are responsible for the separating '/'.
            if ( header[345] != 0 )
            {
                for ( int index = 345; ( index < 500 ) && ( header[index] != 0 ); ++index )
                {
                    result.append( (char) header[index] );
                }
                result.append( '/' );
            }
            for ( int index = 0; ( index < 100 ) && ( header[index] != 0 ); ++index )
            {
                result.append( (char) header[index] );
            }
            return result;
        }

        /**
         * Parse an entry name from a header buffer.
         *
         * @param header The header buffer from which to parse.
         * @param offset The offset into the buffer from which to parse.
         * @param length The number of header bytes to parse.
         * @return The header's entry name.
         */
        private static StringBuffer parseName( final byte[] header, final int offset, final int length )
        {
            final StringBuffer result = new StringBuffer( length );
            final int end = offset + length;
            for ( int index = offset; index < end; ++index )
            {
                if ( header[index] == 0 )
                {
                    break;
                }
                result.append( (char) header[index] );
            }
            return result;
        }

        /**
         * This method, like getNameBytes(), is intended to place a name into a TarHeader's buffer. However, this method
         * is sophisticated enough to recognize long names (name.length() > NAMELEN). In these cases, the method will
         * break the name into a prefix and suffix and place the name in the header in 'ustar' format. It is up to the
         * TarEntry to manage the "entry header format". This method assumes the name is valid for the type of archive
         * being generated.
         *
         * @param buffer The buffer containing the entry header to modify.
         * @param newName The new name to place into the header buffer.
         * @return The current offset in the tar header (always NAMELEN).
         * @throws TarHeaderException If the name will not fit in the header.
         */
        private static int getFileNameBytes( final String newName, final byte[] buffer )
            throws TarHeaderException
        {

            if ( newName.length() <= 100 )
            {
                TarEntry.getNameBytes( new StringBuffer( newName ), buffer, TarEntry.NAME_OFFSET, TarEntry.NAME_LENGTH );
                return TarEntry.NAME_LENGTH;
            }

            // Locate a pathname "break" prior to the maximum name length...
            final int index = newName.indexOf( "/", newName.length() - 100 );
            if ( index == -1 )
            {
                throw new TarHeaderException( "file name is greater than 100 characters:", newName );
            }

            // Get the "suffix subpath" of the name.
            final String name = newName.substring( index + 1 );

            // Get the "prefix subpath", or "prefix", of the name.
            final String prefix = newName.substring( 0, index );
            if ( prefix.length() > TarEntry.PREFIX_LENGTH )
            {
                throw new TarHeaderException( "file prefix is greater than 155 characters:", prefix );
            }

            TarEntry.getNameBytes( new StringBuffer( name ), buffer, TarEntry.NAME_OFFSET, TarEntry.NAME_LENGTH );
            TarEntry.getNameBytes( new StringBuffer( prefix ), buffer, TarEntry.PREFIX_OFFSET, TarEntry.PREFIX_LENGTH );

            // The offset, regardless of the format, is now the end of the
            // original name field.
            return TarEntry.NAME_LENGTH;
        }

        /**
         * Move the bytes from the name StringBuffer into the header's buffer.
         *
         * @param header The header buffer into which to copy the name.
         * @param offset The offset into the buffer at which to store.
         * @param count The number of header bytes to store.
         * @return The new offset (offset + length).
         */
        private static int getNameBytes( final StringBuffer name, final byte[] buffer, final int offset, final int count )
        {
            int index = 0;
            for ( ; ( index < count ) && ( index < name.length() ); ++index )
            {
                buffer[offset + index] = (byte) name.charAt( index );
            }
            for ( ; index < count; ++index )
            {
                buffer[offset + index] = 0;
            }
            return offset + count;
        }

        /**
         * Parse an octal integer from a header buffer.
         *
         * @param header The header buffer from which to parse.
         * @param offset The offset into the buffer from which to parse.
         * @param count The number of header bytes to parse.
         * @return The integer value of the octal bytes.
         */
        private static int getOctalBytes( final long value, final byte[] buffer, final int offset, final int count )
        {

            int index = count - 1;
            buffer[offset + index--] = 0;
            buffer[offset + index--] = (byte) ' ';

            if ( value == 0 )
            {
                buffer[offset + index--] = (byte) '0';
            }
            else
            {
                for ( long val = value; ( index >= 0 ) && ( val > 0 ); --index )
                {
                    buffer[offset + index] = (byte) ( (byte) '0' + (byte) ( val & 7 ) );
                    val = val >> 3;
                }
            }
            for ( ; index >= 0; --index )
            {
                buffer[offset + index] = (byte) ' ';
            }
            return offset + count;
        }

        /**
         * Parse an octal long integer from a header buffer.
         *
         * @param header The header buffer from which to parse.
         * @param offset The offset into the buffer from which to parse.
         * @param count The number of header bytes to parse.
         * @return The long value of the octal bytes.
         */
        private static int getLongOctalBytes( final long value, final byte[] buffer, final int offset, final int count )
        {
            final byte[] temp = new byte[count + 1];
            TarEntry.getOctalBytes( value, temp, 0, count + 1 );
            System.arraycopy( temp, 0, buffer, offset, count );
            return offset + count;
        }

        /**
         * Parse the checksum octal integer from a header buffer.
         *
         * @param header The header buffer from which to parse.
         * @param offset The offset into the buffer from which to parse.
         * @param count The number of header bytes to parse.
         * @return The integer value of the entry's checksum.
         */
        private static int getCheckSumOctalBytes( final long value, final byte[] buf, final int offset, final int count )
        {
            TarEntry.getOctalBytes( value, buf, offset, count );
            buf[offset + count - 1] = (byte) ' ';
            buf[offset + count - 2] = 0;
            return offset + count;
        }
    }
}
