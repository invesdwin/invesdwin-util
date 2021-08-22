package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class EmptyOutputStream extends OutputStream {

    public static final EmptyOutputStream INSTANCE = new EmptyOutputStream();

    private EmptyOutputStream() {
    }

    @Override
    public void write(final int b) throws IOException {
        throw new UnsupportedOperationException("empty");
    }

}
