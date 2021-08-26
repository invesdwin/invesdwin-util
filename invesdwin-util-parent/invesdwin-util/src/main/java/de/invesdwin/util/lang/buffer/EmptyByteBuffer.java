package de.invesdwin.util.lang.buffer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.annotation.concurrent.Immutable;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.apache.hc.core5.http.impl.io.EmptyInputStream;

import de.invesdwin.util.math.Bytes;
import de.invesdwin.util.streams.EmptyOutputStream;

@Immutable
public final class EmptyByteBuffer implements IByteBuffer {

    public static final EmptyByteBuffer INSTANCE = new EmptyByteBuffer();

    private EmptyByteBuffer() {
    }

    @Override
    public ByteOrder getOrder() {
        return ByteBuffers.DEFAULT_ORDER;
    }

    @Override
    public boolean isExpandable() {
        return false;
    }

    @Override
    public void putLong(final int index, final long value) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putInt(final int index, final int value) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putDouble(final int index, final double value) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putFloat(final int index, final float value) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putShort(final int index, final short value) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putChar(final int index, final char value) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putByte(final int index, final byte value) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putBytes(final int index, final byte[] src) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putBytes(final int index, final ByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        throw new IndexOutOfBoundsException("empty");
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
    public ByteBuffer byteBuffer() {
        return ByteBuffers.EMPTY_BYTE_BUFFER;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public long getLong(final int index) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public int getInt(final int index) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public double getDouble(final int index) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public float getFloat(final int index) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public short getShort(final int index) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public char getChar(final int index) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public byte getByte(final int index) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void getBytes(final int index, final ByteBuffer dstBuffer, final int dstIndex, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public int wrapAdjustment() {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        return directBuffer();
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return ByteBuffers.EMPTY_DIRECT_BUFFER;
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
    public byte[] asByteArrayCopy(final int index, final int length) {
        return byteArray();
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        return this;
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) {
        throw new IndexOutOfBoundsException("empty");
    }

}
