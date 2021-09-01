package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class DisabledOutputStream extends OutputStream {

    public static final DisabledOutputStream INSTANCE = new DisabledOutputStream();

    private DisabledOutputStream() {
    }

    @Override
    public void write(final int b) throws IOException {
        //noop
    }

}
