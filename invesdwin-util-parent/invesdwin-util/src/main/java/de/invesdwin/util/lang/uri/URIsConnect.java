package de.invesdwin.util.lang.uri;

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
import java.util.concurrent.Future;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.io.CloseMode;

import de.invesdwin.util.concurrent.future.Futures;
import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.shutdown.IShutdownHook;
import de.invesdwin.util.shutdown.ShutdownHookManager;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public final class URIsConnect {

    private static Duration defaultNetworkTimeout = new Duration(30, FTimeUnit.SECONDS);
    private static Proxy defaultProxy = null;
    private static CloseableHttpAsyncClient httpClient;
    private static IShutdownHook shutdownHook;
    private static RequestConfig defaultRequestConfig = RequestConfig.DEFAULT;

    private final URI uri;
    private Duration networkTimeout = defaultNetworkTimeout;
    private Proxy proxy = null;

    private Map<String, String> headers;

    //package private
    URIsConnect(final URI url) {
        this.uri = url;
    }

    public static CloseableHttpAsyncClient getHttpClient() {
        if (httpClient == null) {
            synchronized (URIsConnect.class) {
                if (httpClient == null) {
                    final CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create()
                            .useSystemProperties() //use system proxy etc
                            .build();
                    client.start();
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
        synchronized (URIsConnect.class) {
            if (httpClient != null) {
                httpClient.close(CloseMode.GRACEFUL);
                httpClient = null;
            }
        }
    }

    public static void setDefaultNetworkTimeout(final Duration defaultNetworkTimeout) {
        URIsConnect.defaultNetworkTimeout = defaultNetworkTimeout;
        //create derived instances to share connections etc: https://github.com/square/okhttp/issues/3372
        defaultRequestConfig = applyNetworkTimeout(RequestConfig.copy(defaultRequestConfig), defaultNetworkTimeout)
                .build();
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

    public static Duration getDefaultNetworkTimeout() {
        return defaultNetworkTimeout;
    }

    public URIsConnect withNetworkTimeout(final Duration networkTimeout) {
        this.networkTimeout = networkTimeout;
        return this;
    }

    public Duration getNetworkTimeout() {
        return networkTimeout;
    }

    public void withProxy(final Proxy proxy) {
        this.proxy = proxy;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public URI getUri() {
        return uri;
    }

    /**
     * WARNING: connection pooling will share authentication between requests. If this is not desired, then use a
     * separate HttpClient where this information does not get shared!
     */
    public URIsConnect withBasicAuth(final String username, final String password) {
        final String authString = username + ":" + password;
        final byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        final String authStringEnc = new String(authEncBytes);
        putHeader(HttpHeaders.AUTHORIZATION, "Basic " + authStringEnc);
        return this;
    }

    private void putHeader(final String key, final String value) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(key, value);
    }

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
    public boolean isDownloadPossible() {
        if (uri == null) {
            return false;
        }
        final Future<SimpleHttpResponse> future = openConnection(IHttpRequestSettings.HEAD,
                SimpleResponseConsumer.create());
        try {
            final SimpleHttpResponse response = Futures.get(future);
            if (!isSuccessful(response)) {
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

    public long lastModified() {
        final Future<SimpleHttpResponse> future = openConnection(IHttpRequestSettings.HEAD,
                SimpleResponseConsumer.create());
        try {
            final SimpleHttpResponse response = Futures.get(future);
            if (!isSuccessful(response)) {
                return -1;
            }
            final String lastModifiedStr = response.getFirstHeader(HttpHeaders.LAST_MODIFIED).getValue();
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

    public Future<InputStreamHttpResponse> openConnection() {
        return openConnection(IHttpRequestSettings.GET, new InputStreamHttpResponseConsumer(), null);
    }

    public Future<InputStreamHttpResponse> openConnection(final IHttpRequestSettings settings) {
        return openConnection(settings, new InputStreamHttpResponseConsumer(), null);
    }

    public <T> Future<T> openConnection(final IHttpRequestSettings settings,
            final AsyncResponseConsumer<T> responseConsumer) {
        return openConnection(IHttpRequestSettings.GET, responseConsumer, null);
    }

    public <T> Future<T> openConnection(final IHttpRequestSettings settings,
            final AsyncResponseConsumer<T> responseConsumer, final FutureCallback<T> callback) {
        final SimpleHttpRequest request = SimpleHttpRequests.create(settings.getMethod(), uri);
        request.setConfig(getRequestConfig());
        if (headers != null) {
            for (final Entry<String, String> header : headers.entrySet()) {
                request.addHeader(header.getKey(), header.getValue());
            }
        }

        final SimpleRequestProducer requestProducer = SimpleRequestProducer.create(request);
        final Future<T> response = getHttpClient().execute(requestProducer, responseConsumer, callback);
        return response;
    }

    private RequestConfig getRequestConfig() {
        if (networkTimeout != defaultNetworkTimeout || proxy != defaultProxy) {
            RequestConfig.Builder builder = RequestConfig.copy(defaultRequestConfig);
            if (networkTimeout != defaultNetworkTimeout) {
                builder = applyNetworkTimeout(builder, networkTimeout);
            }
            if (proxy != defaultProxy) {
                builder = applyProxy(builder, proxy);
            }
            return builder.build();
        } else {
            return defaultRequestConfig;
        }
    }

    public InputStreamHttpResponse getInputStream() throws IOException {
        return getInputStream(openConnection(), uri);
    }

    public static InputStreamHttpResponse getInputStream(final Future<InputStreamHttpResponse> call, final URI uri)
            throws IOException {
        final InputStreamHttpResponse response;
        try {
            response = Futures.get(call);
        } catch (final InterruptedException e) {
            throw new IOException(e);
        }
        // https://stackoverflow.com/questions/613307/read-error-response-body-in-java
        final int responseCode = response.getResponse().getCode();
        if (isSuccessful(responseCode)) {
            return response;
        } else {
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND || responseCode == HttpURLConnection.HTTP_GONE) {
                throw new FileNotFoundException(uri.toString());
            } else {
                if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    final String errorStr = IOUtils.toString(response, Charset.defaultCharset());
                    throw new IOException("Server returned HTTP response code [" + responseCode + "] for URL ["
                            + uri.toString() + "] with error:\n*****************************\n"
                            + Strings.putSuffix(errorStr, "\n") + "*****************************");
                } else {
                    throw new IOException("Server returned HTTP response code [" + responseCode + "] for URL ["
                            + uri.toString() + "]");
                }
            }
        }
    }

    public static boolean isSuccessful(final HttpResponse response) {
        return isSuccessful(response.getCode());
    }

    public static boolean isSuccessful(final int responseCode) {
        return responseCode >= 200 && responseCode <= 299;
    }

    @Override
    public String toString() {
        return uri.toString();
    }

}
