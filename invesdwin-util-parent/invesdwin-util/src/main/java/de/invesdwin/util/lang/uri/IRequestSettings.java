package de.invesdwin.util.lang.uri;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.Method;

public interface IRequestSettings {

    IRequestSettings GET = new IRequestSettings() {

        @Override
        public Method getMethod() {
            return Method.GET;
        }

        @Override
        public SimpleHttpRequest customize(final SimpleHttpRequest request) {
            return request;
        }

    };
    IRequestSettings HEAD = new IRequestSettings() {

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
