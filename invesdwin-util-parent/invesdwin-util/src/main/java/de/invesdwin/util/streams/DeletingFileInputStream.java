package de.invesdwin.util.streams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.lang.Files;

@NotThreadSafe
public class DeletingFileInputStream extends FileInputStream {

    private final File file;

    public DeletingFileInputStream(final File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    @Override
    public void close() throws IOException {
        super.close();
        Files.deleteQuietly(file);
    }

}
