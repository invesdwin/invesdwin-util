package de.invesdwin.util.streams.pool.buffered;

import java.io.File;
import java.io.FileNotFoundException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Files;

@NotThreadSafe
public class DeletingBufferedFileDataInputStream extends BufferedFileDataInputStream {

    private final File file;

    public DeletingBufferedFileDataInputStream(final File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    public DeletingBufferedFileDataInputStream(final File file, final int bufferSize) throws FileNotFoundException {
        super(file, bufferSize);
        this.file = file;
    }

    @Override
    protected void onClose() {
        super.onClose();
        Files.deleteQuietly(file);
    }

}
