package de.invesdwin.util.lang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.zeroturnaround.exec.InvalidExitValueException;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import de.invesdwin.norva.apt.staticfacade.StaticFacadeDefinition;
import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.lang.internal.AFilesStaticFacade;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.decimal.scaled.ByteSizeScale;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

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

    public static final int DEFAULT_MAX_REFERENCE_LENGTH = (int) ByteSizeScale.BYTES.convert(10,
            ByteSizeScale.MEGABYTES);

    public static final String[] NORMALIZE_FILENAME_SEARCH = { ":", "@", "*", "?", "<", ">", "=", "\"", "|", "/",
            "\\" };
    /**
     * need to use distinct characters here so that expressions don't become mixed if they only differ in an operator
     * that gets escaped here
     */
    public static final String[] NORMALIZE_FILENAME_REPLACE = { "c", "a", "m", "q", "l", "g", "e", "u", "p", "s", "b" };
    public static final String[] NORMALIZE_PATH_SEARCH = { ":", "@", "*", "?", "<", ">", "=", "\"", "|" };
    public static final String[] NORMALIZE_PATH_REPLACE = { "c", "a", "m", "q", "l", "g", "e", "u", "p" };
    /*
     * 256 should be maximum, but we need a few less so that windows explorer can actually delete too long paths maybe
     * for some "" that it adds internally
     */
    public static final int MAX_FILE_NAME_LENGTH = 254;

    private static Boolean deleteNativeUnixAvailable = null;
    private static Boolean deleteNativeWindowsAvailable = null;

    static {
        Assertions.assertThat(NORMALIZE_FILENAME_SEARCH.length).isEqualByComparingTo(NORMALIZE_FILENAME_REPLACE.length);
        Assertions.assertThat(NORMALIZE_PATH_SEARCH.length).isEqualByComparingTo(NORMALIZE_PATH_REPLACE.length);
        if (!OperatingSystem.isWindows()) {
            deleteNativeWindowsAvailable = false;
        }
    }

    private Files() {}

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

    public static File normalizePath(final File path) {
        if (path.isAbsolute()) {
            final String absolutePath = path.getAbsolutePath();
            if (absolutePath.charAt(1) == ':' && absolutePath.length() > 4) {
                //we are on windows with a device location
                final String pathSuffix = absolutePath.substring(2);
                final String pathPrefix = absolutePath.substring(0, 2);
                final String pathSuffixNormalized = normalizePath(pathSuffix);
                final String normalizedPath = pathPrefix + pathSuffixNormalized;
                return new File(normalizedPath);
            } else {
                final String normalizedPath = normalizePath(path.getAbsolutePath());
                return new File(normalizedPath);
            }
        } else {
            final String normalizedPath = normalizePath(path.getPath());
            return new File(normalizedPath);
        }
    }

    public static String normalizePath(final String path) {
        return normalizePathMaxLength(Strings.replaceEach(path, NORMALIZE_PATH_SEARCH, NORMALIZE_PATH_REPLACE));
    }

    public static String normalizePathMaxLength(final String path) {
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
            //sometimes it does not work properly with some files, in that case we need to go again with java delete
            return deleteNativeWindows(file) && !file.exists();
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
            final int returnCode = executeCommand(deleteCommand);
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
            final int returnCode = executeCommand("cmd.exe", "/c", deleteCommand);
            return returnCode == 0;
        } catch (final Exception e) {
            return false;
        }
    }

    public static String getExtension(final File f) {
        final String fileName = f.getName();
        final int i = fileName.lastIndexOf('.');
        if (i < 0) {
            return "";
        }
        final String extension = fileName.substring(i);
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

    /**
     * Overwrites the destFile if it already exists.
     */
    public static void moveFile(final java.io.File srcFile, final java.io.File destFile) throws java.io.IOException {
        try {
            org.apache.commons.io.FileUtils.moveFile(srcFile, destFile);
        } catch (final FileExistsException e) {
            //delete and retry
            deleteQuietly(destFile);
            org.apache.commons.io.FileUtils.moveFile(srcFile, destFile);
        }
    }

    private static int executeCommand(final String... command)
            throws IOException, InvalidExitValueException, InterruptedException, TimeoutException {
        final ProcessResult result = new ProcessExecutor().command(command)
                .destroyOnExit()
                //                .redirectOutput(Slf4jStream.of(Files.class).asDebug())
                //                .redirectError(Slf4jStream.of(Files.class).asWarn())
                .execute();
        return result.getExitValue();
    }

    public static void checkReference(final boolean createReferenceFile, final File referenceFile,
            final String reference) {
        checkReference(createReferenceFile, referenceFile, reference, DEFAULT_MAX_REFERENCE_LENGTH);
    }

    public static void checkReference(final boolean createReferenceFile, final File referenceFile,
            final String reference, final int maxReferenceLength) {
        final List<String> references = Strings.splitByMaxLength(reference, maxReferenceLength);
        int i = 0;
        while (i < references.size()) {
            final String ref = references.get(i);
            final File indexedReferenceFile;
            if (i == 0) {
                indexedReferenceFile = referenceFile;
            } else {
                indexedReferenceFile = Files.prefixExtension(referenceFile, "_" + i);
            }
            if (createReferenceFile) {
                Files.writeStringToFileIfDifferent(indexedReferenceFile, ref);
            }
            try {
                final String existingRef = Files.readFileToString(indexedReferenceFile, Charset.defaultCharset());
                Assertions.checkEquals(existingRef, ref);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        if (createReferenceFile) {
            //delete any existing reference files that are not needed anymore
            while (true) {
                final File obsoleteIndexedReferenceFile = Files.prefixExtension(referenceFile, "_" + i);
                if (obsoleteIndexedReferenceFile.exists()) {
                    Files.deleteQuietly(obsoleteIndexedReferenceFile);
                } else {
                    break;
                }
                i++;
            }
        }
    }

    public static void writeReference(final File referenceFile, final String reference) {
        writeReference(referenceFile, reference, DEFAULT_MAX_REFERENCE_LENGTH);
    }

    public static void writeReference(final File referenceFile, final String reference, final int maxReferenceLength) {
        final List<String> references = Strings.splitByMaxLength(reference, maxReferenceLength);
        int i = 0;
        while (i < references.size()) {
            final String ref = references.get(i);
            final File indexedReferenceFile;
            if (i == 0) {
                indexedReferenceFile = referenceFile;
            } else {
                indexedReferenceFile = Files.prefixExtension(referenceFile, "_" + i);
            }
            Files.writeStringToFileIfDifferent(indexedReferenceFile, ref);
            i++;
        }
        //delete any existing reference files that are not needed anymore
        while (true) {
            i++;
            final File obsoleteIndexedReferenceFile = Files.prefixExtension(referenceFile, "_" + i);
            if (obsoleteIndexedReferenceFile.exists()) {
                Files.deleteQuietly(obsoleteIndexedReferenceFile);
            } else {
                break;
            }
        }
    }

    public static String readReference(final File referenceFile) {
        final StringBuilder reference = new StringBuilder();
        int i = 0;
        while (true) {
            final File indexedReferenceFile;
            if (i == 0) {
                indexedReferenceFile = referenceFile;
            } else {
                indexedReferenceFile = Files.prefixExtension(referenceFile, "_" + i);
            }
            i++;
            if (indexedReferenceFile.exists()) {
                final String indexedReference;
                try {
                    indexedReference = Files.readFileToString(indexedReferenceFile, Charset.defaultCharset());
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                if (reference.length() > 0) {
                    reference.append("\n");
                }
                reference.append(indexedReference);
            } else {
                break;
            }
        }
        return reference.toString();
    }

    public static boolean moveFileQuietly(final File srcFile, final File destFile) {
        if (!srcFile.exists()) {
            return false;
        }
        try {
            Files.moveFile(srcFile, destFile);
            return true;
        } catch (final IOException e) {
            return false;
        }
    }

    public static boolean touchQuietly(final File file) {
        try {
            touch(file);
            return true;
        } catch (final IOException e) {
            return false;
        }
    }

    public static File createFolder(final File parent, final String name) {
        final File folder = new File(parent, name);
        try {
            Files.forceMkdir(folder);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return folder;
    }

    public static boolean redirectedFileExists(final File redirectFile) {
        if (!redirectFile.exists()) {
            return false;
        }
        try {
            final String firstFileStr = Files.readFileToString(redirectFile, Charsets.DEFAULT);
            if (Strings.isBlank(firstFileStr)) {
                return false;
            }
            final File firstFile = new File(firstFileStr);
            if (firstFile.exists()) {
                return true;
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean createRedirectFile(final File redirectFile, final File redirectedFile) {
        if (redirectedFile != null && redirectedFile.exists()) {
            try {
                Files.forceMkdirParent(redirectFile);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            Files.writeStringToFileIfDifferent(redirectFile, redirectedFile.getAbsolutePath());
            return true;
        } else {
            Files.deleteQuietly(redirectFile);
            return false;
        }
    }

    public static void cleanDirectoryQuietly(final File directory) {
        try {
            cleanDirectory(directory);
        } catch (final IOException e) {
            //ignore
        }
    }

}
