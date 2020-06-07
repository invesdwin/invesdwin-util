package de.invesdwin.util.lang.uri.connect;

public interface IHttpResponse {

    int getCode();

    String getHeader(String name);

}
