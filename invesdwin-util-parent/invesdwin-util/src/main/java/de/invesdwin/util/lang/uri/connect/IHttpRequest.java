package de.invesdwin.util.lang.uri.connect;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.Method;

import okhttp3.Request;

public interface IHttpRequest {

    IHttpRequest GET = new IHttpRequest() {

        @Override
        public Method getMethod() {
            return Method.GET;
        }

        @Override
        public SimpleHttpRequest customize(final SimpleHttpRequest request) {
            return request;
        }

        @Override
        public Request.Builder customize(final Request.Builder request) {
            return request.get();
        }

    };
    IHttpRequest HEAD = new IHttpRequest() {

        @Override
        public Method getMethod() {
            return Method.HEAD;
        }

        @Override
        public SimpleHttpRequest customize(final SimpleHttpRequest request) {
            return request;
        }

        @Override
        public Request.Builder customize(final Request.Builder request) {
            return request.head();
        }

    };

    Method getMethod();

    SimpleHttpRequest customize(SimpleHttpRequest request);

    Request.Builder customize(Request.Builder request);

}
