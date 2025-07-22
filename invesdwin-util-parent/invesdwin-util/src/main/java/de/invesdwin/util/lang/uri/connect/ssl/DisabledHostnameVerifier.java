package de.invesdwin.util.lang.uri.connect.ssl;

import javax.annotation.concurrent.Immutable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

@Immutable
public final class DisabledHostnameVerifier implements HostnameVerifier {

    public static final DisabledHostnameVerifier INSTANCE = new DisabledHostnameVerifier();

    private DisabledHostnameVerifier() {}

    @Override
    public boolean verify(final String hostname, final SSLSession session) {
        return true;
    }

}
