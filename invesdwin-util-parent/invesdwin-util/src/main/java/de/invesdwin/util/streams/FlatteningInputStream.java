package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class FlatteningInputStream extends InputStream {

    private final InputStream[] ins;
    private int insIdx = 0;

    public FlatteningInputStream(final InputStream[] ins) {
        this.ins = ins;
    }

    @Override
    public int available() throws IOException {
        if (insIdx >= ins.length) {
            return 0;
        }
        return ins[insIdx].available();
    }

    @Override
    public int read() throws IOException {
        if (insIdx >= ins.length) {
            return -1;
        }
        final InputStream curIns = ins[insIdx];
        final int read = curIns.read();
        if (read == -1) {
            curIns.close();
            insIdx++;
            if (insIdx >= ins.length) {
                return -1;
            } else {
                return read();
            }
        }
        return read;
    }

    @Override
    public void close() throws IOException {
        while (insIdx < ins.length) {
            ins[insIdx].close();
            insIdx++;
        }
    }

}
