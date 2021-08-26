package de.invesdwin.util.lang.buffer.delegate;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.buffer.ByteBuffers;

@Immutable
public class JavaDelegateArrayByteBuffer extends JavaDelegateByteBuffer {

    public JavaDelegateArrayByteBuffer(final byte[] bytes) {
        super(bytes);
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
            return byteArray().clone();
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
        final int result = src.read(byteArray(), index, length);
        if (result == -1) {
            throw ByteBuffers.newPutBytesToEOF();
        }
    }

}
