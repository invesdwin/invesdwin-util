package de.invesdwin.util.lang.uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.Socket;
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
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

@NotThreadSafe
public final class URIsConnect {

    private static Duration defaultNetworkTimeout = new Duration(30, FTimeUnit.SECONDS);
    private static Proxy defaultProxy = getSystemProxy();
    private static OkHttpClient sharedClient = applyProxy(
            applyNetworkTimeout(new OkHttpClient.Builder(), defaultNetworkTimeout), defaultProxy).build();

    private final URL url;
    private Duration networkTimeout = defaultNetworkTimeout;
    private Proxy proxy = defaultProxy;

    private Map<String, String> headers;

    //package private
    URIsConnect(final URL url) {
        this.url = url;
    }

    public static OkHttpClient getHttpClient() {
        return sharedClient;
    }

    public static void setDefaultNetworkTimeout(final Duration defaultNetworkTimeout) {
        URIsConnect.defaultNetworkTimeout = defaultNetworkTimeout;
        //create derived instances to share connections etc: https://github.com/square/okhttp/issues/3372
        sharedClient = applyNetworkTimeout(sharedClient.newBuilder(), defaultNetworkTimeout).build();
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

    public URIsConnect withProxy(final Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public URL getUrl() {
        return url;
    }

    public URIsConnect withBasicAuth(final String username, final String password) {
        final String authString = username + ":" + password;
        final byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        final String authStringEnc = new String(authEncBytes);
        putHeader("Authorization", "Basic " + authStringEnc);
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
    public boolean isDownloadPossible() {
        if (url == null) {
            return false;
        }
        final Call con = openConnection(new IRequestCustomizer() {
            @Override
            public Builder customize(final Builder request) {
                return request.head();
            }
        });
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

    public long lastModified() {
        final Call con = openConnection(new IRequestCustomizer() {
            @Override
            public Builder customize(final Builder request) {
                return request.head();
            }
        });
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

    public Call openConnection() {
        return openConnection(null);
    }

    public Call openConnection(final IRequestCustomizer customizer) {
        final OkHttpClient client;
        if (networkTimeout != defaultNetworkTimeout || proxy != defaultProxy) {
            OkHttpClient.Builder builder = sharedClient.newBuilder();
            if (networkTimeout != defaultNetworkTimeout) {
                builder = applyNetworkTimeout(builder, networkTimeout);
            }
            if (proxy != defaultProxy) {
                builder = applyProxy(builder, proxy);
            }
            client = builder.build();
        } else {
            client = sharedClient;
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

    public InputStream getInputStream() throws IOException {
        return getInputStream(openConnection());
    }

    public static InputStream getInputStream(final Call call) throws IOException {
        final Response response = call.execute();
        // https://stackoverflow.com/questions/613307/read-error-response-body-in-java
        if (response.isSuccessful()) {
            return response.body().byteStream();
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

    @Override
    public String toString() {
        return url.toString();
    }

    public static Proxy getSystemProxy() {
        //CHECKSTYLE:OFF
        final String httpProxyHost = System.getProperty("http.proxyHost");
        final String httpProxyPort = System.getProperty("http.proxyPort");
        //CHECKSTYLE:ON
        if (httpProxyHost != null && httpProxyPort != null) {
            final int port = Integer.parseInt(httpProxyPort);
            return new Proxy(Type.HTTP, Addresses.asAddress(httpProxyHost, port));
        } else {
            return null;
        }
    }

}
