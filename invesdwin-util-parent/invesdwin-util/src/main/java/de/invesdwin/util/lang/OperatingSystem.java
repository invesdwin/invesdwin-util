package de.invesdwin.util.lang;

import javax.annotation.concurrent.Immutable;

/**
 * Provide simplified platform information. Derived from JNA.
 */
@Immutable
public enum OperatingSystem {
    UNSPECIFIED,
    MAC,
    LINUX,
    WINDOWS,
    SOLARIS,
    FREEBSD,
    OPENBSD,
    WINDOWSCE;

    private static final OperatingSystem OS;

    static {
        //CHECKSTYLE:OFF
        final String osName = System.getProperty("os.name");
        //CHECKSTYLE:ON
        if (osName.startsWith("Linux")) {
            OS = LINUX;
        } else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
            OS = MAC;
        } else if (osName.startsWith("Windows CE")) {
            OS = WINDOWSCE;
        } else if (osName.startsWith("Windows")) {
            OS = WINDOWS;
        } else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
            OS = SOLARIS;
        } else if (osName.startsWith("FreeBSD")) {
            OS = FREEBSD;
        } else if (osName.startsWith("OpenBSD")) {
            OS = OPENBSD;
        } else {
            OS = UNSPECIFIED;
        }
    }

    OperatingSystem() {
    }

    public static OperatingSystem getCurrent() {
        return OS;
    }

    public static boolean isMac() {
        return OS == MAC;
    }

    public static boolean isLinux() {
        return OS == LINUX;
    }

    public static boolean isWindowsCE() {
        return OS == WINDOWSCE;
    }

    public static boolean isWindows() {
        return OS == WINDOWS || OS == WINDOWSCE;
    }

    public static boolean isSolaris() {
        return OS == SOLARIS;
    }

    public static boolean isFreeBSD() {
        return OS == FREEBSD;
    }

    public static boolean isOpenBSD() {
        return OS == OPENBSD;
    }

}
