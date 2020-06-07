package de.invesdwin.util.lang.uri.connect;

import java.net.URI;

import de.invesdwin.util.lang.uri.connect.apache.URIsConnectApacheAsync;
import de.invesdwin.util.lang.uri.connect.apache.URIsConnectApacheSync;
import de.invesdwin.util.lang.uri.connect.java.URIsConnectURLConnection;
import de.invesdwin.util.lang.uri.connect.okhttp.URIsConnectOkHttp;

public interface IURIsConnectFactory {

    IURIsConnectFactory APACHE_SYNC = new IURIsConnectFactory() {

        @Override
        public IURIsConnect connect(final URI uri) {
            return new URIsConnectApacheSync(uri);
        }
    };
    IURIsConnectFactory APACHE_ASYNC = new IURIsConnectFactory() {

        @Override
        public IURIsConnect connect(final URI uri) {
            return new URIsConnectApacheAsync(uri);
        }
    };
    IURIsConnectFactory URL_CONNECTION = new IURIsConnectFactory() {

        @Override
        public IURIsConnect connect(final URI uri) {
            return new URIsConnectURLConnection(uri);
        }
    };
    IURIsConnectFactory OK_HTTP = new IURIsConnectFactory() {

        @Override
        public IURIsConnect connect(final URI uri) {
            return new URIsConnectOkHttp(uri);
        }
    };

    IURIsConnect connect(URI uri);

}
