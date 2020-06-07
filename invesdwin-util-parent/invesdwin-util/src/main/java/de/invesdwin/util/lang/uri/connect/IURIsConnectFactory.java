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

        @Override
        public void reset() {
            URIsConnectApacheSync.resetHttpClient();
        }
    };
    IURIsConnectFactory APACHE_ASYNC = new IURIsConnectFactory() {

        @Override
        public IURIsConnect connect(final URI uri) {
            return new URIsConnectApacheAsync(uri);
        }

        @Override
        public void reset() {
            URIsConnectApacheAsync.resetHttpClient();
        }
    };
    IURIsConnectFactory URL_CONNECTION = new IURIsConnectFactory() {

        @Override
        public IURIsConnect connect(final URI uri) {
            return new URIsConnectURLConnection(uri);
        }

        @Override
        public void reset() {
            //noop
        }
    };
    IURIsConnectFactory OK_HTTP = new IURIsConnectFactory() {

        @Override
        public IURIsConnect connect(final URI uri) {
            return new URIsConnectOkHttp(uri);
        }

        @Override
        public void reset() {
            //noop
        }
    };

    IURIsConnect connect(URI uri);

    void reset();

}
