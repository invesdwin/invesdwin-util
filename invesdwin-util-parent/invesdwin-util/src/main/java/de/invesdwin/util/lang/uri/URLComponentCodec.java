package de.invesdwin.util.lang.uri;

import java.nio.charset.StandardCharsets;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;

/**
 * https://stackoverflow.com/questions/607176/java-equivalent-to-javascripts-encodeuricomponent-that-produces-identical-outpu
 */
@Immutable
public final class URLComponentCodec implements StringEncoder, StringDecoder {

    private static final String HEX = "0123456789ABCDEF";

    @Override
    public Object encode(final Object obj) throws EncoderException {
        if (obj == null) {
            return null;
        } else if (obj instanceof String) {
            return encode((String) obj);
        } else {
            throw new EncoderException("Objects of type " + obj.getClass().getName() + " cannot be URL encoded");
        }
    }

    @Override
    public Object decode(final Object obj) throws DecoderException {
        if (obj == null) {
            return null;
        } else if (obj instanceof String) {
            return decode((String) obj);
        } else {
            throw new DecoderException("Objects of type " + obj.getClass().getName() + " cannot be URL decoded");
        }
    }

    @Override
    public String decode(final String str) throws DecoderException {
        if (str == null) {
            return null;
        }

        final int length = str.length();
        final byte[] bytes = new byte[length / 3];
        final StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length;) {
            char c = str.charAt(i);
            if (c != '%') {
                builder.append(c);
                i += 1;
            } else {
                int j = 0;
                do {
                    char h = str.charAt(i + 1);
                    char l = str.charAt(i + 2);
                    i += 3;

                    h -= '0';
                    if (h >= 10) {
                        h |= ' ';
                        h -= 'a' - '0';
                        if (h >= 6) {
                            throw new IllegalArgumentException();
                        }
                        h += 10;
                    }

                    l -= '0';
                    if (l >= 10) {
                        l |= ' ';
                        l -= 'a' - '0';
                        if (l >= 6) {
                            throw new IllegalArgumentException();
                        }
                        l += 10;
                    }

                    bytes[j++] = (byte) (h << 4 | l);
                    if (i >= length) {
                        break;
                    }
                    c = str.charAt(i);
                } while (c == '%');
                builder.append(new String(bytes, 0, j, StandardCharsets.UTF_8));
            }
        }

        return builder.toString();
    }

    @Override
    public String encode(final String str) throws EncoderException {
        if (str == null) {
            return null;
        }

        final byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        final StringBuilder builder = new StringBuilder(bytes.length);

        for (final byte c : bytes) {
            if (c >= 'a' ? c <= 'z' || c == '~'
                    : c >= 'A' ? c <= 'Z' || c == '_' : c >= '0' ? c <= '9' : c == '-' || c == '.') {
                builder.append((char) c);
            } else {
                builder.append('%').append(HEX.charAt(c >> 4 & 0xf)).append(HEX.charAt(c & 0xf));
            }
        }

        return builder.toString();
    }

}
