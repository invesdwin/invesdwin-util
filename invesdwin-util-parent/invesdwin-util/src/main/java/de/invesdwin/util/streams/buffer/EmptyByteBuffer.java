package de.invesdwin.util.streams.buffer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.annotation.concurrent.Immutable;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.AtomicBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.EmptyInputStream;
import de.invesdwin.util.streams.EmptyOutputStream;

@Immutable
public final class EmptyByteBuffer implements IByteBuffer {

    public static final java.nio.ByteBuffer EMPTY_BYTE_BUFFER = java.nio.ByteBuffer.allocate(0);
    public static final java.nio.ByteBuffer EMPTY_DIRECT_BYTE_BUFFER = java.nio.ByteBuffer.allocateDirect(0);
    public static final AtomicBuffer EMPTY_AGRONA_BUFFER = new UnsafeBuffer(Bytes.EMPTY_ARRAY);
    public static final AtomicBuffer EMPTY_DIRECT_AGRONA_BUFFER = new UnsafeBuffer(EMPTY_DIRECT_BYTE_BUFFER);

    public static final EmptyByteBuffer INSTANCE = new EmptyByteBuffer();

    private EmptyByteBuffer() {
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public boolean isExpandable() {
        return false;
    }

    @Override
    public void putLong(final int index, final long value) {
        throw newEmptyException();
    }

    @Override
    public void putInt(final int index, final int value) {
        throw newEmptyException();
    }

    @Override
    public void putDouble(final int index, final double value) {
        throw newEmptyException();
    }

    @Override
    public void putFloat(final int index, final float value) {
        throw newEmptyException();
    }

    @Override
    public void putShort(final int index, final short value) {
        throw newEmptyException();
    }

    @Override
    public void putChar(final int index, final char value) {
        throw newEmptyException();
    }

    @Override
    public void putByte(final int index, final byte value) {
        throw newEmptyException();
    }

    @Override
    public void putBytes(final int index, final byte[] src) {
        throw newEmptyException();
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        throw newEmptyException();
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw newEmptyException();
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        throw newEmptyException();
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw newEmptyException();
    }

    @Override
    public long addressOffset() {
        return 0;
    }

    @Override
    public byte[] byteArray() {
        return Bytes.EMPTY_ARRAY;
    }

    @Override
    public java.nio.ByteBuffer byteBuffer() {
        return EMPTY_BYTE_BUFFER;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public long getLong(final int index) {
        throw newEmptyException();
    }

    @Override
    public int getInt(final int index) {
        throw newEmptyException();
    }

    @Override
    public double getDouble(final int index) {
        throw newEmptyException();
    }

    @Override
    public float getFloat(final int index) {
        throw newEmptyException();
    }

    @Override
    public short getShort(final int index) {
        throw newEmptyException();
    }

    @Override
    public char getChar(final int index) {
        throw newEmptyException();
    }

    @Override
    public byte getByte(final int index) {
        throw newEmptyException();
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        throw newEmptyException();
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        throw newEmptyException();
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        throw newEmptyException();
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        throw newEmptyException();
    }

    @Override
    public int wrapAdjustment() {
        return 0;
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return directBuffer();
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        return directBuffer();
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return EMPTY_AGRONA_BUFFER;
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return EmptyInputStream.INSTANCE;
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return EmptyOutputStream.INSTANCE;
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        return byteArray();
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        return byteArray();
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        return newSliceFrom(index);
    }

    @Override
    public IByteBuffer newSliceFrom(final int index) {
        return newSlice(index, remaining(index));
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        return newSlice(index, length);
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        return this;
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        throw newEmptyException();
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        throw newEmptyException();
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        throw newEmptyException();
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        throw newEmptyException();
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        throw newEmptyException();
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        throw newEmptyException();
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) {
        throw newEmptyException();
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) {
        throw newEmptyException();
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) {
        throw newEmptyException();
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) {
        throw newEmptyException();
    }

    private IndexOutOfBoundsException newEmptyException() {
        return new IndexOutOfBoundsException("empty");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        return null;
    }

    @Override
    public java.nio.ByteBuffer asByteBuffer(final int index, final int length) {
        throw newEmptyException();
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        throw newEmptyException();
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        throw newEmptyException();
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        return this;
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        return this;
    }

}
