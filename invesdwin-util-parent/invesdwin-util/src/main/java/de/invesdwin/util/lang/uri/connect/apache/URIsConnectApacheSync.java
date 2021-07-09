package de.invesdwin.util.lang.uri.connect.apache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.ClassicHttpRequests;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.util.TimeValue;

import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.uri.Addresses;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.lang.uri.connect.IURIsConnect;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponse;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponseConsumer;
import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public final class URIsConnectApacheSync implements IURIsConnect {

    public static final int MAX_CONNECTIONS = URIsConnectApacheAsync.MAX_CONNECTIONS;
    public static final TimeValue EVICT_IDLE_CONNECTIONS_TIMEOUT = URIsConnectApacheAsync.EVICT_IDLE_CONNECTIONS_TIMEOUT;
    private static CloseableHttpClient httpClient;
    private static IShutdownHook shutdownHook;
    private static RequestConfig defaultRequestConfig = RequestConfig.DEFAULT;

    private final URI uri;
    private Duration networkTimeout = URIs.getDefaultNetworkTimeout();
    private Proxy proxy = null;

    private Map<String, String> headers;

    public URIsConnectApacheSync(final URI url) {
        this.uri = url;
    }

    public static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (URIsConnectApacheSync.class) {
                if (httpClient == null) {
                    final CloseableHttpClient client = HttpClientBuilder.create()
                            .useSystemProperties() //use system proxy etc
                            .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                                    .setMaxConnPerRoute(MAX_CONNECTIONS)
                                    .setMaxConnTotal(MAX_CONNECTIONS)
                                    .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.LAX)
                                    .build())
                            .evictExpiredConnections()
                            .evictIdleConnections(EVICT_IDLE_CONNECTIONS_TIMEOUT)
                            .build();
                    if (shutdownHook == null) {
                        shutdownHook = new IShutdownHook() {
                            @Override
                            public void shutdown() throws Exception {
                                resetHttpClient();
                            }
                        };
                        ShutdownHookManager.register(shutdownHook);
                    }
                    httpClient = client;
                }
            }
        }
        return httpClient;
    }

    public static void resetHttpClient() {
        synchronized (URIsConnectApacheSync.class) {
            if (httpClient != null) {
                httpClient.close(CloseMode.GRACEFUL);
                httpClient = null;
            }
        }
    }

    private static RequestConfig.Builder applyNetworkTimeout(final RequestConfig.Builder config,
            final Duration networkTimeout) {
        return config
                .setConnectionRequestTimeout(networkTimeout.longValue(), networkTimeout.getTimeUnit().timeUnitValue())
                .setConnectTimeout(networkTimeout.longValue(), networkTimeout.getTimeUnit().timeUnitValue())
                .setResponseTimeout(networkTimeout.longValue(), networkTimeout.getTimeUnit().timeUnitValue());
    }

    private static RequestConfig.Builder applyProxy(final RequestConfig.Builder config, final Proxy proxy) {
        final InetSocketAddress address = (InetSocketAddress) proxy.address();
        final HttpHost httpHost = new HttpHost(address.getHostName(), address.getPort());
        return config.setProxy(httpHost);
    }

    @Override
    public URIsConnectApacheSync withNetworkTimeout(final Duration networkTimeout) {
        this.networkTimeout = networkTimeout;
        return this;
    }

    @Override
    public Duration getNetworkTimeout() {
        return networkTimeout;
    }

    @Override
    public URIsConnectApacheSync withProxy(final Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public Proxy getProxy() {
        return proxy;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    /**
     * WARNING: connection pooling will share authentication between requests. If this is not desired, then use a
     * separate HttpClient where this information does not get shared!
     */
    @Override
    public URIsConnectApacheSync withBasicAuth(final String username, final String password) {
        final String authString = username + ":" + password;
        final byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        final String authStringEnc = new String(authEncBytes);
        withHeader(HttpHeaders.AUTHORIZATION, "Basic " + authStringEnc);
        return this;
    }

    @Override
    public URIsConnectApacheSync withHeader(final String key, final String value) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(key, value);
        return this;
    }

    @Override
    public boolean isServerResponding() {
        try {
            final Socket socket = new Socket();
            final int timeoutMillis = networkTimeout.intValue(FTimeUnit.MILLISECONDS);
            socket.setSoTimeout(timeoutMillis);
            socket.connect(Addresses.asAddress(uri.getHost(), uri.getPort()), timeoutMillis);
            socket.close();
            return true;
        } catch (final Throwable e) {
            return false;
        }
    }

    /**
     * Tries to open a connection to the specified url and checks if the content is available there.
     */
    @Override
    public boolean isDownloadPossible() {
        if (uri == null) {
            return false;
        }
        try (CloseableHttpResponse response = openConnection(HEAD)) {
            if (!URIs.isSuccessful(response.getCode())) {
                return false;
            }
            final String contentLengthStr = response.getFirstHeader(HttpHeaders.CONTENT_LENGTH).getValue();
            if (contentLengthStr != null) {
                final long contentLength = Long.parseLong(contentLengthStr);
                return contentLength > 0;
            } else {
                return true;
            }
        } catch (final Throwable e) {
            return false;
        }
    }

    @Override
    public long lastModified() {
        try (CloseableHttpResponse response = openConnection(HEAD)) {
            if (!URIs.isSuccessful(response.getCode())) {
                return -1;
            }
            final String lastModifiedStr = response.getFirstHeader(HttpHeaders.LAST_MODIFIED).getValue();
            if (lastModifiedStr == null) {
                return -1;
            }
            final Date lastModified = org.apache.hc.client5.http.utils.DateUtils.parseDate(lastModifiedStr);
            if (lastModified == null) {
                return -1;
            } else {
                return lastModified.getTime();
            }
        } catch (final Throwable e) {
            return -1;
        }
    }

    @Override
    public String download() {
        InputStream in = null;
        try {
            in = getInputStream();
            return IOUtils.toString(in, Charset.defaultCharset());
        } catch (final Throwable e) {
            return null;
        } finally {
            Closeables.closeQuietly(in);
        }
    }

    @Override
    public String downloadThrowing() throws IOException {
        InputStream in = null;
        try {
            in = getInputStream();
            final String response = IOUtils.toString(in, Charset.defaultCharset());
            if (response == null) {
                throw new IOException("response is null");
            }
            return response;
        } finally {
            Closeables.closeQuietly(in);
        }
    }

    public CloseableHttpResponse openConnection() throws IOException {
        return openConnection(GET);
    }

    public CloseableHttpResponse openConnection(final String method) throws IOException {
        final HttpUriRequestBase request = (HttpUriRequestBase) ClassicHttpRequests.create(method, uri);
        request.setConfig(getRequestConfig());
        if (headers != null) {
            for (final Entry<String, String> header : headers.entrySet()) {
                request.addHeader(header.getKey(), header.getValue());
            }
        }

        final CloseableHttpResponse response = getHttpClient().execute(request);
        return response;
    }

    private RequestConfig getRequestConfig() {
        if (networkTimeout != URIs.getDefaultNetworkTimeout() || proxy != null) {
            RequestConfig.Builder builder = RequestConfig.copy(defaultRequestConfig);
            if (networkTimeout != URIs.getDefaultNetworkTimeout()) {
                builder = applyNetworkTimeout(builder, networkTimeout);
            }
            if (proxy != null) {
                builder = applyProxy(builder, proxy);
            }
            return builder.build();
        } else {
            return defaultRequestConfig;
        }
    }

    public InputStreamHttpResponse getInputStream(final String method) throws IOException {
        return getInputStream(openConnection(method), uri);
    }

    @Override
    public InputStreamHttpResponse getInputStream() throws IOException {
        return getInputStream(openConnection(), uri);
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    public static InputStreamHttpResponse getInputStream(final CloseableHttpResponse response, final URI uri)
            throws IOException {
        try {
            // https://stackoverflow.com/questions/613307/read-error-response-body-in-java
            final int responseCode = response.getCode();
            if (URIs.isSuccessful(responseCode)) {
                final InputStreamHttpResponseConsumer consumer = new InputStreamHttpResponseConsumer();
                consumer.start(new HttpResponseApache(response));
                consumer.data(response.getEntity().getContent());
                final InputStreamHttpResponse result = consumer.buildResult();
                consumer.releaseResources();
                return result;
            } else {
                if (responseCode == HttpURLConnection.HTTP_NOT_FOUND || responseCode == HttpURLConnection.HTTP_GONE) {
                    throw new FileNotFoundException(uri.toString());
                } else {
                    if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                        final String errorStr = IOUtils.toString(response.getEntity().getContent(),
                                Charset.defaultCharset());
                        throw new IOException("Server returned HTTP response code [" + responseCode + "] for URL ["
                                + uri.toString() + "] with error:\n*****************************\n"
                                + Strings.putSuffix(errorStr, "\n") + "*****************************");
                    } else {
                        throw new IOException("Server returned HTTP response code [" + responseCode + "] for URL ["
                                + uri.toString() + "]");
                    }
                }
            }
        } finally {
            response.close();
        }
    }

}
