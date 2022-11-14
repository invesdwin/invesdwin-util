package de.invesdwin.util.lang.uri.header;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.codec.binary.Base64;

@Immutable
public final class BasicAuth {

    private static final String BASIC_PREFIX = "Basic ";
    private static final String SEPARATOR = ":";
    private final String username;
    private final String password;

    public BasicAuth(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return encode(username, password);
    }

    public static String encode(final String username, final String password) {
        final String authString = username + SEPARATOR + password;
        final byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        final String authStringEnc = new String(authEncBytes);
        return BASIC_PREFIX + authStringEnc;
    }

    public static BasicAuth decode(final String encoded) {
        if (encoded == null) {
            return null;
        }

        if (encoded.length() < 6) {
            return null;
        }
        final String type = encoded.substring(0, 6);
        if (!BASIC_PREFIX.equalsIgnoreCase(type)) {
            return null;
        }
        String val = encoded.substring(6);
        val = new String(org.apache.commons.codec.binary.Base64.decodeBase64(val.getBytes()));
        final String[] split = val.split(SEPARATOR);
        if (split.length != 2) {
            return null;
        }
        return new BasicAuth(split[0], split[1]);
    }

}
