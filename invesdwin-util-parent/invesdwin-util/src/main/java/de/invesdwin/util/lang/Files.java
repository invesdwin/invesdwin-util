package de.invesdwin.util.lang;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.lang.internal.AFilesStaticFacade;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
@StaticFacadeDefinition(name = "de.invesdwin.util.lang.internal.AFilesStaticFacade", targets = {
        org.apache.commons.io.FileUtils.class, java.nio.file.Files.class }, filterSeeMethodSignatures = {
                //these methods are not available in java 8
                "java.nio.file.Files#readString(java.nio.file.Path)",
                "java.nio.file.Files#readString(java.nio.file.Path, java.nio.charset.Charset)",
                "java.nio.file.Files#writeString(java.nio.file.Path, java.lang.CharSequence, java.nio.file.OpenOption...)",
                "java.nio.file.Files#writeString(java.nio.file.Path, java.lang.CharSequence, java.nio.charset.Charset, java.nio.file.OpenOption...)",
                "java.nio.file.Files#mismatch(java.nio.file.Path, java.nio.file.Path)" })
public final class Files extends AFilesStaticFacade {

    private static final String[] NORMALIZE_FILENAME_SEARCH = { ":", "@", "/", "\\", "*", "?", "<", ">", "\"", "|" };
    private static final String[] NORMALIZE_FILENAME_REPLACE;

    static {
        NORMALIZE_FILENAME_REPLACE = new String[NORMALIZE_FILENAME_SEARCH.length];
        for (int i = 0; i < NORMALIZE_FILENAME_REPLACE.length; i++) {
            NORMALIZE_FILENAME_REPLACE[i] = "_";
        }
    }

    private Files() {
    }

    public static void purgeOldFiles(final File directory, final Duration threshold) {
        if (!directory.exists()) {
            return;
        }
        final FDate thresholdDate = new FDate().subtract(threshold);
        final Iterator<File> filesToDelete = iterateFiles(directory, new AgeFileFilter(thresholdDate.dateValue(), true),
                TrueFileFilter.INSTANCE);
        while (filesToDelete.hasNext()) {
            final File fileToDelete = filesToDelete.next();
            fileToDelete.delete();
        }
        for (final File f : directory.listFiles()) {
            deleteEmptyDirectories(f);
        }
    }

    /**
     * https://stackoverflow.com/questions/26017545/delete-all-empty-folders-in-java
     */
    public static long deleteEmptyDirectories(final File f) {
        final String[] listFiles = f.list();
        long totalSize = 0;
        for (final String file : listFiles) {
            final File folder = new File(f, file);
            if (folder.isDirectory()) {
                totalSize += deleteEmptyDirectories(folder);
            } else {
                totalSize += folder.length();
            }
        }

        if (totalSize == 0) {
            f.delete();
        }

        return totalSize;
    }

    public static boolean isEmptyDirectory(final File f) {
        final String[] listFiles = f.list();
        return listFiles == null || listFiles.length == 0;
    }

    public static String normalizeFilename(final String name) {
        return Strings.replaceEach(name, NORMALIZE_FILENAME_SEARCH, NORMALIZE_FILENAME_REPLACE);
    }

    public static boolean isDirectoryEmpty(final File directory) throws IOException {
        return isDirectoryEmpty(directory.toPath());
    }

    public static boolean isDirectoryEmpty(final Path directory) throws IOException {
        if (!isDirectory(directory)) {
            return false;
        }
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        }
    }

}
