package de.invesdwin.util.lang.uri.connect;

import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import de.invesdwin.util.streams.ASimpleDelegateInputStream;

@NotThreadSafe
public class InputStreamHttpResponse extends ASimpleDelegateInputStream {

    private final IHttpResponse response;

    public InputStreamHttpResponse(final IHttpResponse response, final InputStream delegate) {
        super(delegate);
        this.response = response;
    }

    public IHttpResponse getResponse() {
        return response;
    }

}
