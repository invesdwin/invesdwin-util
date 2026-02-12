package de.invesdwin.util.streams;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.SortedSet;

import javax.annotation.concurrent.NotThreadSafe;
import javax.net.ServerSocketFactory;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.math.Integers;
import de.invesdwin.util.math.random.PseudoRandomGenerators;

/**
 * Taken from: org.springframework.util.SocketUtils
 * 
 * Simple utility methods for working with network sockets &mdash; for example, for finding available ports on
 * {@code localhost}.
 *
 * <p>
 * Within this class, a TCP port refers to a port for a {@link ServerSocket}; whereas, a UDP port refers to a port for a
 * {@link DatagramSocket}.
 *
 * <p>
 * {@code SocketUtils} was introduced in Spring Framework 4.0, primarily to assist in writing integration tests which
 * start an external server on an available random port. However, these utilities make no guarantee about the subsequent
 * availability of a given port and are therefore unreliable. Instead of using {@code SocketUtils} to find an available
 * local port for a server, it is recommended that you rely on a server's ability to start on a random port that it
 * selects or is assigned by the operating system. To interact with that server, you should query the server for the
 * port it is currently using.
 *
 */
//CHECKSTYLE:OFF
@NotThreadSafe
public class SocketUtils {
    //CHECKSTYLE:ON

    /**
     * The default minimum value for port ranges used when finding an available socket port.
     */
    public static final int PORT_RANGE_MIN = 1024;

    /**
     * The default maximum value for port ranges used when finding an available socket port.
     */
    public static final int PORT_RANGE_MAX = 65535;

    /**
     * Although {@code SocketUtils} consists solely of static utility methods, this constructor is intentionally
     * {@code public}.
     * <h4>Rationale</h4>
     * <p>
     * Static methods from this class may be invoked from within XML configuration files using the Spring Expression
     * Language (SpEL) and the following syntax.
     *
     * <pre>
     * <code>&lt;bean id="bean1" ... p:port="#{T(org.springframework.util.SocketUtils).findAvailableTcpPort(12000)}" /&gt;</code>
     * </pre>
     *
     * If this constructor were {@code private}, you would be required to supply the fully qualified class name to
     * SpEL's {@code T()} function for each usage. Thus, the fact that this constructor is {@code public} allows you to
     * reduce boilerplate configuration with SpEL as can be seen in the following example.
     *
     * <pre>
     * <code>&lt;bean id="socketUtils" class="org.springframework.util.SocketUtils" /&gt;
     * &lt;bean id="bean1" ... p:port="#{socketUtils.findAvailableTcpPort(12000)}" /&gt;
     * &lt;bean id="bean2" ... p:port="#{socketUtils.findAvailableTcpPort(30000)}" /&gt;</code>
     * </pre>
     */
    public SocketUtils() {}

    /**
     * Find an available TCP port randomly selected from the range [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
     *
     * @return an available TCP port number
     * @throws IllegalStateException
     *             if no available port could be found
     */
    public static int findAvailableTcpPort() {
        return findAvailableTcpPort(PORT_RANGE_MIN);
    }

    /**
     * Find an available TCP port randomly selected from the range [{@code minPort}, {@value #PORT_RANGE_MAX}].
     *
     * @param minPort
     *            the minimum port number
     * @return an available TCP port number
     * @throws IllegalStateException
     *             if no available port could be found
     */
    public static int findAvailableTcpPort(final int minPort) {
        return findAvailableTcpPort(minPort, PORT_RANGE_MAX);
    }

    /**
     * Find an available TCP port randomly selected from the range [{@code minPort}, {@code maxPort}].
     *
     * @param minPort
     *            the minimum port number
     * @param maxPort
     *            the maximum port number
     * @return an available TCP port number
     * @throws IllegalStateException
     *             if no available port could be found
     */
    public static int findAvailableTcpPort(final int minPort, final int maxPort) {
        return SocketType.TCP.findAvailablePort(minPort, maxPort);
    }

    /**
     * Find the requested number of available TCP ports, each randomly selected from the range
     * [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
     *
     * @param numRequested
     *            the number of available ports to find
     * @return a sorted set of available TCP port numbers
     * @throws IllegalStateException
     *             if the requested number of available ports could not be found
     */
    public static int[] findAvailableTcpPorts(final int numRequested) {
        return findAvailableTcpPorts(numRequested, PORT_RANGE_MIN, PORT_RANGE_MAX);
    }

    /**
     * Find the requested number of available TCP ports, each randomly selected from the range [{@code minPort},
     * {@code maxPort}].
     *
     * @param numRequested
     *            the number of available ports to find
     * @param minPort
     *            the minimum port number
     * @param maxPort
     *            the maximum port number
     * @return a sorted set of available TCP port numbers
     * @throws IllegalStateException
     *             if the requested number of available ports could not be found
     */
    public static int[] findAvailableTcpPorts(final int numRequested, final int minPort, final int maxPort) {
        return SocketType.TCP.findAvailablePorts(numRequested, minPort, maxPort);
    }

    /**
     * Find an available UDP port randomly selected from the range [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
     *
     * @return an available UDP port number
     * @throws IllegalStateException
     *             if no available port could be found
     */
    public static int findAvailableUdpPort() {
        return findAvailableUdpPort(PORT_RANGE_MIN);
    }

    /**
     * Find an available UDP port randomly selected from the range [{@code minPort}, {@value #PORT_RANGE_MAX}].
     *
     * @param minPort
     *            the minimum port number
     * @return an available UDP port number
     * @throws IllegalStateException
     *             if no available port could be found
     */
    public static int findAvailableUdpPort(final int minPort) {
        return findAvailableUdpPort(minPort, PORT_RANGE_MAX);
    }

    /**
     * Find an available UDP port randomly selected from the range [{@code minPort}, {@code maxPort}].
     *
     * @param minPort
     *            the minimum port number
     * @param maxPort
     *            the maximum port number
     * @return an available UDP port number
     * @throws IllegalStateException
     *             if no available port could be found
     */
    public static int findAvailableUdpPort(final int minPort, final int maxPort) {
        return SocketType.UDP.findAvailablePort(minPort, maxPort);
    }

    /**
     * Find the requested number of available UDP ports, each randomly selected from the range
     * [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
     *
     * @param numRequested
     *            the number of available ports to find
     * @return a sorted set of available UDP port numbers
     * @throws IllegalStateException
     *             if the requested number of available ports could not be found
     */
    public static int[] findAvailableUdpPorts(final int numRequested) {
        return findAvailableUdpPorts(numRequested, PORT_RANGE_MIN, PORT_RANGE_MAX);
    }

    /**
     * Find the requested number of available UDP ports, each randomly selected from the range [{@code minPort},
     * {@code maxPort}].
     *
     * @param numRequested
     *            the number of available ports to find
     * @param minPort
     *            the minimum port number
     * @param maxPort
     *            the maximum port number
     * @return a sorted set of available UDP port numbers
     * @throws IllegalStateException
     *             if the requested number of available ports could not be found
     */
    public static int[] findAvailableUdpPorts(final int numRequested, final int minPort, final int maxPort) {
        return SocketType.UDP.findAvailablePorts(numRequested, minPort, maxPort);
    }

    private enum SocketType {

        TCP {
            @Override
            protected boolean isPortAvailable(final int port) {
                try {
                    //CHECKSTYLE:OFF
                    final ServerSocket serverSocket = ServerSocketFactory.getDefault()
                            .createServerSocket(port, 1, InetAddress.getByName("localhost"));
                    //CHECKSTYLE:ON
                    serverSocket.close();
                    return true;
                } catch (final Exception ex) {
                    return false;
                }
            }
        },

        UDP {
            @Override
            protected boolean isPortAvailable(final int port) {
                try {
                    //CHECKSTYLE:OFF
                    final DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("localhost"));
                    //CHECKSTYLE:ON
                    socket.close();
                    return true;
                } catch (final Exception ex) {
                    return false;
                }
            }
        };

        /**
         * Determine if the specified port for this {@code SocketType} is currently available on {@code localhost}.
         */
        protected abstract boolean isPortAvailable(int port);

        /**
         * Find a pseudo-random port number within the range [{@code minPort}, {@code maxPort}].
         *
         * @param minPort
         *            the minimum port number
         * @param maxPort
         *            the maximum port number
         * @return a random port number within the specified range
         */
        private int findRandomPort(final int minPort, final int maxPort) {
            final int portRange = maxPort - minPort;
            return minPort + PseudoRandomGenerators.getThreadLocalPseudoRandom().nextInt(portRange + 1);
        }

        /**
         * Find an available port for this {@code SocketType}, randomly selected from the range [{@code minPort},
         * {@code maxPort}].
         *
         * @param minPort
         *            the minimum port number
         * @param maxPort
         *            the maximum port number
         * @return an available port number for this socket type
         * @throws IllegalStateException
         *             if no available port could be found
         */
        int findAvailablePort(final int minPort, final int maxPort) {
            Assertions.checkTrue(minPort > 0, "'minPort' must be greater than 0");
            Assertions.checkTrue(maxPort >= minPort, "'maxPort' must be greater than or equal to 'minPort'");
            Assertions.checkTrue(maxPort <= PORT_RANGE_MAX,
                    "'maxPort' must be less than or equal to " + PORT_RANGE_MAX);

            final int portRange = maxPort - minPort;
            int candidatePort;
            int searchCounter = 0;
            do {
                if (searchCounter > portRange) {
                    //CHECKSTYLE:OFF
                    throw new IllegalStateException(
                            String.format("Could not find an available %s port in the range [%d, %d] after %d attempts",
                                    name(), minPort, maxPort, searchCounter));
                    //CHECKSTYLE:ON
                }
                candidatePort = findRandomPort(minPort, maxPort);
                searchCounter++;
            } while (!isPortAvailable(candidatePort));

            return candidatePort;
        }

        /**
         * Find the requested number of available ports for this {@code SocketType}, each randomly selected from the
         * range [{@code minPort}, {@code maxPort}].
         *
         * @param numRequested
         *            the number of available ports to find
         * @param minPort
         *            the minimum port number
         * @param maxPort
         *            the maximum port number
         * @return a sorted set of available port numbers for this socket type
         * @throws IllegalStateException
         *             if the requested number of available ports could not be found
         */
        int[] findAvailablePorts(final int numRequested, final int minPort, final int maxPort) {
            Assertions.checkTrue(minPort > 0, "'minPort' must be greater than 0");
            Assertions.checkTrue(maxPort > minPort, "'maxPort' must be greater than 'minPort'");
            Assertions.checkTrue(maxPort <= PORT_RANGE_MAX,
                    "'maxPort' must be less than or equal to " + PORT_RANGE_MAX);
            Assertions.checkTrue(numRequested > 0, "'numRequested' must be greater than 0");
            Assertions.checkTrue((maxPort - minPort) >= numRequested,
                    "'numRequested' must not be greater than 'maxPort' - 'minPort'");

            final SortedSet<Integer> availablePorts = ILockCollectionFactory.getInstance(false).newTreeSet();
            int attemptCount = 0;
            while ((++attemptCount <= numRequested + 100) && availablePorts.size() < numRequested) {
                availablePorts.add(findAvailablePort(minPort, maxPort));
            }

            if (availablePorts.size() != numRequested) {
                //CHECKSTYLE:OFF
                throw new IllegalStateException(
                        String.format("Could not find %d available %s ports in the range [%d, %d]", numRequested,
                                name(), minPort, maxPort));
                //CHECKSTYLE:ON
            }

            return Integers.checkedCastVector(availablePorts);
        }
    }

}
