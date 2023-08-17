package de.invesdwin.util.streams.buffer.bytes.delegate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.annotation.concurrent.ThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.concurrent.lock.ILock;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@ThreadSafe
public abstract class ALockedDelegateByteBuffer implements IByteBuffer {

    private final ILock lock;

    protected ALockedDelegateByteBuffer(final ILock lock) {
        this.lock = lock;
    }

    @Override
    public boolean isReadOnly() {
        lock.lock();
        try {
            return getDelegate().isReadOnly();
        } finally {
            lock.unlock();
        }
    }

    protected abstract IByteBuffer getDelegate();

    @Override
    public ByteOrder getOrder() {
        lock.lock();
        try {
            return getDelegate().getOrder();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putChar(final int index, final char value) {
        lock.lock();
        try {
            getDelegate().putCharReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        lock.lock();
        try {
            getDelegate().putChar(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putDouble(final int index, final double value) {
        lock.lock();
        try {
            getDelegate().putDoubleReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        lock.lock();
        try {
            getDelegate().putDouble(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putFloat(final int index, final float value) {
        lock.lock();
        try {
            getDelegate().putFloatReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        lock.lock();
        try {
            getDelegate().putFloat(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putInt(final int index, final int value) {
        lock.lock();
        try {
            getDelegate().putIntReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        lock.lock();
        try {
            getDelegate().putInt(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putLong(final int index, final long value) {
        lock.lock();
        try {
            getDelegate().putLongReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        lock.lock();
        try {
            getDelegate().putLong(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putShort(final int index, final short value) {
        lock.lock();
        try {
            getDelegate().putShortReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        lock.lock();
        try {
            getDelegate().putShort(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public char getChar(final int index) {
        lock.lock();
        try {
            return getDelegate().getCharReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public char getCharReverse(final int index) {
        lock.lock();
        try {
            return getDelegate().getChar(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double getDouble(final int index) {
        lock.lock();
        try {
            return getDelegate().getDoubleReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double getDoubleReverse(final int index) {
        lock.lock();
        try {
            return getDelegate().getDouble(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public float getFloat(final int index) {
        lock.lock();
        try {
            return getDelegate().getFloatReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public float getFloatReverse(final int index) {
        lock.lock();
        try {
            return getDelegate().getFloat(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getInt(final int index) {
        lock.lock();
        try {
            return getDelegate().getIntReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getIntReverse(final int index) {
        lock.lock();
        try {
            return getDelegate().getInt(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getLong(final int index) {
        lock.lock();
        try {
            return getDelegate().getLongReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getLongReverse(final int index) {
        lock.lock();
        try {
            return getDelegate().getLong(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public short getShort(final int index) {
        lock.lock();
        try {
            return getDelegate().getShortReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public short getShortReverse(final int index) {
        lock.lock();
        try {
            return getDelegate().getShort(index);
        } finally {
            lock.unlock();
        }
    }

    /////////////////// delegates ////////////////////////////

    @Override
    public void putByte(final int index, final byte value) {
        lock.lock();
        try {
            getDelegate().putByte(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte getByte(final int index) {
        lock.lock();
        try {
            return getDelegate().getByte(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long addressOffset() {
        lock.lock();
        try {
            return getDelegate().addressOffset();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        lock.lock();
        try {
            return getDelegate().directBuffer();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        lock.lock();
        try {
            return getDelegate().asDirectBuffer();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        lock.lock();
        try {
            return getDelegate().asDirectBuffer(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        lock.lock();
        try {
            return getDelegate().asMemoryBuffer();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IMemoryBuffer asMemoryBufferFrom(final int index) {
        lock.lock();
        try {
            return getDelegate().asMemoryBufferFrom(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        lock.lock();
        try {
            return getDelegate().asMemoryBuffer(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte[] byteArray() {
        lock.lock();
        try {
            return getDelegate().byteArray();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        lock.lock();
        try {
            return getDelegate().nioByteBuffer();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int capacity() {
        lock.lock();
        try {
            return getDelegate().capacity();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int srcIndex, final int length) {
        lock.lock();
        try {
            getDelegate().getBytes(index, dst, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        lock.lock();
        try {
            getDelegate().getBytes(index, dstBuffer, dstIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        lock.lock();
        try {
            getDelegate().getBytes(index, dstBuffer, dstIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        lock.lock();
        try {
            getDelegate().getBytes(index, dstBuffer, dstIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        lock.lock();
        try {
            getDelegate().getBytes(index, dstBuffer, dstIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int wrapAdjustment() {
        lock.lock();
        try {
            return getDelegate().wrapAdjustment();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isExpandable() {
        lock.lock();
        try {
            return getDelegate().isExpandable();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        lock.lock();
        try {
            getDelegate().putBytes(index, src, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        lock.lock();
        try {
            getDelegate().putBytes(srcIndex, srcBuffer, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        lock.lock();
        try {
            getDelegate().putBytes(srcIndex, srcBuffer, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        lock.lock();
        try {
            getDelegate().putBytes(index, srcBuffer, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        lock.lock();
        try {
            getDelegate().putBytes(index, srcBuffer, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        lock.lock();
        try {
            return getDelegate().asInputStream(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        lock.lock();
        try {
            return getDelegate().asOutputStream(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        lock.lock();
        try {
            return getDelegate().asByteArray(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        lock.lock();
        try {
            return getDelegate().asByteArrayCopy(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        lock.lock();
        try {
            return getDelegate().sliceFrom(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        lock.lock();
        try {
            return getDelegate().slice(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IByteBuffer newSliceFrom(final int index) {
        if (index == 0) {
            return this;
        } else {
            lock.lock();
            try {
                return getDelegate().newSliceFrom(index);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return this;
        } else {
            lock.lock();
            try {
                return getDelegate().newSlice(index, length);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public String getStringAsciii(final int index, final int size) {
        lock.lock();
        try {
            return getDelegate().getStringAsciii(index, size);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        lock.lock();
        try {
            getDelegate().getStringAsciii(index, length, dst);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        lock.lock();
        try {
            getDelegate().putStringAsciii(index, value, valueIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        lock.lock();
        try {
            return getDelegate().getStringUtf8(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        lock.lock();
        try {
            return getDelegate().putStringUtf8(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        lock.lock();
        try {
            getDelegate().getStringUtf8(index, length, dst);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        lock.lock();
        try {
            getDelegate().getBytesTo(index, dst, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        lock.lock();
        try {
            getDelegate().getBytesTo(index, dst, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        lock.lock();
        try {
            getDelegate().putBytesTo(index, src, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        lock.lock();
        try {
            getDelegate().putBytesTo(index, src, length);
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        lock.lock();
        try {
            return getDelegate().unwrap(type);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        lock.lock();
        try {
            return getDelegate().asNioByteBuffer(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        lock.lock();
        try {
            getDelegate().getBytesTo(index, dst, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        lock.lock();
        try {
            getDelegate().putBytesTo(index, src, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return ByteBuffers.toString(this);
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        lock.lock();
        try {
            return getDelegate().clone();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        lock.lock();
        try {
            return getDelegate().clone(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IByteBuffer ensureCapacity(final int desiredCapacity) {
        lock.lock();
        try {
            getDelegate().ensureCapacity(desiredCapacity);
        } finally {
            lock.unlock();
        }
        return this;
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        lock.lock();
        try {
            getDelegate().clear(value, index, length);
        } finally {
            lock.unlock();
        }
    }
}
