package de.invesdwin.util.streams.buffer.delegate.slice.mutable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.annotation.concurrent.NotThreadSafe;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

import de.invesdwin.util.streams.buffer.ByteBuffers;
import de.invesdwin.util.streams.buffer.IByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.SlicedFromDelegateByteBuffer;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.ExpandableMutableSlicedDelegateByteBufferFactory;
import de.invesdwin.util.streams.buffer.delegate.slice.mutable.factory.IMutableSlicedDelegateByteBufferFactory;

@NotThreadSafe
public class MutableSlicedFromDelegateByteBuffer implements IByteBuffer {

    private final IByteBuffer delegate;
    private int from;
    private IMutableSlicedDelegateByteBufferFactory mutableSliceFactory;

    public MutableSlicedFromDelegateByteBuffer(final IByteBuffer delegate, final int from) {
        this.delegate = delegate;
        this.from = from;
    }

    public void setFrom(final int from) {
        this.from = from;
    }

    @Override
    public boolean isReadOnly() {
        return delegate.isReadOnly();
    }

    @Override
    public ByteOrder getOrder() {
        return delegate.getOrder();
    }

    @Override
    public long addressOffset() {
        return delegate.addressOffset() + from;
    }

    @Override
    public MutableDirectBuffer directBuffer() {
        return delegate.directBuffer();
    }

    @Override
    public byte[] byteArray() {
        return delegate.byteArray();
    }

    @Override
    public java.nio.ByteBuffer byteBuffer() {
        return delegate.byteBuffer();
    }

    @Override
    public int capacity() {
        return delegate.capacity() - from;
    }

    @Override
    public long getLong(final int index) {
        return delegate.getLong(index + from);
    }

    @Override
    public int getInt(final int index) {
        return delegate.getInt(index + from);
    }

    @Override
    public double getDouble(final int index) {
        return delegate.getDouble(index + from);
    }

    @Override
    public float getFloat(final int index) {
        return delegate.getFloat(index + from);
    }

    @Override
    public short getShort(final int index) {
        return delegate.getShort(index + from);
    }

    @Override
    public char getChar(final int index) {
        return delegate.getChar(index + from);
    }

    @Override
    public byte getByte(final int index) {
        return delegate.getByte(index + from);
    }

    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        delegate.getBytes(index + from, dst, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final MutableDirectBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index + from, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final IByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index + from, dstBuffer, dstIndex, length);
    }

    @Override
    public void getBytes(final int index, final java.nio.ByteBuffer dstBuffer, final int dstIndex, final int length) {
        delegate.getBytes(index + from, dstBuffer, dstIndex, length);
    }

    @Override
    public int wrapAdjustment() {
        return delegate.wrapAdjustment() + from;
    }

    @Override
    public boolean isExpandable() {
        return delegate.isExpandable();
    }

    @Override
    public void putLong(final int index, final long value) {
        delegate.putLong(index + from, value);
    }

    @Override
    public void putInt(final int index, final int value) {
        delegate.putInt(index + from, value);
    }

    @Override
    public void putDouble(final int index, final double value) {
        delegate.putDouble(index + from, value);
    }

    @Override
    public void putFloat(final int index, final float value) {
        delegate.putFloat(index + from, value);
    }

    @Override
    public void putShort(final int index, final short value) {
        delegate.putShort(index + from, value);
    }

    @Override
    public void putChar(final int index, final char value) {
        delegate.putChar(index + from, value);
    }

    @Override
    public void putByte(final int index, final byte value) {
        delegate.putByte(index + from, value);
    }

    @Override
    public void putBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        delegate.putBytes(index + from, src, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final java.nio.ByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index + from, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final DirectBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index + from, srcBuffer, srcIndex, length);
    }

    @Override
    public void putBytes(final int index, final IByteBuffer srcBuffer, final int srcIndex, final int length) {
        delegate.putBytes(index + from, srcBuffer, srcIndex, length);
    }

    @Override
    public InputStream asInputStream(final int index, final int length) {
        return delegate.asInputStream(index + from, length);
    }

    @Override
    public OutputStream asOutputStream(final int index, final int length) {
        return delegate.asOutputStream(index + from, length);
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        return delegate.asByteArray(index + from, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        return delegate.asByteArrayCopy(index + from, length);
    }

    @Override
    public MutableDirectBuffer asDirectBuffer() {
        return asDirectBufferTo(capacity());
    }

    @Override
    public MutableDirectBuffer asDirectBuffer(final int index, final int length) {
        return delegate.asDirectBuffer(index + from, length);
    }

    private IMutableSlicedDelegateByteBufferFactory getMutableSliceFactory() {
        if (mutableSliceFactory == null) {
            mutableSliceFactory = new ExpandableMutableSlicedDelegateByteBufferFactory(this);
        }
        return mutableSliceFactory;
    }

    @Override
    public IByteBuffer sliceFrom(final int index) {
        return getMutableSliceFactory().sliceFrom(index);
    }

    @Override
    public IByteBuffer slice(final int index, final int length) {
        return getMutableSliceFactory().slice(index, length);
    }

    @Override
    public IByteBuffer newSliceFrom(final int index) {
        return new SlicedFromDelegateByteBuffer(delegate, index + from);
    }

    @Override
    public IByteBuffer newSlice(final int index, final int length) {
        return delegate.newSlice(index + from, length);
    }

    @Override
    public String getStringAsciii(final int index, final int length) {
        return delegate.getStringAsciii(index + from, length);
    }

    @Override
    public void getStringAsciii(final int index, final int length, final Appendable dst) {
        delegate.getStringAsciii(index + from, length, dst);
    }

    @Override
    public void putStringAsciii(final int index, final CharSequence value, final int valueIndex, final int length) {
        delegate.putStringAsciii(index + from, value, valueIndex, length);
    }

    @Override
    public int putStringUtf8(final int index, final String value) {
        return delegate.putStringUtf8(index + from, value);
    }

    @Override
    public String getStringUtf8(final int index, final int length) {
        return delegate.getStringUtf8(index + from, length);
    }

    @Override
    public void getStringUtf8(final int index, final int length, final Appendable dst) {
        delegate.getStringUtf8(index + from, length, dst);
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        delegate.getBytesTo(index + from, dst, length);
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        delegate.getBytesTo(index + from, dst, length);
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        delegate.putBytesTo(index + from, src, length);
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        delegate.putBytesTo(index + from, src, length);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(final Class<T> type) {
        if (getClass().isAssignableFrom(type)) {
            return (T) this;
        }
        return delegate.unwrap(type);
    }

    @Override
    public java.nio.ByteBuffer asByteBuffer(final int index, final int length) {
        return delegate.asByteBuffer(index + from, length);
    }

    @Override
    public void getBytesTo(final int index, final WritableByteChannel dst, final int length) throws IOException {
        delegate.getBytesTo(index + from, dst, length);
    }

    @Override
    public void putBytesTo(final int index, final ReadableByteChannel src, final int length) throws IOException {
        delegate.putBytesTo(index + from, src, length);
    }

    @Override
    public String toString() {
        return ByteBuffers.toString(this);
    }

    //CHECKSTYLE:OFF
    @Override
    public IByteBuffer clone() {
        //CHECKSTYLE:ON
        return ByteBuffers.wrap(asByteArrayCopy());
    }

    @Override
    public IByteBuffer clone(final int index, final int length) {
        return ByteBuffers.wrap(asByteArrayCopy(index, length));
    }

}
