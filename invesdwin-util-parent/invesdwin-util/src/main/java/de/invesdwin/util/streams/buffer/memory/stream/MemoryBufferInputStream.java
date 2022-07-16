package de.invesdwin.util.streams.buffer.memory.stream;

import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

/**
 * An {@link InputStream} that wraps a {@link IMemoryBuffer}.
 */
@NotThreadSafe
public class MemoryBufferInputStream extends InputStream {
    private IMemoryBuffer buffer;
    private long offset;
    private long length;
    private long position;

    /**
     * Default constructor.
     */
    public MemoryBufferInputStream() {
    }

    /**
     * Wrap given {@link IMemoryBuffer}.
     *
     * @param buffer
     *            to wrap.
     */
    public MemoryBufferInputStream(final IMemoryBuffer buffer) {
        wrap(buffer, 0, buffer.capacity());
    }

    /**
     * Wrap given {@link IMemoryBuffer}.
     *
     * @param buffer
     *            to wrap.
     * @param offset
     *            into the buffer.
     * @param length
     *            in bytes.
     */
    public MemoryBufferInputStream(final IMemoryBuffer buffer, final long offset, final long length) {
        wrap(buffer, offset, length);
    }

    /**
     * Wrap given {@link IMemoryBuffer}.
     *
     * @param buffer
     *            to wrap.
     */
    public void wrap(final IMemoryBuffer buffer) {
        wrap(buffer, 0, buffer.capacity());
    }

    /**
     * Wrap given {@link IMemoryBuffer}.
     *
     * @param buffer
     *            to wrap.
     * @param offset
     *            into the buffer.
     * @param length
     *            in bytes.
     */
    public void wrap(final IMemoryBuffer buffer, final long offset, final long length) {
        if (null == buffer) {
            throw new NullPointerException("buffer cannot be null");
        }

        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
        this.position = 0;
    }

    /**
     * The offset within the underlying buffer at which to start.
     *
     * @return offset within the underlying buffer at which to start.
     */
    public long offset() {
        return offset;
    }

    /**
     * The length of the underlying buffer to use
     *
     * @return length of the underlying buffer to use
     */
    public long length() {
        return length;
    }

    /**
     * The underlying buffer being wrapped.
     *
     * @return the underlying buffer being wrapped.
     */
    public IMemoryBuffer buffer() {
        return buffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean markSupported() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int available() {
        return Integers.checkedCastNoOverflow(length - position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(final long n) {
        final long skipped = Longs.min(n, available());
        position += skipped;

        return skipped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() {
        int b = -1;
        if (position < length) {
            b = buffer.getByte(offset + position) & 0xFF;
            ++position;
        }

        return b;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(final byte[] dstBytes, final int dstOffset, final int length) {
        int bytesRead = -1;

        if (position < this.length) {
            bytesRead = Math.min(length, available());
            buffer.getBytes(offset + position, dstBytes, dstOffset, bytesRead);
            position += bytesRead;
        }

        return bytesRead;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
    }
}
