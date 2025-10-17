package de.invesdwin.util.streams.pool;

import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.delegate.NonClosingDelegateInputStream;

@NotThreadSafe
public abstract class APooledInputStream extends InputStream {

    private InputStream nonClosing;

    public InputStream asNonClosing() {
        if (nonClosing == null) {
            nonClosing = new NonClosingDelegateInputStream(this);
        }
        return nonClosing;
    }

}
