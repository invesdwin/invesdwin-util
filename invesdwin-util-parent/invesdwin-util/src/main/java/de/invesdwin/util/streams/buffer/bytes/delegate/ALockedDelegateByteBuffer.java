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
import de.invesdwin.util.streams.PreLockedDelegateInputStream;
import de.invesdwin.util.streams.PreLockedDelegateOutputStream;
import de.invesdwin.util.streams.buffer.bytes.ByteBuffers;
import de.invesdwin.util.streams.buffer.bytes.IByteBuffer;
import de.invesdwin.util.streams.buffer.memory.IMemoryBuffer;

@ThreadSafe
public abstract class ALockedDelegateByteBuffer extends ADelegateByteBuffer {

    private final ILock lock;

    protected ALockedDelegateByteBuffer(final ILock lock) {
        this.lock = lock;
    }

    @Override
    public boolean isReadOnly() {
        lock.lock();
        try {
            return super.isReadOnly();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ByteOrder getOrder() {
        lock.lock();
        try {
            return super.getOrder();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putChar(final int index, final char value) {
        lock.lock();
        try {
            super.putChar(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putCharReverse(final int index, final char value) {
        lock.lock();
        try {
            super.putCharReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putDouble(final int index, final double value) {
        lock.lock();
        try {
            super.putDouble(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putDoubleReverse(final int index, final double value) {
        lock.lock();
        try {
            super.putDoubleReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putFloat(final int index, final float value) {
        lock.lock();
        try {
            super.putFloat(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putFloatReverse(final int index, final float value) {
        lock.lock();
        try {
            super.putFloatReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putInt(final int index, final int value) {
        lock.lock();
        try {
            super.putInt(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putIntReverse(final int index, final int value) {
        lock.lock();
        try {
            super.putIntReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putLong(final int index, final long value) {
        lock.lock();
        try {
            super.putLong(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putLongReverse(final int index, final long value) {
        lock.lock();
        try {
            super.putLongReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putShort(final int index, final short value) {
        lock.lock();
        try {
            super.putShort(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putShortReverse(final int index, final short value) {
        lock.lock();
        try {
            super.putShortReverse(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public char getChar(final int index) {
        lock.lock();
        try {
            return super.getChar(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public char getCharReverse(final int index) {
        lock.lock();
        try {
            return super.getCharReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double getDouble(final int index) {
        lock.lock();
        try {
            return super.getDouble(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public double getDoubleReverse(final int index) {
        lock.lock();
        try {
            return super.getDoubleReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public float getFloat(final int index) {
        lock.lock();
        try {
            return super.getFloat(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public float getFloatReverse(final int index) {
        lock.lock();
        try {
            return super.getFloatReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getInt(final int index) {
        lock.lock();
        try {
            return super.getInt(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getIntReverse(final int index) {
        lock.lock();
        try {
            return super.getIntReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getLong(final int index) {
        lock.lock();
        try {
            return super.getLong(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getLongReverse(final int index) {
        lock.lock();
        try {
            return super.getLongReverse(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public short getShort(final int index) {
        lock.lock();
        try {
            return super.getShort(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public short getShortReverse(final int index) {
        lock.lock();
        try {
            return super.getShortReverse(index);
        } finally {
            lock.unlock();
        }
    }

    /////////////////// delegates ////////////////////////////

    @Override
    public void putByte(final int index, final byte value) {
        lock.lock();
        try {
            super.putByte(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte getByte(final int index) {
        lock.lock();
        try {
            return super.getByte(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long addressOffset() {
        lock.lock();
        try {
            return super.addressOffset();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        lock.lock();
        try {
            return super.directBuffer();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        lock.lock();
        try {
            return super.asDirectBuffer();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        lock.lock();
        try {
            return super.asDirectBuffer(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IMemoryBuffer asMemoryBuffer() {
        lock.lock();
        try {
            return super.asMemoryBuffer();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IMemoryBuffer asMemoryBufferFrom(final int index) {
        lock.lock();
        try {
            return super.asMemoryBufferFrom(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IMemoryBuffer asMemoryBuffer(final int index, final int length) {
        lock.lock();
        try {
            return super.asMemoryBuffer(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte[] byteArray() {
        lock.lock();
        try {
            return super.byteArray();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public java.nio.ByteBuffer nioByteBuffer() {
        lock.lock();
        try {
            return super.nioByteBuffer();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int capacity() {
        lock.lock();
        try {
            return super.capacity();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int srcIndex, final int length) {
        lock.lock();
        try {
            super.getBytes(index, dst, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        lock.lock();
        try {
            super.getBytes(index, dstBuffer, dstIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        lock.lock();
        try {
            super.getBytes(index, dstBuffer, dstIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytes(final int index, final IMemoryBuffer dstBuffer, final long dstIndex, final int length) {
        lock.lock();
        try {
            super.getBytes(index, dstBuffer, dstIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        lock.lock();
        try {
            super.getBytes(index, dstBuffer, dstIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int wrapAdjustment() {
        lock.lock();
        try {
            return super.wrapAdjustment();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isExpandable() {
        lock.lock();
        try {
            return super.isExpandable();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        lock.lock();
        try {
            super.putBytes(index, src, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        lock.lock();
        try {
            super.putBytes(srcIndex, srcBuffer, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        lock.lock();
        try {
            super.putBytes(srcIndex, srcBuffer, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        lock.lock();
        try {
            super.putBytes(index, srcBuffer, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytes(final int index, final IMemoryBuffer srcBuffer, final long srcIndex, final int length) {
        lock.lock();
        try {
            super.putBytes(index, srcBuffer, srcIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        lock.lock();
        return new PreLockedDelegateInputStream(lock, super.asInputStream(index, length));
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        lock.lock();
        return new PreLockedDelegateOutputStream(lock, super.asOutputStream(index, length));
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        lock.lock();
        try {
            return super.asByteArray(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        lock.lock();
        try {
            return super.asByteArrayCopy(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getStringAsciii(final int index, final int size) {
        lock.lock();
        try {
            return super.getStringAsciii(index, size);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        lock.lock();
        try {
            super.getStringAsciii(index, length, dst);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        lock.lock();
        try {
            super.putStringAsciii(index, value, valueIndex, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        lock.lock();
        try {
            return super.getStringUtf8(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        lock.lock();
        try {
            return super.putStringUtf8(index, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        lock.lock();
        try {
            super.getStringUtf8(index, length, dst);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        lock.lock();
        try {
            super.getBytesTo(index, dst, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        lock.lock();
        try {
            super.getBytesTo(index, dst, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        lock.lock();
        try {
            super.putBytesTo(index, src, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        lock.lock();
        try {
            super.putBytesTo(index, src, length);
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
            return super.unwrap(type);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public java.nio.ByteBuffer asNioByteBuffer(final int index, final int length) {
        lock.lock();
        try {
            return super.asNioByteBuffer(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        lock.lock();
        try {
            super.getBytesTo(index, dst, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        lock.lock();
        try {
            super.putBytesTo(index, src, length);
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
            return super.clone();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        lock.lock();
        try {
            return super.clone(index, length);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public IByteBuffer ensureCapacity(final int desiredCapacity) {
        lock.lock();
        try {
            super.ensureCapacity(desiredCapacity);
        } finally {
            lock.unlock();
        }
        return this;
    }

    @Override
    public void clear(final byte value, final int index, final int length) {
        lock.lock();
        try {
            super.clear(value, index, length);
        } finally {
            lock.unlock();
        }
    }
}
