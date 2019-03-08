package de.invesdwin.util.classpath;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

@NotThreadSafe
public final class FastClassPathScanner {

    private static Set<String> blacklistPaths = new LinkedHashSet<String>();
    private static Set<String> whitelistPaths = new LinkedHashSet<String>();
    @GuardedBy("this")
    private static ScanResult scanResult;

    private FastClassPathScanner() {}

    public static synchronized ScanResult getScanResult() {
        if (scanResult == null) {
            final ClassGraph graph = new ClassGraph().removeTemporaryFilesAfterScan()
                    .enableClassInfo()
                    .enableAnnotationInfo();
            for (final String blacklistPath : blacklistPaths) {
                graph.blacklistPaths(blacklistPath);
            }
            for (final String whitelistPath : whitelistPaths) {
                graph.whitelistPaths(whitelistPath);
            }
            scanResult = graph.scan();
        }
        return scanResult;
    }

    public static synchronized void addBlacklistPath(final String blacklistPath) {
        if (FastClassPathScanner.blacklistPaths.add(blacklistPath)) {
            if (scanResult != null) {
                scanResult.close();
                scanResult = null;
            }
        }
    }

    public static Set<String> getBlacklistPaths() {
        return Collections.unmodifiableSet(FastClassPathScanner.blacklistPaths);
    }

    public static synchronized void addWhitelistPath(final String whitelistPath) {
        if (FastClassPathScanner.whitelistPaths.add(whitelistPath)) {
            if (scanResult != null) {
                scanResult.close();
                scanResult = null;
            }
        }
    }

    public static Set<String> getWhitelistPaths() {
        return Collections.unmodifiableSet(FastClassPathScanner.whitelistPaths);
    }

}
