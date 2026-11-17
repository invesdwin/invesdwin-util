package de.invesdwin.util.lang;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;

/**
 * The native recursive delete (the shell's own remove command) is chosen once per JVM by a probe against a test
 * directory, and the probe's answer must be the one the probe actually observed. Measured 2026-11-17: after a single
 * delete of a directory the caller may not unlink, the cached answer came back "not available", so every later delete
 * in that JVM ran on the java.nio tree walk instead - 100,000 entries in 2,737 ms against 1,174 ms.
 */
@NotThreadSafe
public class FilesNativeDeleteTest {

    @Test
    public void aNativeDeleteThatFailsLeavesTheNativeCommandAvailable() throws Exception {
        Assumptions.assumeTrue(!OperatingSystem.isWindows(),
                "the ununlinkable-target shape is built with the unix write bit; the windows probe path is not exercised here");
        resetNativeDeleteFlags();
        final File base = newTestDir("failed-target");
        try {
            final File locked = new File(base, "locked");
            Files.forceMkdir(locked);
            Assertions.assertThat(new File(locked, "inside.txt").createNewFile()).isTrue();
            Assertions.assertThat(locked.setWritable(false))
                    .as("the target has to be ununlinkable, or the probe branch never runs")
                    .isTrue();
            Assertions.assertThat(Files.deleteNative(locked)).as("this delete legitimately fails").isFalse();
            Assertions.assertThat(nativeDeleteFlag("deleteNativeUnixAvailable"))
                    .as("a delete that failed on its target must not switch the whole JVM onto the java.nio walk")
                    .isNotEqualTo(Boolean.FALSE);
        } finally {
            //the availability cache is JVM-wide static state: leaving it set would decide the path other tests take
            resetNativeDeleteFlags();
            makeWritable(base);
            Files.deleteQuietly(base);
        }
    }

    @Test
    public void aNativeDeleteOnAnExistingTreeLeavesTheNativeCommandAvailable() throws Exception {
        resetNativeDeleteFlags();
        final File base = newTestDir("deletable-tree");
        try {
            final File tree = newTree(new File(base, "tree"), 20, 50);
            Assertions.assertThat(Files.deleteNative(tree)).isTrue();
            Assertions.assertThat(tree).doesNotExist();
            Assertions.assertThat(nativeDeleteFlag("deleteNativeUnixAvailable")).isEqualTo(Boolean.TRUE);
        } finally {
            resetNativeDeleteFlags();
            Files.deleteQuietly(base);
        }
    }

    @Test
    public void theDetachedNativeDeleteRunsOnItsOwnProcess() throws Exception {
        final File base = newTestDir("detached");
        try {
            final File tree = newTree(new File(base, "tree"), 20, 50);
            final Process process = Files.deleteNativeDetached(tree);
            Assertions.assertThat(process).isNotNull();
            Assertions.assertThat(process.waitFor(30, TimeUnit.SECONDS))
                    .as("the caller never blocks on the delete, so the process has to reach an exit of its own")
                    .isTrue();
            Assertions.assertThat(process.exitValue()).isZero();
            Assertions.assertThat(tree).doesNotExist();
        } finally {
            Files.deleteQuietly(base);
        }
    }

    private File newTestDir(final String name) throws IOException {
        final File dir = new File(Files.getTempDirectory(), FilesNativeDeleteTest.class.getSimpleName() + "-" + name);
        Files.deleteQuietly(dir);
        Files.forceMkdir(dir);
        return dir;
    }

    private File newTree(final File root, final int dirs, final int filesPerDir) throws IOException {
        Files.forceMkdir(root);
        for (int d = 0; d < dirs; d++) {
            final File dir = new File(root, "d" + d);
            Files.forceMkdir(dir);
            for (int f = 0; f < filesPerDir; f++) {
                Assertions.assertThat(new File(dir, "f" + f).createNewFile()).isTrue();
            }
        }
        return root;
    }

    private void makeWritable(final File file) {
        file.setWritable(true);
        final File[] children = file.listFiles();
        if (children != null) {
            for (final File child : children) {
                if (child.isDirectory()) {
                    makeWritable(child);
                }
            }
        }
    }

    private static Boolean nativeDeleteFlag(final String name) throws Exception {
        final Field field = Files.class.getDeclaredField(name);
        field.setAccessible(true);
        return (Boolean) field.get(null);
    }

    private static void resetNativeDeleteFlags() throws Exception {
        for (final String name : new String[] { "deleteNativeUnixAvailable", "deleteNativeWindowsAvailable" }) {
            final Field field = Files.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(null, null);
        }
    }

}
