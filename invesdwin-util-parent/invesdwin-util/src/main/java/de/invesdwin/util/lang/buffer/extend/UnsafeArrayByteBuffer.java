package de.invesdwin.util.lang.buffer.extend;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.buffer.ByteBuffers;

@Immutable
public class UnsafeArrayByteBuffer extends UnsafeByteBuffer {

    public UnsafeArrayByteBuffer(final byte[] array) {
        super(array);
    }

    @Override
    public byte[] asByteArray() {
        return byteArray();
    }

    @Override
    public byte[] asByteArrayCopy() {
        return byteArray().clone();
    }

    @Override
    public byte[] asByteArray(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return byteArray();
        }
        return ByteBuffers.asByteArrayCopyGet(this, index, length);
    }

    @Override
    public byte[] asByteArrayCopy(final int index, final int length) {
        if (index == 0 && length == capacity()) {
            return byteArray();
        }
        return ByteBuffers.asByteArrayCopyGet(this, index, length);
    }

    @Override
    public void getBytesTo(final int index, final DataOutput dst, final int length) throws IOException {
        dst.write(byteArray(), index, length);
    }

    @Override
    public void getBytesTo(final int index, final OutputStream dst, final int length) throws IOException {
        dst.write(byteArray(), index, length);
    }

    @Override
    public void putBytesTo(final int index, final DataInput src, final int length) throws IOException {
        src.readFully(byteArray(), index, length);
    }

    @Override
    public void putBytesTo(final int index, final InputStream src, final int length) throws IOException {
        final byte[] array = byteArray();
        final int end = index + length;
        int remaining = length;
        while (remaining > 0) {
            final int location = end - remaining;
            final int count = src.read(array, location, remaining);
            if (count == -1) { // EOF
                break;
            }
            remaining -= count;
        }
        if (remaining > 0) {
            throw ByteBuffers.newPutBytesToEOF();
        }
    }

}