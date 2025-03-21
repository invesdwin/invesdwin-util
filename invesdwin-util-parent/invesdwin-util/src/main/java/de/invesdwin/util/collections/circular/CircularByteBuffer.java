package de.invesdwin.util.collections.circular;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;

import de.invesdwin.util.math.Integers;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;

/**
 * Adapted from: org.apache.commons.io.input.buffer.CircularByteBuffer
 */
@NotThreadSafe
public class CircularByteBuffer {
    private final IByteBuffer buffer;
    private final int capacity;
    private int startOffset = 0;
    private int endOffset = 0;
    private int size = 0;

    public CircularByteBuffer() {
        this(IOUtils.DEFAULT_BUFFER_SIZE);
    }

    public CircularByteBuffer(final int capacity) {
        this(ByteBuffers.allocate(capacity));
    }

    public CircularByteBuffer(final IByteBuffer buffer) {
        this.buffer = buffer;
        this.capacity = buffer.capacity();
    }

    public byte peek() {
        if (size <= 0) {
            throw new IllegalStateException("No bytes available.");
        }
        final byte b = buffer.getByte(startOffset);
        if (++startOffset == capacity) {
            startOffset = 0;
        }
        return b;
    }

    public void peek(final byte[] targetBuffer, final int targetOffset, final int length) {
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + "in the buffer, not " + length);
        }
        int offset = targetOffset;
        int bufferOffset = startOffset;
        for (int i = 0; i < length; i++) {
            targetBuffer[offset++] = buffer.getByte(bufferOffset);
            if (++bufferOffset == capacity) {
                bufferOffset = 0;
            }
        }
    }

    public void peek(final java.nio.ByteBuffer targetBuffer, final int targetOffset, final int length) {
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + "in the buffer, not " + length);
        }
        int offset = targetOffset;
        int bufferOffset = startOffset;
        for (int i = 0; i < length; i++) {
            targetBuffer.put(offset++, buffer.getByte(bufferOffset));
            if (++bufferOffset == capacity) {
                bufferOffset = 0;
            }
        }
    }

    public void peek(final IByteBuffer targetBuffer, final int targetOffset, final int length) {
        if (targetBuffer.isExpandable()) {
            targetBuffer.ensureCapacity(targetOffset + length);
        }
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + "in the buffer, not " + length);
        }
        int offset = targetOffset;
        int bufferOffset = startOffset;
        for (int i = 0; i < length; i++) {
            targetBuffer.putByte(offset++, buffer.getByte(bufferOffset));
            if (++bufferOffset == capacity) {
                bufferOffset = 0;
            }
        }
    }

    public byte read() {
        if (size <= 0) {
            throw new IllegalStateException("No bytes available.");
        }
        final byte b = buffer.getByte(startOffset);
        --size;
        if (++startOffset == capacity) {
            startOffset = 0;
        }
        return b;
    }

    public void read(final byte[] targetBuffer, final int targetOffset, final int length) {
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + "in the buffer, not " + length);
        }
        int offset = targetOffset;
        for (int i = 0; i < length; i++) {
            targetBuffer[offset++] = buffer.getByte(startOffset);
            --size;
            if (++startOffset == capacity) {
                startOffset = 0;
            }
        }
    }

    public void read(final java.nio.ByteBuffer targetBuffer, final int targetOffset, final int length) {
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + "in the buffer, not " + length);
        }
        int offset = targetOffset;
        for (int i = 0; i < length; i++) {
            targetBuffer.put(offset++, buffer.getByte(startOffset));
            --size;
            if (++startOffset == capacity) {
                startOffset = 0;
            }
        }
    }

    public void read(final IByteBuffer targetBuffer, final int targetOffset, final int length) {
        if (targetBuffer.isExpandable()) {
            targetBuffer.ensureCapacity(targetOffset + length);
        }
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + "in the buffer, not " + length);
        }
        int offset = targetOffset;
        for (int i = 0; i < length; i++) {
            targetBuffer.putByte(offset++, buffer.getByte(startOffset));
            --size;
            if (++startOffset == capacity) {
                startOffset = 0;
            }
        }
    }

    public void write(final byte value) {
        if (size >= capacity) {
            throw new IllegalStateException("No space available");
        }
        buffer.putByte(endOffset, value);
        ++size;
        if (++endOffset == capacity) {
            endOffset = 0;
        }
    }

    public void write(final byte[] sourceBuffer, final int sourceOffset, final int length) {
        if (size + length > capacity) {
            throw new IllegalStateException("No space available");
        }
        for (int i = 0; i < length; i++) {
            buffer.putByte(endOffset, sourceBuffer[sourceOffset + i]);
            if (++endOffset == capacity) {
                endOffset = 0;
            }
        }
        size += length;
    }

    public void write(final java.nio.ByteBuffer sourceBuffer, final int sourceOffset, final int length) {
        if (size + length > capacity) {
            throw new IllegalStateException("No space available");
        }
        for (int i = 0; i < length; i++) {
            buffer.putByte(endOffset, sourceBuffer.get(sourceOffset + i));
            if (++endOffset == capacity) {
                endOffset = 0;
            }
        }
        size += length;
    }

    public void write(final IByteBuffer sourceBuffer, final int sourceOffset, final int length) {
        if (size + length > capacity) {
            throw new IllegalStateException("No space available");
        }
        for (int i = 0; i < length; i++) {
            buffer.putByte(endOffset, sourceBuffer.getByte(sourceOffset + i));
            if (++endOffset == capacity) {
                endOffset = 0;
            }
        }
        size += length;
    }

    /**
     * Writes all available data (from circular buffer and source buffer) into the target buffer, except the last data
     * that fits into circular buffer.
     */
    public int writeThrough(final byte[] sourceBuffer, final int sourceOffset, final byte[] targetBuffer,
            final int targetOffset, final int length) {
        int curSourceOffset = sourceOffset;
        int curSourceLength = length;
        //        int curTargetOffset = targetOffset;
        int curTargetLength = length;
        if (size > 0) {
            //drain buffer into target
            final int toBeRead = Integers.min(size, curTargetLength);
            read(targetBuffer, targetOffset, toBeRead);
            //            curTargetOffset += toBeRead;
            curTargetLength -= toBeRead;
        }

        final int toBeCopied = Integers.min(curSourceLength, curTargetLength) - capacity;
        if (toBeCopied > 0) {
            System.arraycopy(sourceBuffer, curSourceOffset, targetBuffer, curSourceOffset, toBeCopied);
            curSourceOffset += toBeCopied;
            curSourceLength -= toBeCopied;
            //            curTargetOffset += toBeCopied;
            curTargetLength -= toBeCopied;
        }

        //write end of source into buffer
        final int toBeWritten = Integers.min(curSourceLength, capacity());
        if (toBeWritten > 0) {
            write(sourceBuffer, curSourceOffset, toBeWritten);
            curSourceLength -= toBeWritten;
        }

        return length - curTargetLength;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public void clear() {
        startOffset = 0;
        endOffset = 0;
        size = 0;
    }

    public byte[] asByteArrayCopy() {
        final byte[] copy = new byte[size];
        peek(copy, 0, size);
        return copy;
    }
}