package de.invesdwin.util.streams.pool.buffered;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.io.MeasurableOutputStream;
import it.unimi.dsi.fastutil.io.MeasurableStream;
import it.unimi.dsi.fastutil.io.RepositionableStream;

@NotThreadSafe
public class ReusableFastBufferedOutputStream extends MeasurableOutputStream implements RepositionableStream {

    /** The default size of the internal buffer in bytes (8Ki). */
    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;
    private static final boolean ASSERTS = false;

    /** The internal buffer. */
    protected byte[] buffer;

    /** The current position in the buffer. */
    protected int pos;

    /**
     * The number of buffer bytes available starting from {@link #pos} (it must be always equal to
     * {@code buffer.length - pos}).
     */
    protected int avail;

    /** The underlying output stream. */
    protected OutputStream os;

    /** The cached file channel underlying {@link #os}, if any. */
    private FileChannel fileChannel;

    /** {@link #os} cast to a positionable stream, if possible. */
    private RepositionableStream repositionableStream;

    /** {@link #os} cast to a measurable stream, if possible. */
    private MeasurableStream measurableStream;

    /**
     * Creates a new fast buffered output stream by wrapping a given output stream with a given buffer.
     *
     * @param os
     *            an output stream to wrap.
     * @param buffer
     *            a buffer of positive length.
     */
    public ReusableFastBufferedOutputStream(final byte[] buffer) {
        this.buffer = buffer;
        ensureBufferSize(buffer.length);
    }

    private static int ensureBufferSize(final int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Illegal buffer size: " + bufferSize);
        }
        return bufferSize;
    }

    public ReusableFastBufferedOutputStream init(final OutputStream os) {
        if (this.os != null) {
            throw new IllegalStateException("not closed");
        }
        this.os = os;
        avail = buffer.length;
        pos = 0;

        if (os instanceof RepositionableStream) {
            repositionableStream = (RepositionableStream) os;
        }
        if (os instanceof MeasurableStream) {
            measurableStream = (MeasurableStream) os;
        }

        if (repositionableStream == null) {

            try {
                fileChannel = (FileChannel) (os.getClass().getMethod("getChannel", new Class<?>[] {})).invoke(os);
            } catch (final IllegalAccessException e) {
            } catch (final IllegalArgumentException e) {
            } catch (final NoSuchMethodException e) {
            } catch (final java.lang.reflect.InvocationTargetException e) {
            } catch (final ClassCastException e) {
            }
        }
        return this;
    }

    private void dumpBuffer(final boolean ifFull) throws IOException {
        if (!ifFull || avail == 0) {
            os.write(buffer, 0, pos);
            pos = 0;
            avail = buffer.length;
        }
    }

    @Override
    public void write(final int b) throws IOException {
        if (ASSERTS) {
            assert avail > 0;
        }
        avail--;
        buffer[pos++] = (byte) b;
        dumpBuffer(true);
    }

    @Override
    public void write(final byte[] b, final int offset, final int length) throws IOException {
        if (length >= buffer.length) {
            dumpBuffer(false);
            os.write(b, offset, length);
            return;
        }

        if (length <= avail) {
            // Copy in buffer
            System.arraycopy(b, offset, buffer, pos, length);
            pos += length;
            avail -= length;
            dumpBuffer(true);
            return;
        }

        dumpBuffer(false);
        System.arraycopy(b, offset, buffer, 0, length);
        pos = length;
        avail -= length;
    }

    @Override
    public void flush() throws IOException {
        dumpBuffer(false);
        os.flush();
    }

    @Override
    public void close() throws IOException {
        if (os == null) {
            return;
        }
        flush();
        if (os != System.out) {
            os.close();
        }
        os = null;
        repositionableStream = null;
        measurableStream = null;
        fileChannel = null;
    }

    @Override
    public long position() throws IOException {
        if (repositionableStream != null) {
            return repositionableStream.position() + pos;
        } else if (measurableStream != null) {
            return measurableStream.position() + pos;
        } else if (fileChannel != null) {
            return fileChannel.position() + pos;
        } else {
            throw new UnsupportedOperationException(
                    "position() can only be called if the underlying byte stream implements the MeasurableStream or RepositionableStream interface or if the getChannel() method of the underlying byte stream exists and returns a FileChannel");
        }
    }

    /**
     * Repositions the stream.
     *
     * <p>
     * Note that this method performs a {@link #flush()} before changing the underlying stream position.
     */

    @Override
    public void position(final long newPosition) throws IOException {
        flush();
        if (repositionableStream != null) {
            repositionableStream.position(newPosition);
        } else if (fileChannel != null) {
            fileChannel.position(newPosition);
        } else {
            throw new UnsupportedOperationException(
                    "position() can only be called if the underlying byte stream implements the RepositionableStream interface or if the getChannel() method of the underlying byte stream exists and returns a FileChannel");
        }
    }

    /**
     * Returns the length of the underlying output stream, if it is {@linkplain MeasurableStream measurable}.
     *
     * <p>
     * Note that this method performs a {@link #flush()} before detecting the length.
     *
     * @return the length of the underlying output stream.
     * @throws UnsupportedOperationException
     *             if the underlying output stream is not {@linkplain MeasurableStream measurable} and cannot provide a
     *             {@link FileChannel}.
     */

    @Override
    public long length() throws IOException {
        flush();
        if (measurableStream != null) {
            return measurableStream.length();
        }
        if (fileChannel != null) {
            return fileChannel.size();
        }
        throw new UnsupportedOperationException();
    }
}
