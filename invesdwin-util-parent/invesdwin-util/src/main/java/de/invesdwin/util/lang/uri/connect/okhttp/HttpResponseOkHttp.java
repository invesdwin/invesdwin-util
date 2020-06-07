package de.invesdwin.util.lang.uri.connect.okhttp;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.uri.connect.IHttpResponse;
import okhttp3.Response;

@Immutable
public class HttpResponseOkHttp implements IHttpResponse {

    private final Response response;

    public HttpResponseOkHttp(final Response response) {
        this.response = response;
    }

    @Override
    public int getCode() {
        return response.code();
    }

    @Override
    public String getHeader(final String name) {
        return response.header(name);
    }

}
