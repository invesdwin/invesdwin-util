package de.invesdwin.util.streams.pool;

import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class APooledOutputStream extends OutputStream {

    private NonClosingDelegateOutputStream nonClosing;

    public OutputStream asNonClosing() {
        if (nonClosing == null) {
            nonClosing = new NonClosingDelegateOutputStream(this);
        }
        return nonClosing;
    }

}
