package de.invesdwin.util.lang;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@NotThreadSafe
public class LazyCopyFolderRunnable implements Runnable {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(AFinalizer.class);

    private final File fromFolder;
    private final File toFolder;
    private final Map<String, Long> file_lastModified = new HashMap<String, Long>();

    public LazyCopyFolderRunnable(final File fromFolder, final File toFolder) {
        this.fromFolder = fromFolder;
        this.toFolder = toFolder;
    }

    @Override
    public void run() {
        try {
            Files.copyDirectory(fromFolder, toFolder, new FileFilter() {
                @Override
                public boolean accept(final File pathname) {
                    final String path = pathname.getAbsolutePath();
                    final Long prevLastModified = file_lastModified.get(path);
                    final long lastModified = pathname.lastModified();
                    if (prevLastModified == null || prevLastModified != lastModified) {
                        file_lastModified.put(path, lastModified);
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } catch (final Throwable t) {
            //CHECKSTYLE:OFF
            LOG.error("Failed to copy folder [{}] to folder [{}]: {}", fromFolder, toFolder,
                    Throwables.getFullStackTrace(t));
            //CHECKSTYLE:ON
        }
    }
}