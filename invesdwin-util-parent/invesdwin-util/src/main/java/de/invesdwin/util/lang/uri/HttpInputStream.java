package de.invesdwin.util.lang.uri;

import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.hc.core5.http.HttpResponse;

import de.invesdwin.util.streams.ASimpleDelegateInputStream;

@NotThreadSafe
public class HttpInputStream extends ASimpleDelegateInputStream {

    private final HttpResponse response;

    public HttpInputStream(final HttpResponse response, final InputStream delegate) {
        super(delegate);
        this.response = response;
    }

    public HttpResponse getResponse() {
        return response;
    }

}
