package de.invesdwin.util.streams;

import javax.annotation.concurrent.NotThreadSafe;

import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;

@NotThreadSafe
public class StringInputStream extends FastByteArrayInputStream {

    public StringInputStream(final String value) {
        super(value.getBytes());
    }

}
