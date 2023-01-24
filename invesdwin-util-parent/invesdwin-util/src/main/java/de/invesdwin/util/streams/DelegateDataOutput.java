package de.invesdwin.util.streams;

import java.io.DataOutput;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateDataOutput implements DataOutput {

    private final DataOutput delegate;

    public DelegateDataOutput(final DataOutput delegate) {
        this.delegate = delegate;
    }

    public DataOutput getDelegate() {
        return delegate;
    }

    @Override
    public void write(final int b) throws IOException {
        getDelegate().write(b);
    }

    @Override
    public void write(final byte[] b) throws IOException {
        getDelegate().write(b);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        getDelegate().write(b, off, len);
    }

    @Override
    public void writeBoolean(final boolean v) throws IOException {
        getDelegate().writeBoolean(v);
    }

    @Override
    public void writeByte(final int v) throws IOException {
        getDelegate().writeByte(v);
    }

    @Override
    public void writeShort(final int v) throws IOException {
        getDelegate().writeShort(v);
    }

    @Override
    public void writeChar(final int v) throws IOException {
        getDelegate().writeChar(v);
    }

    @Override
    public void writeInt(final int v) throws IOException {
        getDelegate().writeInt(v);
    }

    @Override
    public void writeLong(final long v) throws IOException {
        getDelegate().writeLong(v);
    }

    @Override
    public void writeFloat(final float v) throws IOException {
        getDelegate().writeFloat(v);
    }

    @Override
    public void writeDouble(final double v) throws IOException {
        getDelegate().writeDouble(v);
    }

    @Override
    public void writeBytes(final String s) throws IOException {
        getDelegate().writeBytes(s);
    }

    @Override
    public void writeChars(final String s) throws IOException {
        getDelegate().writeChars(s);
    }

    @Override
    public void writeUTF(final String s) throws IOException {
        getDelegate().writeUTF(s);
    }

}
