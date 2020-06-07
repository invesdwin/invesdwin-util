package de.invesdwin.util.lang.uri.connect.urlconnection;

import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.uri.connect.IHttpResponse;

@Immutable
public class HttpResponseURLConnection implements IHttpResponse {

    private final int code;
    private final Map<String, List<String>> headers;

    public HttpResponseURLConnection(final int code, final Map<String, List<String>> headers) {
        this.code = code;
        this.headers = headers;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getHeader(final String name) {
        final List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) {
            return null;
        } else {
            return values.get(0);
        }
    }

}
