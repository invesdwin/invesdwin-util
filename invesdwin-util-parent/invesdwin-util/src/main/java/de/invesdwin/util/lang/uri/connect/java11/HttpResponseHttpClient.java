package de.invesdwin.util.lang.uri.connect.java11;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.uri.connect.IHttpResponse;

@Immutable
public class HttpResponseHttpClient implements IHttpResponse {

    private final int code;
    private final HttpHeaders headers;

    public HttpResponseHttpClient(final HttpResponse<?> response) {
        this.code = response.statusCode();
        this.headers = response.headers();
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getHeader(final String name) {
        return headers.firstValue(name).orElse(null);
    }

}
