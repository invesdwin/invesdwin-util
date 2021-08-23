package de.invesdwin.util.lang;

import java.nio.charset.Charset;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Charsets {

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private Charsets() {
    }

}
