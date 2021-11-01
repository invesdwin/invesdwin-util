package de.invesdwin.util.lang.uri.connect;

import java.io.IOException;
import java.net.Proxy;
import java.net.URI;

import de.invesdwin.util.time.duration.Duration;

public interface IURIsConnect {

    String HEAD = "HEAD";
    String GET = "GET";

    IURIsConnect setNetworkTimeout(Duration networkTimeout);

    Duration getNetworkTimeout();

    IURIsConnect setProxy(Proxy proxy);

    Proxy getProxy();

    URI getUri();

    IURIsConnect addBasicAuth(String username, String password);

    IURIsConnect addHeader(String key, String value);

    long lastModified();

    String download();

    String downloadThrowing() throws IOException;

    boolean isServerResponding();

    boolean isDownloadPossible();

    InputStreamHttpResponse getInputStream() throws IOException;

}
