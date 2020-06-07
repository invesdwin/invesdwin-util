package de.invesdwin.util.lang.uri.connect.okhttp;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.uri.Addresses;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.lang.uri.connect.IHttpRequest;
import de.invesdwin.util.lang.uri.connect.IURIsConnect;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponse;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponseConsumer;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

@NotThreadSafe
public final class URIsConnectOkHttp implements IURIsConnect {

    private static Duration defaultNetworkTimeout = new Duration(30, FTimeUnit.SECONDS);
    private static Proxy defaultProxy = null;
    private static OkHttpClient httpClient;

    private final URL url;
    private Duration networkTimeout = defaultNetworkTimeout;
    private Proxy proxy = defaultProxy;

    private Map<String, String> headers;

    public URIsConnectOkHttp(final URL url) {
        this.url = url;
    }

    public URIsConnectOkHttp(final URI url) {
        this.url = URIs.asUrl(url);
    }

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (URIsConnectOkHttp.class) {
                if (httpClient == null) {
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder = applyNetworkTimeout(builder, defaultNetworkTimeout);
                    builder = applyProxy(builder, defaultProxy);
                    httpClient = builder.build();
                }
            }
        }
        return httpClient;
    }

    public static void setDefaultNetworkTimeout(final Duration defaultNetworkTimeout) {
        URIsConnectOkHttp.defaultNetworkTimeout = defaultNetworkTimeout;
        //create derived instances to share connections etc: https://github.com/square/okhttp/issues/3372
        if (httpClient != null) {
            httpClient = applyNetworkTimeout(httpClient.newBuilder(), defaultNetworkTimeout).build();
        }
    }

    private static OkHttpClient.Builder applyNetworkTimeout(final OkHttpClient.Builder builder,
            final Duration networkTimeout) {
        return builder.connectTimeout(networkTimeout.intValue(), networkTimeout.getTimeUnit().timeUnitValue())
                .readTimeout(networkTimeout.intValue(), networkTimeout.getTimeUnit().timeUnitValue())
                .writeTimeout(networkTimeout.intValue(), networkTimeout.getTimeUnit().timeUnitValue());
    }

    public static Duration getDefaultNetworkTimeout() {
        return defaultNetworkTimeout;
    }

    public static void setDefaultProxy(final Proxy defaultProxy) {
        URIsConnectOkHttp.defaultProxy = defaultProxy;
        //create derived instances to share connections etc: https://github.com/square/okhttp/issues/3372
        if (httpClient != null) {
            httpClient = applyProxy(httpClient.newBuilder(), defaultProxy).build();
        }
    }

    private static OkHttpClient.Builder applyProxy(final OkHttpClient.Builder builder, final Proxy proxy) {
        if (proxy != null) {
            return builder.proxy(proxy);
        } else {
            return builder;
        }
    }

    public static Proxy getDefaultProxy() {
        return defaultProxy;
    }

    @Override
    public URIsConnectOkHttp withNetworkTimeout(final Duration networkTimeout) {
        this.networkTimeout = networkTimeout;
        return this;
    }

    @Override
    public Duration getNetworkTimeout() {
        return networkTimeout;
    }

    @Override
    public URIsConnectOkHttp withProxy(final Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public Proxy getProxy() {
        return proxy;
    }

    @Override
    public URI getUri() {
        return URIs.asUri(url);
    }

    @Override
    public URIsConnectOkHttp withBasicAuth(final String username, final String password) {
        final String authString = username + ":" + password;
        final byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        final String authStringEnc = new String(authEncBytes);
        withHeader("Authorization", "Basic " + authStringEnc);
        return this;
    }

    @Override
    public URIsConnectOkHttp withHeader(final String key, final String value) {
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
            socket.connect(Addresses.asAddress(url.getHost(), url.getPort()), timeoutMillis);
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
        if (url == null) {
            return false;
        }
        final Call con = openConnection(IHttpRequest.HEAD);
        try (Response response = con.execute()) {
            if (!response.isSuccessful()) {
                return false;
            }
            final String contentLength = response.headers().get("content-length");
            if (contentLength != null) {
                return Long.parseLong(contentLength) > 0;
            } else {
                return true;
            }
        } catch (final Throwable e) {
            return false;
        }
    }

    @Override
    public long lastModified() {
        final Call con = openConnection(IHttpRequest.HEAD);
        try (Response response = con.execute()) {
            if (!response.isSuccessful()) {
                return -1;
            }
            final Date lastModified = response.headers().getDate("last-modified");
            if (lastModified == null) {
                return -1;
            } else {
                return lastModified.getTime();
            }
        } catch (final IOException e) {
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

    public Call openConnection() {
        return openConnection(null);
    }

    public Call openConnection(final IHttpRequest customizer) {
        final OkHttpClient client;
        if (networkTimeout != defaultNetworkTimeout || proxy != defaultProxy) {
            OkHttpClient.Builder builder = getHttpClient().newBuilder();
            if (networkTimeout != defaultNetworkTimeout) {
                builder = applyNetworkTimeout(builder, networkTimeout);
            }
            if (proxy != defaultProxy) {
                builder = applyProxy(builder, proxy);
            }
            client = builder.build();
        } else {
            client = getHttpClient();
        }
        Builder requestBuilder = new Request.Builder().url(url);
        if (customizer != null) {
            requestBuilder = customizer.customize(requestBuilder);
        }
        if (headers != null) {
            for (final Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }
        final Request request = requestBuilder.build();
        return client.newCall(request);
    }

    @Override
    public InputStreamHttpResponse getInputStream() throws IOException {
        return InputStreamHttpResponseConsumer.getInputStream(openConnection());
    }

    @Override
    public String toString() {
        return url.toString();
    }

}
