package de.invesdwin.util.lang.uri;

import java.io.IOException;
import java.io.InputStream;
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
            socket.connect(Addresses.asAddress(url.getHost(), url.getPort()),
                    networkTimeout.intValue(FTimeUnit.MILLISECONDS));
            socket.close();
            return true;
        } catch (final IOException e) {
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
        } catch (final IOException e) {
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
            final URLConnection con = openConnection();
            in = con.getInputStream();
            return IOUtils.toString(in, Charset.defaultCharset());
        } catch (final IOException e) {
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public String downloadThrowing() throws IOException {
        InputStream in = null;
        try {
            final URLConnection con = openConnection();
            in = con.getInputStream();
            final String response = IOUtils.toString(in, Charset.defaultCharset());
            if (response == null) {
                throw new IOException("response is null");
            }
            return response;
        } finally {
            IOUtils.closeQuietly(in);
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

    @Override
    public String toString() {
        return url.toString();
    }

}
