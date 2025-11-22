package de.invesdwin.util.lang.uri;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.concurrent.Immutable;

import com.google.common.net.InetAddresses;

import de.invesdwin.util.collections.Arrays;

@Immutable
public final class Addresses {

    public static final int PORT_MIN = 1;
    public static final int PORT_MAX = 65535;
    private static List<Integer> allPorts;

    private static final Pattern BYTE_PATTERN = Pattern.compile("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
    private static final Pattern IPV4_PATTERN = Pattern
            .compile(BYTE_PATTERN + "\\." + BYTE_PATTERN + "\\." + BYTE_PATTERN + "\\." + BYTE_PATTERN);

    private Addresses() {}

    public static boolean isPort(final String port) {
        try {
            return isPort(Integer.parseInt(port));
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPort(final int port) {
        return port >= PORT_MIN && port <= PORT_MAX;
    }

    public static boolean isIp(final String address) {
        if (address == null) {
            return false;
        }
        return InetAddresses.isInetAddress(address);
    }

    public static boolean isIpV6(final String address) {
        if (!isIp(address)) {
            return false;
        }
        if (isIpV4(address)) {
            return false;
        }
        return true;
    }

    public static boolean isIpV4(final String address) {
        if (address == null) {
            return false;
        }
        return IPV4_PATTERN.matcher(address).matches();
    }

    /**
     * E.g.: 127.0.0.1:8080
     */
    public static boolean isIpV4WithPort(final String ipMitPort) {
        final String[] parts = ipMitPort.split(":");
        if (parts.length != 2) {
            return false;
        } else {
            return isIpV4(parts[0]) && isPort(parts[1]);
        }
    }

    public static InetAddress asAddress(final String host) {
        try {
            return InetAddress.getByName(host); //SUPPRESS CHECKSTYLE singleline
        } catch (final UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static InetSocketAddress asAddress(final String host, final int port) {
        final InetAddress addr = asAddress(host);
        return addr == null ? null : new InetSocketAddress(addr, port);
    }

    public static boolean isConnectionPossible(final String host, final int port) {
        try (Socket ignored = new Socket(host, port)) {
            return true;
        } catch (final Throwable e) {
            return false;
        }
    }

    public static List<Integer> getAllPorts() {
        if (allPorts == null) {
            synchronized (Addresses.class) {
                if (allPorts == null) {
                    final Integer[] ports = new Integer[PORT_MAX - PORT_MIN + 1];
                    for (int port = PORT_MIN; port <= PORT_MAX; port++) {
                        ports[port - PORT_MIN] = port;
                    }
                    allPorts = Arrays.asList(ports);
                }
            }
        }
        return allPorts;
    }

}
