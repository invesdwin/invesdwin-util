package de.invesdwin.util.lang.uri.connect.apache;

import javax.annotation.concurrent.Immutable;

import org.apache.hc.core5.http.HttpResponse;

import de.invesdwin.util.lang.uri.connect.IHttpResponse;

@Immutable
public class HttpResponseApache implements IHttpResponse {

    private final HttpResponse response;

    public HttpResponseApache(final HttpResponse response) {
        this.response = response;
    }

    @Override
    public int getCode() {
        return response.getCode();
    }

    @Override
    public String getHeader(final String name) {
        return response.getFirstHeader(name).getValue();
    }

}
