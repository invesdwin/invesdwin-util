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

import org.apache.commons.io.IOUtils;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.lang.uri.Addresses;
import de.invesdwin.util.lang.uri.URIs;
import de.invesdwin.util.lang.uri.connect.IURIsConnect;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponse;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponseConsumer;
import de.invesdwin.util.lang.uri.header.BasicAuth;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

@NotThreadSafe
public final class URIsConnectOkHttp implements IURIsConnect {

    private static OkHttpClient httpClient;

    private final URL url;
    private Duration networkTimeout = URIs.getDefaultNetworkTimeout();
    private Proxy proxy;
    private String method = GET;
    private byte[] body;
    private String contentType;

    private Map<String, String> headers;

    public URIsConnectOkHttp(final URL url) {
        this.url = url;
    }

    public URIsConnectOkHttp(final URI url) {
        this.url = URIs.asUrl(url);
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public IURIsConnect setMethod(final String method) {
        this.method = method;
        return this;
    }

    @Override
    public IURIsConnect setBody(final byte[] body) {
        if (body != null && body.length == 0) {
            return this;
        }
        this.body = body;
        return this;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public IURIsConnect setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
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
    public URIsConnectOkHttp setNetworkTimeout(final Duration networkTimeout) {
        this.networkTimeout = networkTimeout;
        return this;
    }

    @Override
    public Duration getNetworkTimeout() {
        return networkTimeout;
    }

    @Override
    public URIsConnectOkHttp setProxy(final Proxy proxy) {
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
    public URIsConnectOkHttp putBasicAuth(final String username, final String password) {
        putHeader(BasicAuth.HEADER, BasicAuth.encode(username, password));
        return this;
    }

    @Override
    public URIsConnectOkHttp putHeader(final String key, final String value) {
        if (key == null || value == null) {
            return this;
        }
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(key, value);
        return this;
    }

    @Override
    public Map<String, String> getHeaders() {
        if (headers == null) {
            return Collections.emptyMap();
        }
        return headers;
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
        final Call con = openConnection("HEAD");
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
        final Call con = openConnection("HEAD");
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
            in = downloadInputStream();
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
            in = downloadInputStream();
            final String response = IOUtils.toString(in, Charset.defaultCharset());
            if (response == null) {
                throw new IOException("response is null");
            }
            return response;
        } finally {
            Closeables.closeQuietly(in);
        }
    }

    @Override
    public boolean upload() {
        try {
            return uploadThrowing();
        } catch (final Throwable e) {
            //noop
            return false;
        }
    }

    @Override
    public boolean uploadThrowing() throws IOException {
        return upload(openConnection(method));
    }

    public Call openConnection() {
        return openConnection(method);
    }

    public Call openConnection(final String method) {
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
        final Builder requestBuilder = new Request.Builder().url(url);

        final RequestBody requestBody;
        if (body != null) {
            final MediaType mediaType;
            if (contentType != null) {
                mediaType = MediaType.parse(contentType);
            } else {
                mediaType = null;
            }
            requestBody = RequestBody.create(body, mediaType);
        } else {
            requestBody = null;
        }

        requestBuilder.method(method, requestBody);
        if (headers != null) {
            for (final Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }
        final Request request = requestBuilder.build();
        return client.newCall(request);
    }

    public InputStreamHttpResponse getInputStream(final String method) throws IOException {
        return downloadInputStream(openConnection(method));
    }

    @Override
    public InputStreamHttpResponse downloadInputStream() throws IOException {
        return downloadInputStream(openConnection());
    }

    @Override
    public String toString() {
        return url.toString();
    }

    public static InputStreamHttpResponse downloadInputStream(final Call call) throws IOException {
        try (Response response = call.execute()) {
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

    public static boolean upload(final Call call) throws IOException {
        try (Response response = call.execute()) {
            // https://stackoverflow.com/questions/613307/read-error-response-body-in-java
            if (response.isSuccessful()) {
                return true;
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

}
