package de.invesdwin.util.lang.uri.connect;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;

import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.description.TextDescription;
import de.invesdwin.util.math.decimal.scaled.ByteSize;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import de.invesdwin.util.streams.ADelegateInputStream;
import de.invesdwin.util.streams.pool.PooledFastByteArrayOutputStream;
import de.invesdwin.util.streams.pool.buffered.DeletingBufferedFileDataInputStream;

@NotThreadSafe
public class InputStreamHttpResponseConsumer {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private static File defaultTempDir;
    private static Path defaultTempDirPath;
    private static int defaultMaxSizeInMemory = (int) ByteSizeScale.BYTES.convert(512, ByteSizeScale.KILOBYTES);

    private File tempDir = defaultTempDir;
    private Path tempDirPath = defaultTempDirPath;
    private int maxSizeInMemory = defaultMaxSizeInMemory;

    private IHttpResponse response;
    private PooledFastByteArrayOutputStream byteArrayOut;
    private FileOutputStream fileOut;
    private File file;

    public InputStreamHttpResponseConsumer() {
    }

    /**
     * Set a non null value here to enable a write cache when the size exceeds 512KB. This helps to reduce the load on
     * the memory.
     */
    public static void setDefaultTempDir(final File file) {
        defaultTempDir = file;
        defaultTempDirPath = defaultTempDir.toPath();
    }

    /**
     * Use this method to change the default threshold of 512KB.
     */
    public static void setDefaultMaxSizeInMemory(final ByteSize defaultMaxSizeInMemory) {
        InputStreamHttpResponseConsumer.defaultMaxSizeInMemory = (int) defaultMaxSizeInMemory
                .getValue(ByteSizeScale.BYTES);
    }

    /**
     * Use this method to override the write cache directory. Null disables the write cache for this call.
     */
    public InputStreamHttpResponseConsumer setTempDir(final File tempDir) {
        this.tempDir = tempDir;
        if (tempDir == null) {
            this.tempDirPath = null;
        } else {
            this.tempDirPath = tempDir.toPath();
        }
        return this;
    }

    public File getTempDir() {
        return tempDir;
    }

    /**
     * Use this method to override the default size limit for using the write cache.
     */
    public InputStreamHttpResponseConsumer setMaxSizeInMemory(final ByteSize maxSizeInMemory) {
        this.maxSizeInMemory = (int) maxSizeInMemory.getValue(ByteSizeScale.BYTES);
        return this;
    }

    public int getMaxSizeInMemory() {
        return maxSizeInMemory;
    }

    public void releaseResources() {
        //asInputStream closes the output stream later
        byteArrayOut = null;
        if (fileOut != null) {
            try {
                fileOut.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            fileOut = null;
            Files.deleteQuietly(file);
            file = null;
        }
    }

    public void start(final IHttpResponse response) {
        this.response = response;
        this.byteArrayOut = PooledFastByteArrayOutputStream.newInstance();
    }

    public InputStreamHttpResponse buildResult() {
        return new InputStreamHttpResponse(response, newDelegate());
    }

    private InputStream newDelegate() {
        if (byteArrayOut != null) {
            return byteArrayOut.asInputStream();
        } else {
            return new ADelegateInputStream(
                    new TextDescription(InputStreamHttpResponseConsumer.class.getSimpleName())) {
                @Override
                protected InputStream newDelegate() {
                    try {
                        //without buffering input stream, reading can be 5x slower
                        return new DeletingBufferedFileDataInputStream(file, maxSizeInMemory);
                    } catch (final FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }

    public void data(final java.nio.ByteBuffer src, final boolean endOfStream) throws IOException {
        if (byteArrayOut != null) {
            if (defaultTempDir != null) {
                while (src.hasRemaining()) {
                    byteArrayOut.write(src.get());
                    if (byteArrayOut.length > maxSizeInMemory) {
                        Files.forceMkdir(tempDir);
                        file = newTempFile();
                        fileOut = new FileOutputStream(file);
                        IOUtils.write(byteArrayOut.toByteArray(), fileOut);
                        byteArrayOut = null;
                        break;
                    }
                }
            } else {
                while (src.hasRemaining()) {
                    byteArrayOut.write(src.get());
                }
            }
        }
        if (fileOut != null) {
            while (src.hasRemaining()) {
                fileOut.write(src.get());
            }
            if (endOfStream) {
                fileOut.close();
                fileOut = null;
            }
        }
    }

    public void data(final InputStream src) throws FileNotFoundException, IOException {
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        if (byteArrayOut != null) {
            if (defaultTempDir != null) {
                int n;
                try {
                    while (IOUtils.EOF != (n = src.read(buffer))) {
                        byteArrayOut.write(buffer, 0, n);
                        if (byteArrayOut.length > maxSizeInMemory) {
                            Files.forceMkdir(tempDir);
                            file = newTempFile();
                            fileOut = new FileOutputStream(file);
                            IOUtils.write(byteArrayOut.toByteArray(), fileOut);
                            byteArrayOut.close();
                            byteArrayOut = null;
                            break;
                        }
                    }
                } catch (final EOFException eof) {
                    //end reached
                }
            } else {
                try {
                    while (src.available() > 0) {
                        byteArrayOut.write(src.read());
                    }
                } catch (final EOFException eof) {
                    //end reached
                }
            }
        }
        if (fileOut != null) {
            int n;
            try {
                while (IOUtils.EOF != (n = src.read(buffer))) {
                    fileOut.write(buffer, 0, n);
                }
            } catch (final EOFException eof) {
                //end reached
            }
            fileOut.close();
            fileOut = null;
        }
    }

    private File newTempFile() {
        try {
            return Files.createTempFile(tempDirPath, "download_", ".bin").toFile();
        } catch (final IOException e1) {
            if (!tempDir.exists()) {
                try {
                    Files.forceMkdir(tempDir);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    return Files.createTempFile(tempDirPath, "download_", ".bin").toFile();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException(e1);
            }
        }
    }

}
