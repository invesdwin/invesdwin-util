package de.invesdwin.util.lang.uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import de.invesdwin.util.lang.Closeables;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@NotThreadSafe
public final class URIsConnect {

    private static Duration defaultNetworkTimeout = new Duration(30, FTimeUnit.SECONDS);
    private final URL url;
    private Duration networkTimeout = defaultNetworkTimeout;

    private Map<String, String> headers;

    //package private
    URIsConnect(final URL url) {
        this.url = url;
    }

    public static void setDefaultNetworkTimeout(final Duration defaultNetworkTimeout) {
        URIsConnect.defaultNetworkTimeout = defaultNetworkTimeout;
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
     * 
     * TODO: There seems to be a openjdk bug when the wlan module gets reloaded. It seems this causes dns information to
     * get lost and causes UnknownHostExceptions here.
     */
    public boolean isDownloadPossible() {
        if (url == null) {
            return false;
        }
        try {
            final URLConnection con = openConnection();
            return con.getInputStream().available() > 0;
        } catch (final Throwable e) {
            return false;
        }
    }

    public long lastModified() {
        try {
            final URLConnection con = openConnection();
            return con.getLastModified();
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

    public URLConnection openConnection() throws IOException {
        final URLConnection con = url.openConnection();
        con.setUseCaches(false);
        con.setConnectTimeout(networkTimeout.intValue(FTimeUnit.MILLISECONDS));
        con.setReadTimeout(networkTimeout.intValue(FTimeUnit.MILLISECONDS));
        if (headers != null) {
            for (final Entry<String, String> header : headers.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        return con;
    }

    public InputStream getInputStream() throws IOException {
        return getInputStream(openConnection());
    }

    public static InputStream getInputStream(final URLConnection con) throws IOException {
        if (con instanceof HttpURLConnection) {
            final HttpURLConnection cCon = (HttpURLConnection) con;
            // https://stackoverflow.com/questions/613307/read-error-response-body-in-java
            final int respCode = cCon.getResponseCode();
            if (respCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                if (respCode == HttpURLConnection.HTTP_NOT_FOUND || respCode == HttpURLConnection.HTTP_GONE) {
                    throw new FileNotFoundException(con.getURL().toString());
                } else {
                    if (respCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                        final InputStream error = cCon.getErrorStream();
                        final String errorStr = IOUtils.toString(error, Charset.defaultCharset());
                        throw new java.io.IOException("Server returned HTTP" + " response code: " + respCode
                                + " for URL: " + con.getURL().toString() + " error response:"
                                + "\n*****************************" + errorStr + "*****************************");
                    } else {
                        throw new java.io.IOException("Server returned HTTP" + " response code: " + respCode
                                + " for URL: " + con.getURL().toString());
                    }
                }
            }
        }
        return con.getInputStream();
    }

    @Override
    public String toString() {
        return url.toString();
    }

}
