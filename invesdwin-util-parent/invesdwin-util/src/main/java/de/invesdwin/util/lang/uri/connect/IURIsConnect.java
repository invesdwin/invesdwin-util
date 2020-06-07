package de.invesdwin.util.lang.uri.connect;

import java.io.IOException;
import java.net.Proxy;
import java.net.URI;

import de.invesdwin.util.time.duration.Duration;

public interface IURIsConnect {

    String HEAD = "HEAD";
    String GET = "GET";

    IURIsConnect withNetworkTimeout(Duration networkTimeout);

    Duration getNetworkTimeout();

    IURIsConnect withProxy(Proxy proxy);

    Proxy getProxy();

    URI getUri();

    IURIsConnect withBasicAuth(String username, String password);

    IURIsConnect withHeader(String key, String value);

    long lastModified();

    String download();

    String downloadThrowing() throws IOException;

    boolean isServerResponding();

    boolean isDownloadPossible();

    InputStreamHttpResponse getInputStream() throws IOException;

}
