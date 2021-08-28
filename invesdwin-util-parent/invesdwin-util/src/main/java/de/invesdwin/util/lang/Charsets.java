package de.invesdwin.util.lang;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Charsets {

    /**
     * With UTF-8 we don't have to care about endianness:
     * https://stackoverflow.com/questions/35704606/method-string-getbytes-is-big-endian-or-litter-endian
     */
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
    public static final Charset US_ASCII = StandardCharsets.US_ASCII;

    private Charsets() {
    }

}
