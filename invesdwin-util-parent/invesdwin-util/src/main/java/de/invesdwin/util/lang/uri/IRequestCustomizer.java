package de.invesdwin.util.lang.uri;

import okhttp3.Request;

public interface IRequestCustomizer {

    Request.Builder customize(Request.Builder request);

}
