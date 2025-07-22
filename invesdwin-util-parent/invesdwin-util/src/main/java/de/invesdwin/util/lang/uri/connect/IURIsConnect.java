package de.invesdwin.util.lang.uri.connect;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URI;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;

import de.invesdwin.util.time.duration.Duration;

public interface IURIsConnect {

    String HEAD = "HEAD";
    String GET = "GET";
    String POST = "POST";
    String PUT = "PUT";
    String DELETE = "DELETE";
    String PATCH = "PATCH";

    IURIsConnect setNetworkTimeout(Duration networkTimeout);

    Duration getNetworkTimeout();

    IURIsConnect setProxy(Proxy proxy);

    Proxy getProxy();

    IURIsConnect setTrustManager(X509TrustManager trustManager);

    X509TrustManager getTrustManager();

    IURIsConnect setHostnameVerifier(HostnameVerifier hostnameVerifier);

    HostnameVerifier getHostnameVerifier();

    URI getUri();

    IURIsConnect putBasicAuth(String username, String password);

    IURIsConnect putHeader(String key, String value);

    Map<String, String> getHeaders();

    default IURIsConnect setBody(final String body) {
        return setBody(body.getBytes());
    }

    default IURIsConnect setBody(final InputStream body) {
        final byte[] bytes;
        try {
            bytes = IOUtils.toByteArray(body);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return setBody(bytes);
    }

    IURIsConnect setBody(byte[] body);

    byte[] getBody();

    IURIsConnect setContentType(String contentType);

    String getContentType();

    IURIsConnect setMethod(String method);

    String getMethod();

    boolean isServerResponding();

    boolean isDownloadPossible();

    long lastModified();

    boolean upload();

    boolean uploadThrowing() throws IOException;

    String download();

    String downloadThrowing() throws IOException;

    InputStreamHttpResponse downloadInputStream() throws IOException;

}
