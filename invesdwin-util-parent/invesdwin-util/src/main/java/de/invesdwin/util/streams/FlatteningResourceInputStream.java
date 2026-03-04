package de.invesdwin.util.streams;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.Resource;

import de.invesdwin.util.streams.closeable.Closeables;
import de.invesdwin.util.streams.resource.Resources;

@NotThreadSafe
public class FlatteningResourceInputStream extends InputStream {

    private static final int EOF = -1;
    private int currentIndex = -1;
    private boolean eof = false;
    private final Resource[] resources;
    private InputStream currentStream;

    public FlatteningResourceInputStream(final Collection<? extends Resource> ins) throws IOException {
        this(ins.toArray(Resources.EMPTY_ARRAY));
    }

    public FlatteningResourceInputStream(final Resource... resources) throws IOException {
        this.resources = resources;
    }

    @Override
    public void close() throws IOException {
        closeCurrent();
        eof = true;
    }

    @Override
    public int read() throws IOException {
        int result = readCurrent();
        if (result == EOF && !eof) {
            openFile(++currentIndex);
            result = readCurrent();
        }
        return result;
    }

    private int readCurrent() throws IOException {
        return (eof || currentStream == null) ? EOF : currentStream.read();
    }

    private void openFile(final int index) throws IOException {
        closeCurrent();
        if (resources != null && index < resources.length) {
            try {
                currentStream = resources[index].getInputStream();
            } catch (final IOException eyeOhEx) {
                throw eyeOhEx;
            }
        } else {
            eof = true;
        }
    }

    private void closeCurrent() {
        Closeables.closeQuietly(currentStream);
        currentStream = null;
    }
}
