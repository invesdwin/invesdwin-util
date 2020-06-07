package de.invesdwin.util.lang.uri;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.lang.uri.connect.IURIsConnect;
import de.invesdwin.util.lang.uri.connect.IURIsConnectFactory;
import de.invesdwin.util.time.duration.Duration;
import de.invesdwin.util.time.fdate.FTimeUnit;

@Immutable
public final class URIs {

    private static Duration defaultNetworkTimeout = new Duration(30, FTimeUnit.SECONDS);

    private static final URLComponentCodec URL_CODEC = new URLComponentCodec();
    private static IURIsConnectFactory defaultUrisConnectFactory = IURIsConnectFactory.OK_HTTP;

    private URIs() {
    }

    public static void setDefaultNetworkTimeout(final Duration defaultNetworkTimeout) {
        URIs.defaultNetworkTimeout = defaultNetworkTimeout;
    }

    public static Duration getDefaultNetworkTimeout() {
        return defaultNetworkTimeout;
    }

    public static void setDefaultUrisConnectFactory(final IURIsConnectFactory urisConnectFactory) {
        URIs.defaultUrisConnectFactory = urisConnectFactory;
    }

    public static IURIsConnectFactory getDefaultUrisConnectFactory() {
        return defaultUrisConnectFactory;
    }

    public static String encode(final String url) {
        try {
            return URL_CODEC.encode(url);
        } catch (final EncoderException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(final String url) {
        try {
            return URL_CODEC.decode(url);
        } catch (final DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL asUrl(final String uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URL(uri); //SUPPRESS CHECKSTYLE singleline
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL asUrl(final URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return uri.toURL();
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URI asUri(final String uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI(uri); //SUPPRESS CHECKSTYLE singleline
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static URI asUriOrNull(final String uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI(uri); //SUPPRESS CHECKSTYLE singleline
        } catch (final URISyntaxException e) {
            return null;
        }
    }

    public static URI asUri(final URL url) {
        if (url == null) {
            return null;
        }
        try {
            return url.toURI();
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getBasis(final String uri) {
        return getBasis(asUri(uri));
    }

    public static String getBasis(final URI uri) {
        return uri.toString().replace(uri.getPath(), "");
    }

    public static String getBasis(final URL url) {
        return getBasis(asUri(url));
    }

    public static Map<String, String> getQueryMap(final URL url) {
        return getQueryMap(url.getQuery());
    }

    public static Map<String, String> getQueryMap(final URI uri) {
        return getQueryMap(uri.getQuery());
    }

    /**
     * http://stackoverflow.com/questions/11733500/getting-url-parameter-in-java-and-extract-a-specific-text-from-that-
     * url
     */
    private static Map<String, String> getQueryMap(final String query) {
        final String[] params = query.split("&");
        final Map<String, String> map = new HashMap<String, String>();
        for (final String param : params) {
            final String name = Strings.substringBefore(param, "=");
            final String value = Strings.substringAfter(param, "=");
            map.put(name, value);
        }
        return map;
    }

    public static IURIsConnect connect(final String uri) {
        return connect(asUri(uri));
    }

    public static IURIsConnect connect(final URI uri) {
        return defaultUrisConnectFactory.connect(uri);
    }

    public static IURIsConnect connect(final URL url) {
        return connect(asUri(url));
    }

    public static URI setPort(final URI uri, final int port) {
        try {
            //CHECKSTYLE:OFF
            final URI newUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), port, uri.getPath(),
                    uri.getQuery(), uri.getFragment());
            //CHECKSTYLE:ON
            return newUri;
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL setPort(final URL url, final int port) {
        try {
            //CHECKSTYLE:OFF
            final URL newUri = new URL(url.getProtocol(), url.getHost(), port, url.getFile());
            //CHECKSTYLE:ON
            return newUri;
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isSuccessful(final int responseCode) {
        return responseCode >= 200 && responseCode <= 299;
    }

    public static Proxy getSystemProxy() {
        //CHECKSTYLE:OFF
        final String httpProxyHost = System.getProperty("http.proxyHost");
        final String httpProxyPortStr = System.getProperty("http.proxyPort");
        //CHECKSTYLE:ON
        if (Strings.isNotBlank(httpProxyHost) && Strings.isNotBlank(httpProxyPortStr)) {
            final Integer httpProxyPort = Integer.parseInt(httpProxyPortStr);
            return new Proxy(Type.HTTP, Addresses.asAddress(httpProxyHost, httpProxyPort));
        } else {
            return null;
        }
    }

}
