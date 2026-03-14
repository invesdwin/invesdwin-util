package de.invesdwin.util.lang;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.lang.finalizer.AFinalizer;

@NotThreadSafe
public class LazyCopyFilesRunnable implements Runnable {

    private static final org.slf4j.ext.XLogger LOG = org.slf4j.ext.XLoggerFactory.getXLogger(AFinalizer.class);

    private final List<File> fromFiles;
    private final File toFolder;
    private final Map<String, Long> file_lastModified = ILockCollectionFactory.getInstance(false).newMap();

    public LazyCopyFilesRunnable(final List<File> fromFiles, final File toFolder) {
        this.fromFiles = fromFiles;
        this.toFolder = toFolder;
    }

    @Override
    public void run() {
        for (int i = 0; i < fromFiles.size(); i++) {
            final File fromFile = fromFiles.get(i);
            if (!fromFile.exists()) {
                continue;
            }
            try {
                final String path = fromFile.getAbsolutePath();
                final Long prevLastModified = file_lastModified.get(path);
                final long lastModified = fromFile.lastModified();
                if (prevLastModified == null || prevLastModified != lastModified) {
                    file_lastModified.put(path, lastModified);
                    Files.copyFile(fromFile, toFolder);
                }
            } catch (final Throwable t) {
                //CHECKSTYLE:OFF
                LOG.error("Failed to copy file [{}] to folder [{}]: {}", fromFile, toFolder,
                        Throwables.getFullStackTrace(t));
                //CHECKSTYLE:ON
            }
        }
    }
}