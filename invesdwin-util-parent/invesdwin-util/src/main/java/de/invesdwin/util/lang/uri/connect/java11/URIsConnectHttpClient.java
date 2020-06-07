package de.invesdwin.util.lang.uri.connect.java11;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
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
import de.invesdwin.util.lang.uri.connect.IURIsConnect;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponse;
import de.invesdwin.util.lang.uri.connect.InputStreamHttpResponseConsumer;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public final class URIsConnectHttpClient implements IURIsConnect {

    private final URI uri;
    private Duration networkTimeout = URIs.getDefaultNetworkTimeout();
    private Proxy proxy;

    private Map<String, String> headers;

    public URIsConnectHttpClient(final URI uri) {
        this.uri = uri;
    }

    @Override
    public URIsConnectHttpClient withNetworkTimeout(final Duration networkTimeout) {
        this.networkTimeout = networkTimeout;
        return this;
    }

    @Override
    public Duration getNetworkTimeout() {
        return networkTimeout;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public URIsConnectHttpClient withBasicAuth(final String username, final String password) {
        final String authString = username + ":" + password;
        final byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        final String authStringEnc = new String(authEncBytes);
        withHeader("Authorization", "Basic " + authStringEnc);
        return this;
    }

    @Override
    public URIsConnectHttpClient withHeader(final String key, final String value) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(key, value);
        return this;
    }

    @Override
    public URIsConnectHttpClient withProxy(final Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public Proxy getProxy() {
        return proxy;
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
        try {
            final HttpResponse<Void> response = openConnection("HEAD", BodyHandlers.discarding());
            if (!URIs.isSuccessful(response.statusCode())) {
                return false;
            }
            final String contentLength = response.headers().firstValue("content-length").orElse(null);
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
        try {
            final HttpResponse<Void> response = openConnection("HEAD", BodyHandlers.discarding());
            if (!URIs.isSuccessful(response.statusCode())) {
                return -1;
            }
            final String lastModifiedStr = response.headers().firstValue("last-modified").orElse(null);
            if (lastModifiedStr == null) {
                return -1;
            }
            final Date lastModified = org.apache.hc.client5.http.utils.DateUtils.parseDate(lastModifiedStr);
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

    public <T> HttpResponse<T> openConnection(final String method, final BodyHandler<T> bodyHandler)
            throws IOException {
        final HttpClient.Builder clientBuilder = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .connectTimeout(networkTimeout.javaTimeValue());
        if (proxy != null) {
            final InetSocketAddress address = (InetSocketAddress) proxy.address();
            clientBuilder.proxy(ProxySelector.of(address));
        }
        final HttpClient client = clientBuilder.build();
        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(uri);
        requestBuilder.method(method, BodyPublishers.noBody());
        if (headers != null) {
            for (final Entry<String, String> header : headers.entrySet()) {
                requestBuilder.setHeader(header.getKey(), header.getValue());
            }
        }
        requestBuilder.timeout(networkTimeout.javaTimeValue());

        final HttpRequest request = requestBuilder.build();

        try {
            return client.send(request, bodyHandler);
        } catch (final InterruptedException e) {
            throw new IOException(e);
        }
    }

    public HttpResponse<InputStream> openConnection() throws IOException {
        return openConnection(GET, BodyHandlers.ofInputStream());
    }

    @Override
    public InputStreamHttpResponse getInputStream() throws IOException {
        return getInputStream(openConnection());
    }

    public static InputStreamHttpResponse getInputStream(final HttpResponse<InputStream> response) throws IOException {
        final int respCode = response.statusCode();
        if (URIs.isSuccessful(respCode)) {
            final InputStreamHttpResponseConsumer consumer = new InputStreamHttpResponseConsumer();
            consumer.start(new HttpResponseHttpClient(response));
            consumer.data(response.body());
            final InputStreamHttpResponse result = consumer.buildResult();
            consumer.releaseResources();
            return result;
        } else {
            final String urlString = response.uri().toString();
            if (respCode == HttpURLConnection.HTTP_NOT_FOUND || respCode == HttpURLConnection.HTTP_GONE) {
                throw new FileNotFoundException(urlString);
            } else {
                if (respCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    final String errorStr = IOUtils.toString(response.body(), Charset.defaultCharset());
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

    @Override
    public String toString() {
        return uri.toString();
    }

}
