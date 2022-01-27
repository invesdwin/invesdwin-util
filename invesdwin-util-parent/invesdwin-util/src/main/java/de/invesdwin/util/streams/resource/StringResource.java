package de.invesdwin.util.streams.resource;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.core.io.ByteArrayResource;

@NotThreadSafe
public class StringResource extends ByteArrayResource {

    public StringResource(final String value) {
        super(value.getBytes());
    }

}
