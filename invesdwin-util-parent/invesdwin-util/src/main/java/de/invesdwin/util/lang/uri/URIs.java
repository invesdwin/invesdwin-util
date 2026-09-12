package de.invesdwin.util.lang.uri;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.collections.Arrays;
import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.lang.Files;
import de.invesdwin.util.lang.string.Charsets;
import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.lang.uri.connect.IURIsConnect;
import de.invesdwin.util.lang.uri.connect.IURIsConnectFactory;
import de.invesdwin.util.time.date.FTimeUnit;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public final class URIs {

    /**
     * https://stackoverflow.com/a/13500078
     * 
     * unwise = "{" | "}" | "|" | "\" | "^" | "[" | "]" | "`"
     * 
     * reserved = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" | "$" | ","
     */
    public static final char[] NORMALIZE_FILENAME_SEARCH = Arrays.concat(Files.NORMALIZE_FILENAME_SEARCH,
            new char[] { '{', '}', '[', ']', '^', '\'', ';', '&', '+', '$', ',' });
    /**
     * need to use distinct characters here so that expressions don't become mixed if they only differ in an operator
     * that gets escaped here
     */
    public static final char[] NORMALIZE_FILENAME_REPLACE = Arrays.concat(Files.NORMALIZE_FILENAME_REPLACE,
            new char[] { 'Q', 'E', 'M', 'N', 'F', 'A', 'S', 'N', 'P', 'D', 'C' });
    public static final char[] NORMALIZE_PATH_SEARCH = Arrays.concat(Files.NORMALIZE_PATH_SEARCH,
            new char[] { '{', '}', '[', ']', '^', '\'', ';', '&', '+', '$', ',', '\\' });
    public static final char[] NORMALIZE_PATH_REPLACE = Arrays.concat(Files.NORMALIZE_PATH_REPLACE,
            new char[] { 'Q', 'E', 'M', 'N', 'F', 'A', 'S', 'N', 'P', 'D', 'C', '/' });

    private static Duration defaultNetworkTimeout = new Duration(30, FTimeUnit.SECONDS);

    private static final URLComponentCodec URL_CODEC = new URLComponentCodec();
    private static IURIsConnectFactory defaultUrisConnectFactory = IURIsConnectFactory.OK_HTTP;

    private static final char[] NORMALIZE_FILENAME_REPLACE_MAP = new char[128];
    private static final char[] NORMALIZE_PATH_REPLACE_MAP = new char[128];

    static {
        Assertions.assertThat(NORMALIZE_FILENAME_SEARCH.length).isEqualByComparingTo(NORMALIZE_FILENAME_REPLACE.length);
        Assertions.assertThat(NORMALIZE_PATH_SEARCH.length).isEqualByComparingTo(NORMALIZE_PATH_REPLACE.length);
        for (int i = 0; i < NORMALIZE_FILENAME_SEARCH.length; i++) {
            NORMALIZE_FILENAME_REPLACE_MAP[NORMALIZE_FILENAME_SEARCH[i]] = NORMALIZE_FILENAME_REPLACE[i];
        }
        for (int i = 0; i < NORMALIZE_PATH_SEARCH.length; i++) {
            NORMALIZE_PATH_REPLACE_MAP[NORMALIZE_PATH_SEARCH[i]] = NORMALIZE_PATH_REPLACE[i];
        }
    }

    private URIs() {}

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
            throw new RuntimeException(Strings.asString(uri), e);
        }
    }

    public static URI asUri(final String uri) {
        try {
            return new URI(encodeUri(uri)); //SUPPRESS CHECKSTYLE singleline
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Cannot parse URI: " + uri, e);
        }
    }

    /**
     * Encode characters that break java.net.URI. This is a workaround for the fact that java.net.URI does not encode
     * this.
     */
    public static String encodeUri(final String uri) {
        StringBuilder sb = null;
        // Single-pass check for characters that break java.net.URI
        for (int i = 0; i < uri.length(); i++) {
            final char c = uri.charAt(i);
            String replacement = null;

            switch (c) {
            case ' ':
                replacement = "%20";
                break;
            case '{':
                replacement = "%7B";
                break;
            case '}':
                replacement = "%7D";
                break;
            case '|':
                replacement = "%7C";
                break;
            case '\\':
                replacement = "%5C";
                break;
            case '^':
                replacement = "%5E";
                break;
            case '[':
                replacement = "%5B";
                break;
            case ']':
                replacement = "%5D";
                break;
            case '"':
                replacement = "%22";
                break;
            case '<':
                replacement = "%3C";
                break;
            case '>':
                replacement = "%3E";
                break;
            case '`':
                replacement = "%60";
                break;
            default:
                break;
            }

            if (replacement != null) {
                // Initialize the builder lazily only when the first illegal char is found
                if (sb == null) {
                    sb = new StringBuilder(uri.length() + 16);
                    sb.append(uri, 0, i);
                }
                sb.append(replacement);
            } else if (sb != null) {
                sb.append(c);
            }
        }
        final String safeUriString = (sb == null) ? uri : sb.toString();
        return safeUriString;
    }

    /**
     * Decodes percent-encoded characters in a URI. Safely reverses encodeUri() without the "+" to " " bug found in
     * java.net.URLDecoder.
     */
    public static String decodeUri(final String uri) {
        // Fast path: if there is no percent sign, nothing to decode
        if (uri == null || uri.indexOf('%') < 0) {
            return uri;
        }

        final StringBuilder sb = new StringBuilder(uri.length());
        for (int i = 0; i < uri.length(); i++) {
            final char c = uri.charAt(i);

            // Check if we found a '%' followed by at least two characters
            if (c == '%' && i + 2 < uri.length()) {
                final int high = decodeHex(uri.charAt(i + 1));
                final int low = decodeHex(uri.charAt(i + 2));

                // If both characters are valid hex digits, combine them into a character
                if (high != -1 && low != -1) {
                    sb.append((char) ((high << 4) | low));
                    i += 2; // Skip the two hex characters
                    continue;
                }
            }
            // If not a valid hex sequence, or not a '%', append the character as-is
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Helper to quickly parse hex characters without throwing NumberFormatExceptions.
     */
    private static int decodeHex(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        return -1;
    }

    public static URI asUriOrNull(final String uri) {
        if (uri == null) {
            return null;
        }
        try {
            return new URI(encodeUri(uri)); //SUPPRESS CHECKSTYLE singleline
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
        final Map<String, String> map = ILockCollectionFactory.getInstance(false).newMap();
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

    public static String normalizeFilename(final String name) {
        return Files.normalizePathMaxLength(Strings.replaceMappedAscii(name, NORMALIZE_FILENAME_REPLACE_MAP));
    }

    public static String normalizePath(final String path) {
        return Files.normalizePathMaxLength(Strings.replaceMappedAscii(path, NORMALIZE_PATH_REPLACE_MAP));
    }

    public static Map<String, String> splitQuery(final URI uri) {
        final String query = uri.getQuery();
        if (Strings.isBlank(query)) {
            return Collections.emptyMap();
        }
        final String[] queryPairs = Strings.splitPreserveAllTokens(query, "&");
        if (queryPairs.length == 0) {
            return Collections.emptyMap();
        }
        final Map<String, String> result = ILockCollectionFactory.getInstance(false).newLinkedMap();
        splitQueryPairs(queryPairs, result);
        return result;
    }

    public static void splitQuery(final URI uri, final Map<String, String> result) {
        final String query = uri.getQuery();
        if (Strings.isBlank(query)) {
            return;
        }
        final String[] queryPairs = Strings.splitPreserveAllTokens(query, "&");
        if (queryPairs.length == 0) {
            return;
        }
        splitQueryPairs(queryPairs, result);
    }

    private static void splitQueryPairs(final String[] queryPairs, final Map<String, String> result) {
        for (int i = 0; i < queryPairs.length; i++) {
            final String queryPair = queryPairs[i];
            final int indexOf = queryPair.indexOf("=");
            final String key = indexOf > 0 ? URLDecoder.decode(queryPair.substring(0, indexOf), Charsets.UTF_8)
                    : queryPair;
            final String value = indexOf > 0 && queryPair.length() > indexOf + 1
                    ? URLDecoder.decode(queryPair.substring(indexOf + 1), Charsets.UTF_8)
                    : null;
            result.put(key, value);
        }
    }

    public static String maybeAddQuery(final String host, final Map<String, String> queryPairs) {
        final String query = joinQuery(queryPairs);
        return maybeAddQuery(host, query);
    }

    public static String maybeAddQuery(final String host, final String query) {
        if (Strings.isBlank(query)) {
            return host;
        } else {
            return host + "?" + query;
        }
    }

    public static String maybeRemoveQuery(final String uri) {
        return Strings.substringBefore(uri, '?');
    }

    public static String joinQuery(final Map<String, String> queryPairs) {
        if (queryPairs.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final Entry<String, String> entry : queryPairs.entrySet()) {
            if (!Strings.isEmpty(sb)) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(entry.getKey(), Charsets.UTF_8));
            final String value = entry.getValue();
            if (value != null) {
                sb.append("=");
                sb.append(URLEncoder.encode(value, Charsets.UTF_8));
            }
        }
        return sb.toString();
    }

}
