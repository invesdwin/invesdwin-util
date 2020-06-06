package de.invesdwin.util.lang.uri;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.hc.core5.http.HttpResponse;

@NotThreadSafe
public class HttpInputStream extends InputStream {

    private final HttpResponse response;
    private final InputStream delegate;

    public HttpInputStream(final HttpResponse response, final InputStream delegate) {
        this.response = response;
        this.delegate = delegate;
    }

    public HttpResponse getResponse() {
        return response;
    }

    @Override
    public int read() throws IOException {
        return delegate.read();
    }

}
