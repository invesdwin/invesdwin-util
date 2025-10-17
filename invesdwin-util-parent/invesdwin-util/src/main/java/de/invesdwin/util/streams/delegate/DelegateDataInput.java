package de.invesdwin.util.streams.delegate;

import java.io.DataInput;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class DelegateDataInput implements DataInput {

    private final DataInput delegate;

    public DelegateDataInput(final DataInput delegate) {
        this.delegate = delegate;
    }

    public DataInput getDelegate() {
        return delegate;
    }

    @Override
    public void readFully(final byte[] b) throws IOException {
        getDelegate().readFully(b);
    }

    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        getDelegate().readFully(b, off, len);
    }

    @Override
    public int skipBytes(final int n) throws IOException {
        return getDelegate().skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return getDelegate().readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return getDelegate().readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return getDelegate().readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return getDelegate().readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return getDelegate().readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return getDelegate().readChar();
    }

    @Override
    public int readInt() throws IOException {
        return getDelegate().readInt();
    }

    @Override
    public long readLong() throws IOException {
        return getDelegate().readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return getDelegate().readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return getDelegate().readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return getDelegate().readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return getDelegate().readUTF();
    }

}
