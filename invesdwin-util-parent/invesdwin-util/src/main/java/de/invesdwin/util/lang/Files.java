package de.invesdwin.util.lang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Threads;
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

    private static final String[] NORMALIZE_FILENAME_SEARCH = { ":", "@", "*", "?", "<", ">", "=", "\"", "|", "/",
            "\\" };
    /**
     * need to use distinct characters here so that expressions don't become mixed if they only differ in an operator
     * that gets escaped here
     */
    private static final String[] NORMALIZE_FILENAME_REPLACE = { "c", "a", "m", "q", "l", "g", "e", "u", "p", "s",
            "b" };
    private static final String[] NORMALIZE_PATH_SEARCH = { ":", "@", "*", "?", "<", ">", "=", "\"", "|" };
    private static final String[] NORMALIZE_PATH_REPLACE = { "c", "a", "m", "q", "l", "g", "e", "u", "p" };
    private static final int MAX_FILE_NAME_LENGTH = 255;

    private static Boolean deleteNativeUnixAvailable = null;
    private static Boolean deleteNativeWindowsAvailable = null;

    static {
        Assertions.assertThat(NORMALIZE_FILENAME_SEARCH.length).isEqualByComparingTo(NORMALIZE_FILENAME_REPLACE.length);
        Assertions.assertThat(NORMALIZE_PATH_SEARCH.length).isEqualByComparingTo(NORMALIZE_PATH_REPLACE.length);
        if (!OperatingSystem.isWindows()) {
            deleteNativeWindowsAvailable = false;
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
        final File[] listFiles = directory.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (final File f : listFiles) {
                deleteEmptyDirectories(f);
            }
        }
    }

    /**
     * https://stackoverflow.com/questions/26017545/delete-all-empty-folders-in-java
     */
    public static long deleteEmptyDirectories(final File f) {
        final String[] listFiles = f.list();
        if (listFiles == null || listFiles.length == 0) {
            return 0L;
        }
        long totalSize = 0L;
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
        return normalizePathMaxLength(Strings.replaceEach(name, NORMALIZE_FILENAME_SEARCH, NORMALIZE_FILENAME_REPLACE));
    }

    public static String normalizePath(final String path) {
        return normalizePathMaxLength(Strings.replaceEach(path, NORMALIZE_PATH_SEARCH, NORMALIZE_PATH_REPLACE));
    }

    private static String normalizePathMaxLength(final String path) {
        final StringBuilder sb = new StringBuilder();
        int lengthSinceLastSeparator = 0;
        for (int i = 0; i < path.length(); i++) {
            final char c = path.charAt(i);
            if (c == '/' || c == '\\') {
                lengthSinceLastSeparator = 0;
            } else {
                lengthSinceLastSeparator++;
                if (lengthSinceLastSeparator >= MAX_FILE_NAME_LENGTH) {
                    sb.append(File.separatorChar);
                    lengthSinceLastSeparator = 0;
                }
            }
            sb.append(c);
        }
        if (sb.length() != path.length()) {
            return sb.toString();
        } else {
            return path;
        }
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

    public static boolean exists(final File file) {
        if (file == null) {
            return false;
        }
        return file.exists();
    }

    public static boolean writeStringToFileIfDifferent(final File file, final String newContent) {
        boolean write;
        if (file.exists()) {
            try {
                final String existingContent = readFileToString(file, Charset.defaultCharset());
                write = !existingContent.equals(newContent);
            } catch (final IOException e) {
                write = true;
            }
        } else {
            write = true;
        }
        if (write) {
            try {
                writeStringToFile(file, newContent, Charset.defaultCharset());
                return true;
            } catch (final IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean deleteNative(final File file) {
        //rm with cygwin is also faster on windows because it does an unlink only
        if (deleteNativeUnixIfAvailable(file)) {
            return true;
        }
        if (deleteNativeWindowsIfAvailable(file)) {
            return true;
        }
        return deleteQuietly(file);
    }

    private static boolean deleteNativeUnixIfAvailable(final File file) {
        if (deleteNativeUnixAvailable == null) {
            final boolean success = deleteNativeUnix(file) && !file.exists();
            if (success) {
                deleteNativeUnixAvailable = true;
                return true;
            } else {
                if (!Threads.isInterrupted()) {
                    try {
                        final File tempDir = createNativeDeleteTestDir();
                        final boolean returnCode = deleteNativeUnix(tempDir);
                        final boolean stillExists = tempDir.exists();
                        if (stillExists) {
                            deleteQuietly(tempDir);
                        }
                        deleteNativeUnixAvailable = returnCode && stillExists;
                    } catch (final IOException e) {
                        //we just give up
                        deleteNativeUnixAvailable = false;
                    }
                }
                return false;
            }
        } else if (!deleteNativeUnixAvailable) {
            return false;
        } else {
            return deleteNativeUnix(file);
        }
    }

    private static boolean deleteNativeWindowsIfAvailable(final File file) {
        if (deleteNativeWindowsAvailable == null) {
            final boolean success = deleteNativeWindows(file) && !file.exists();
            if (success) {
                deleteNativeWindowsAvailable = true;
                return true;
            } else {
                if (!Threads.isInterrupted()) {
                    try {
                        final File tempDir = createNativeDeleteTestDir();
                        final boolean returnCode = deleteNativeWindows(tempDir);
                        final boolean stillExists = tempDir.exists();
                        if (stillExists) {
                            deleteQuietly(tempDir);
                        }
                        deleteNativeWindowsAvailable = returnCode && stillExists;
                    } catch (final IOException e) {
                        //we just give up
                        deleteNativeWindowsAvailable = false;
                    }
                }
                return false;
            }
        } else if (!deleteNativeWindowsAvailable) {
            return false;
        } else {
            return deleteNativeWindows(file);
        }
    }

    private static File createNativeDeleteTestDir() throws IOException {
        final String nativeDeleteTest = "nativeDeleteTest";
        final File tempDir = createTempDirectory(nativeDeleteTest).toFile();
        writeStringToFile(new File(tempDir, nativeDeleteTest + ".txt"), nativeDeleteTest, Charset.defaultCharset());
        return tempDir;
    }

    private static boolean deleteNativeUnix(final File file) {
        try {
            final String[] deleteCommand = new String[] { "/bin/rm", "-rf", file.getAbsolutePath() };
            final Process process = new ProcessBuilder(deleteCommand).start();
            final int returnCode = process.waitFor();
            return returnCode == 0;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * https://stackoverflow.com/questions/186737/whats-the-fastest-way-to-delete-a-large-folder-in-windows/6208144#6208144
     */
    private static boolean deleteNativeWindows(final File file) {
        try {
            final String path = file.getAbsolutePath();
            final String deleteCommand;
            if (file.isDirectory()) {
                deleteCommand = "del /f/s/q \"" + path + "\" > nul & rmdir /s/q \"" + path + "\" > nul";
            } else {
                //rmdir would give a 9009 return code if file does not exist
                deleteCommand = "del /f/s/q \"" + path + "\" > nul";
            }
            final Process process = new ProcessBuilder("cmd.exe", "/c", deleteCommand).start();
            final int returnCode = process.waitFor();
            return returnCode == 0;
        } catch (final Exception e) {
            return false;
        }
    }

    public static String getExtension(final File f) {
        final int i = f.getName().lastIndexOf('.');
        if (i < 0) {
            return "";
        }
        final String extension = f.getName().substring(i);
        return extension;
    }

    public static File setExtension(final File f, final String newExtension) {
        final int i = f.getName().lastIndexOf('.');
        if (i < 0) {
            return new File(f.getParent(), f.getName() + newExtension);
        } else {
            final String name = f.getName().substring(0, i);
            return new File(f.getParent(), name + newExtension);
        }
    }

    public static File prefixExtension(final File f, final String prefix) {
        final String newExtension = prefix + getExtension(f);
        return setExtension(f, newExtension);
    }

}
