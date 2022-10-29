package de.invesdwin.util.lang.uri.header;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.codec.binary.Base64;

import de.invesdwin.util.collections.Collections;
import de.invesdwin.util.lang.string.Strings;

@Immutable
public final class Headers {

    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LAST_MODIFIED = "Last-Modified";
    private static final String ENTRY_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = ":";

    private Headers() {}

    public static String encode(final Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final Entry<String, String> entry : headers.entrySet()) {
            if (sb.length() > 0) {
                sb.append(ENTRY_SEPARATOR);
            }
            final String key = new String(Base64.encodeBase64(entry.getKey().getBytes()));
            final String value = new String(Base64.encodeBase64(entry.getValue().getBytes()));
            sb.append(key);
            sb.append(KEY_VALUE_SEPARATOR);
            sb.append(value);
        }
        return sb.toString();
    }

    public static Map<String, String> decode(final String encoded) {
        if (Strings.isBlank(encoded)) {
            return Collections.emptyMap();
        }
        final String[] entries = Strings.splitPreserveAllTokens(encoded, ENTRY_SEPARATOR);
        final Map<String, String> headers = new LinkedHashMap<>();
        for (int i = 0; i < entries.length; i++) {
            final String entry = entries[i];
            final String[] keyValue = Strings.splitPreserveAllTokens(entry, KEY_VALUE_SEPARATOR);
            if (keyValue.length != 2) {
                //drop silently
                continue;
            }
            final String key = new String(Base64.decodeBase64(keyValue[0]));
            final String value = new String(Base64.decodeBase64(keyValue[1]));
            headers.put(key, value);
        }
        return headers;
    }

}
