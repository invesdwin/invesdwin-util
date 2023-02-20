package de.invesdwin.util.streams.buffer.bytes.stream;

import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * An {@link InputStream} that wraps a {@link IByteBuffer}.
 */
@NotThreadSafe
public class ByteBufferInputStream extends InputStream {
    private IByteBuffer buffer;
    private int offset;
    private int length;
    private int position;

    /**
     * Default constructor.
     */
    public ByteBufferInputStream() {}

    /**
     * Wrap given {@link IByteBuffer}.
     *
     * @param buffer
     *            to wrap.
     */
    public ByteBufferInputStream(final IByteBuffer buffer) {
        wrap(buffer, 0, buffer.capacity());
    }

    /**
     * Wrap given {@link IByteBuffer}.
     *
     * @param buffer
     *            to wrap.
     * @param offset
     *            into the buffer.
     * @param length
     *            in bytes.
     */
    public ByteBufferInputStream(final IByteBuffer buffer, final int offset, final int length) {
        wrap(buffer, offset, length);
    }

    /**
     * Wrap given {@link IByteBuffer}.
     *
     * @param buffer
     *            to wrap.
     */
    public void wrap(final IByteBuffer buffer) {
        wrap(buffer, 0, buffer.capacity());
    }

    /**
     * Wrap given {@link IByteBuffer}.
     *
     * @param buffer
     *            to wrap.
     * @param offset
     *            into the buffer.
     * @param length
     *            in bytes.
     */
    public void wrap(final IByteBuffer buffer, final int offset, final int length) {
        if (null == buffer) {
            throw new NullPointerException("buffer cannot be null");
        }

        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
        this.position = 0;

        if (UnsafeBuffer.SHOULD_BOUNDS_CHECK) {
            boundsCheck0(offset, length);
        }
    }

    private void boundsCheck0(final long index, final long length) {
        final long resultingPosition = index + length;
        if (index < 0 || length < 0 || resultingPosition > buffer.capacity()) {
            throw new IndexOutOfBoundsException(
                    "index=" + index + " length=" + length + " capacity=" + buffer.capacity());
        }
    }

    /**
     * The offset within the underlying buffer at which to start.
     *
     * @return offset within the underlying buffer at which to start.
     */
    public int offset() {
        return offset;
    }

    /**
     * The length of the underlying buffer to use
     *
     * @return length of the underlying buffer to use
     */
    public int length() {
        return length;
    }

    /**
     * The underlying buffer being wrapped.
     *
     * @return the underlying buffer being wrapped.
     */
    public IByteBuffer buffer() {
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
        return length - position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long skip(final long n) {
        final int skipped = (int) Longs.min(n, available());
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
    public void close() {}
}
