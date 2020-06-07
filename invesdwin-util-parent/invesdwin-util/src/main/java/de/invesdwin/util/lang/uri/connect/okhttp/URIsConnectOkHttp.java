package de.invesdwin.util.lang.uri.connect.okhttp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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

    private static OkHttpClient httpClient;

    private final URL url;
    private Duration networkTimeout = URIs.getDefaultNetworkTimeout();
    private Proxy proxy;

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
                    builder = applyNetworkTimeout(builder, URIs.getDefaultNetworkTimeout());
                    builder = applyProxy(builder, URIs.getSystemProxy());
                    httpClient = builder.build();
                }
            }
        }
        return httpClient;
    }

    private static OkHttpClient.Builder applyNetworkTimeout(final OkHttpClient.Builder builder,
            final Duration networkTimeout) {
        return builder.connectTimeout(networkTimeout.intValue(), networkTimeout.getTimeUnit().timeUnitValue())
                .readTimeout(networkTimeout.intValue(), networkTimeout.getTimeUnit().timeUnitValue())
                .writeTimeout(networkTimeout.intValue(), networkTimeout.getTimeUnit().timeUnitValue());
    }

    private static OkHttpClient.Builder applyProxy(final OkHttpClient.Builder builder, final Proxy proxy) {
        if (proxy != null) {
            return builder.proxy(proxy);
        } else {
            return builder;
        }
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
        if (networkTimeout != URIs.getDefaultNetworkTimeout() || proxy != null) {
            OkHttpClient.Builder builder = getHttpClient().newBuilder();
            if (networkTimeout != URIs.getDefaultNetworkTimeout()) {
                builder = applyNetworkTimeout(builder, networkTimeout);
            }
            if (proxy != null) {
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
        return getInputStream(openConnection());
    }

    @Override
    public String toString() {
        return url.toString();
    }

    public static InputStreamHttpResponse getInputStream(final Call call) throws IOException {
        final Response response = call.execute();
        // https://stackoverflow.com/questions/613307/read-error-response-body-in-java
        if (response.isSuccessful()) {
            final InputStreamHttpResponseConsumer consumer = new InputStreamHttpResponseConsumer();
            consumer.start(new HttpResponseOkHttp(response));
            consumer.data(response.body().byteStream());
            final InputStreamHttpResponse result = consumer.buildResult();
            consumer.releaseResources();
            return result;
        } else {
            final int respCode = response.code();
            final String urlString = call.request().url().toString();
            if (respCode == HttpURLConnection.HTTP_NOT_FOUND || respCode == HttpURLConnection.HTTP_GONE) {
                throw new FileNotFoundException(urlString);
            } else {
                if (respCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    final String errorStr = response.body().string();
                    throw new IOException("Server returned HTTP" + " response code: " + respCode + " for URL: "
                            + urlString + " error response:" + "\n*****************************" + errorStr
                            + "*****************************");
                } else {
                    throw new IOException(
                            "Server returned HTTP" + " response code: " + respCode + " for URL: " + urlString);
                }
            }
        }
    }

}
