package de.invesdwin.util.lang.uri.connect.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.concurrent.Immutable;
import javax.net.ssl.X509TrustManager;

@Immutable
public final class DisabledX509Trustmanager implements X509TrustManager {

    public static final DisabledX509Trustmanager INSTANCE = new DisabledX509Trustmanager();

    private DisabledX509Trustmanager() {}

    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {}

    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {}

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
