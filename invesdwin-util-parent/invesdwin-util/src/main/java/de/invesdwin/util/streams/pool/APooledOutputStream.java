package de.invesdwin.util.streams.pool;

import java.io.OutputStream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.delegate.NonClosingDelegateOutputStream;

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
