package de.invesdwin.util.lang.uri;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.concurrent.Immutable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

@Immutable
public final class SSLContexts {

    private SSLContexts() {}

    public static SSLContext newInstance(final X509TrustManager trustManager) {
        try {
            final TrustManager[] trustManagers = new TrustManager[] { trustManager };
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new java.security.SecureRandom());
            return sslContext;
        } catch (final KeyManagementException e) {
            throw new RuntimeException(e);
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
