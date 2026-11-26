package de.invesdwin.util.collections.array.large.queue.circular;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.IOUtils;

import de.invesdwin.util.collections.array.large.queue.IByteLargeQueue;
import de.invesdwin.util.collections.array.primitive.queue.circular.CircularByteBuffer;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.Longs;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;
import de.invesdwin.util.streams.buffer.memory.MemoryBuffers;

/**
 * Large variant of {@link CircularByteBuffer} using long based offsets and sizes, thus not being limited to an
 * {@link Integer#MAX_VALUE} sized buffer.
 * 
 * Adapted from: org.apache.commons.io.input.buffer.CircularByteBuffer
 */
@NotThreadSafe
public class CircularMemoryBuffer implements IByteLargeQueue {
    private final IMemoryBuffer buffer;
    private final long capacity;
    private long startOffset = 0;
    private long endOffset = 0;
    private long size = 0;

    public CircularMemoryBuffer() {
        this(IOUtils.DEFAULT_BUFFER_SIZE);
    }

    public CircularMemoryBuffer(final long capacity) {
        this(MemoryBuffers.allocate(capacity));
    }

    public CircularMemoryBuffer(final IMemoryBuffer buffer) {
        this.buffer = buffer;
        this.capacity = buffer.capacity();
    }

    /**
     * @return the head of this queue, or {@link IByteLargeQueue#MISSING_VALUE} if this queue is empty (use
     *         {@link #isEmpty()} to distinguish a missing value from a zero element).
     */
    @Override
    public byte peek() {
        if (isEmpty()) {
            return MISSING_VALUE;
        }
        return buffer.getByte(startOffset);
    }

    public void peek(final byte[] targetBuffer, final int targetOffset, final int length) {
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + " in the buffer, not " + length);
        }
        long offset = targetOffset;
        long bufferOffset = startOffset;
        for (int i = 0; i < length; i++) {
            targetBuffer[Integers.checkedCast(offset++)] = buffer.getByte(bufferOffset);
            if (++bufferOffset == capacity) {
                bufferOffset = 0;
            }
        }
    }

    public void peek(final java.nio.ByteBuffer targetBuffer, final int targetOffset, final int length) {
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + " in the buffer, not " + length);
        }
        long offset = targetOffset;
        long bufferOffset = startOffset;
        for (int i = 0; i < length; i++) {
            targetBuffer.put(Integers.checkedCast(offset++), buffer.getByte(bufferOffset));
            if (++bufferOffset == capacity) {
                bufferOffset = 0;
            }
        }
    }

    public void peek(final IMemoryBuffer targetBuffer, final long targetOffset, final long length) {
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + " in the buffer, not " + length);
        }
        if (targetBuffer.isExpandable()) {
            targetBuffer.ensureCapacity(targetOffset + length);
        }
        long offset = targetOffset;
        long bufferOffset = startOffset;
        for (long i = 0; i < length; i++) {
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

    /**
     * Retrieves and removes the head of this queue, or returns {@link IByteLargeQueue#MISSING_VALUE} if this queue is
     * empty (use {@link #isEmpty()} to distinguish a missing value from a zero element). {@link #read()} throws an
     * exception in that case.
     */
    @Override
    public byte poll() {
        if (isEmpty()) {
            return MISSING_VALUE;
        }
        return read();
    }

    public void read(final byte[] targetBuffer, final int targetOffset, final int length) {
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + " in the buffer, not " + length);
        }
        long offset = targetOffset;
        for (int i = 0; i < length; i++) {
            targetBuffer[Integers.checkedCast(offset++)] = buffer.getByte(startOffset);
            --size;
            if (++startOffset == capacity) {
                startOffset = 0;
            }
        }
    }

    public void read(final java.nio.ByteBuffer targetBuffer, final int targetOffset, final int length) {
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + " in the buffer, not " + length);
        }
        long offset = targetOffset;
        for (int i = 0; i < length; i++) {
            targetBuffer.put(Integers.checkedCast(offset++), buffer.getByte(startOffset));
            --size;
            if (++startOffset == capacity) {
                startOffset = 0;
            }
        }
    }

    public void read(final IMemoryBuffer targetBuffer, final long targetOffset, final long length) {
        if (size < length) {
            throw new IllegalStateException("Currently, there are only " + size + " in the buffer, not " + length);
        }
        if (targetBuffer.isExpandable()) {
            targetBuffer.ensureCapacity(targetOffset + length);
        }
        long offset = targetOffset;
        for (long i = 0; i < length; i++) {
            targetBuffer.putByte(offset++, buffer.getByte(startOffset));
            --size;
            if (++startOffset == capacity) {
                startOffset = 0;
            }
        }
    }

    public void write(final byte value) {
        if (isFull()) {
            throw new IllegalStateException("No space available");
        }
        buffer.putByte(endOffset, value);
        ++size;
        if (++endOffset == capacity) {
            endOffset = 0;
        }
    }

    /**
     * Inserts the specified byte into this queue, or returns {@code false} if this queue is full ({@link #write(byte)}
     * throws an exception in that case).
     */
    @Override
    public boolean offer(final byte e) {
        if (isFull()) {
            return false;
        }
        write(e);
        return true;
    }

    public boolean isFull() {
        return size >= capacity;
    }

    public void write(final byte[] sourceBuffer, final int sourceOffset, final int length) {
        if (size + length > capacity) {
            throw new IllegalStateException("No space available");
        }
        long curEndOffset = endOffset;
        for (int i = 0; i < length; i++) {
            buffer.putByte(curEndOffset, sourceBuffer[sourceOffset + i]);
            if (++curEndOffset == capacity) {
                curEndOffset = 0;
            }
        }
        endOffset = curEndOffset;
        size += length;
    }

    public void write(final java.nio.ByteBuffer sourceBuffer, final int sourceOffset, final int length) {
        if (size + length > capacity) {
            throw new IllegalStateException("No space available");
        }
        long curEndOffset = endOffset;
        for (int i = 0; i < length; i++) {
            buffer.putByte(curEndOffset, sourceBuffer.get(sourceOffset + i));
            if (++curEndOffset == capacity) {
                curEndOffset = 0;
            }
        }
        endOffset = curEndOffset;
        size += length;
    }

    public void write(final IMemoryBuffer sourceBuffer, final long sourceOffset, final long length) {
        if (size + length > capacity) {
            throw new IllegalStateException("No space available");
        }
        long curEndOffset = endOffset;
        for (long i = 0; i < length; i++) {
            buffer.putByte(curEndOffset, sourceBuffer.getByte(sourceOffset + i));
            if (++curEndOffset == capacity) {
                curEndOffset = 0;
            }
        }
        endOffset = curEndOffset;
        size += length;
    }

    /**
     * Writes all available data (from circular buffer and source buffer) into the target buffer, except the last data
     * that fits into circular buffer.
     */
    public int writeThrough(final byte[] sourceBuffer, final int sourceOffset, final byte[] targetBuffer,
            final int targetOffset, final int length) {
        int curSourceOffset = sourceOffset;
        long curSourceLength = length;
        int curTargetOffset = targetOffset;
        long curTargetLength = length;
        if (size > 0) {
            //drain buffer into target
            final long toBeRead = Longs.min(size, curTargetLength);
            read(targetBuffer, curTargetOffset, Integers.checkedCast(toBeRead));
            curTargetOffset += Integers.checkedCast(toBeRead);
            curTargetLength -= toBeRead;
        }

        final long toBeCopied = Longs.min(curSourceLength, curTargetLength) - capacity;
        if (toBeCopied > 0) {
            System.arraycopy(sourceBuffer, curSourceOffset, targetBuffer, curTargetOffset,
                    Integers.checkedCast(toBeCopied));
            curSourceOffset += Integers.checkedCast(toBeCopied);
            curSourceLength -= toBeCopied;
            curTargetOffset += Integers.checkedCast(toBeCopied);
            curTargetLength -= toBeCopied;
        }

        //write end of source into buffer
        final long toBeWritten = Longs.min(curSourceLength, capacity());
        if (toBeWritten > 0) {
            write(sourceBuffer, curSourceOffset, Integers.checkedCast(toBeWritten));
            curSourceLength -= toBeWritten;
        }

        return Integers.checkedCast(length - curTargetLength);
    }

    /**
     * Writes all available data (from circular buffer and source buffer) into the target buffer, except the last data
     * that fits into circular buffer.
     */
    public long writeThrough(final IMemoryBuffer sourceBuffer, final long sourceOffset,
            final IMemoryBuffer targetBuffer, final long targetOffset, final long length) {
        long curSourceOffset = sourceOffset;
        long curSourceLength = length;
        long curTargetOffset = targetOffset;
        long curTargetLength = length;
        if (size > 0) {
            //drain buffer into target
            final long toBeRead = Longs.min(size, curTargetLength);
            read(targetBuffer, curTargetOffset, toBeRead);
            curTargetOffset += toBeRead;
            curTargetLength -= toBeRead;
        }

        final long toBeCopied = Longs.min(curSourceLength, curTargetLength) - capacity;
        if (toBeCopied > 0) {
            copyTo(sourceBuffer, curSourceOffset, targetBuffer, curTargetOffset, toBeCopied);
            curSourceOffset += toBeCopied;
            curSourceLength -= toBeCopied;
            curTargetOffset += toBeCopied;
            curTargetLength -= toBeCopied;
        }

        //write end of source into buffer
        final long toBeWritten = Longs.min(curSourceLength, capacity());
        if (toBeWritten > 0) {
            write(sourceBuffer, curSourceOffset, toBeWritten);
            curSourceLength -= toBeWritten;
        }

        return length - curTargetLength;
    }

    private void copyTo(final IMemoryBuffer sourceBuffer, final long sourceOffset, final IMemoryBuffer targetBuffer,
            final long targetOffset, final long length) {
        if (targetBuffer.isExpandable()) {
            targetBuffer.ensureCapacity(targetOffset + length);
        }
        for (long i = 0; i < length; i++) {
            targetBuffer.putByte(targetOffset + i, sourceBuffer.getByte(sourceOffset + i));
        }
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public long size() {
        return size;
    }

    public long capacity() {
        return capacity;
    }

    @Override
    public void clear() {
        startOffset = 0;
        endOffset = 0;
        size = 0;
    }

    public IMemoryBuffer asMemoryBufferCopy() {
        final IMemoryBuffer copy = MemoryBuffers.allocate(size);
        peek(copy, 0, size);
        return copy;
    }
}
