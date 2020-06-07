package de.invesdwin.util.lang.uri;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.Method;

public interface IHttpRequestSettings {

    IHttpRequestSettings GET = new IHttpRequestSettings() {

        @Override
        public Method getMethod() {
            return Method.GET;
        }

        @Override
        public SimpleHttpRequest customize(final SimpleHttpRequest request) {
            return request;
        }

    };
    IHttpRequestSettings HEAD = new IHttpRequestSettings() {

        @Override
        public Method getMethod() {
            return Method.HEAD;
        }

        @Override
        public SimpleHttpRequest customize(final SimpleHttpRequest request) {
            return request;
        }

    };

    Method getMethod();

    SimpleHttpRequest customize(SimpleHttpRequest request);

}
