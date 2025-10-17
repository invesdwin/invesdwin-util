package de.invesdwin.util.streams.resource;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.Immutable;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.lang.Nullable;

import de.invesdwin.util.assertions.Assertions;

/**
 * Similar to org.springframework.core.io.InputStreamResource except that the InputSreamSource is reusable multiple
 * times instead of throwing an exception when the input stream is requested multiple times. The InputStreamSource
 * should create a new InputStream each on request.
 */
@Immutable
public class InputStreamSourceResource extends AbstractResource {

    private final InputStreamSource inputStreamSource;
    private final String description;

    public InputStreamSourceResource(final InputStreamSource inputStreamSource) {
        this(inputStreamSource, "resource loaded from InputStreamSource");
    }

    public InputStreamSourceResource(final InputStreamSource inputStreamSource, @Nullable final String description) {
        Assertions.checkNotNull(inputStreamSource, "InputStreamSource must not be null");
        this.inputStreamSource = inputStreamSource;
        this.description = (description != null ? description : "");
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public InputStream getInputStream() throws IOException, IllegalStateException {
        return this.inputStreamSource.getInputStream();
    }

    @Override
    public String getDescription() {
        return "InputStream resource [" + this.description + "]";
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof InputStreamSourceResource) {
            final InputStreamSourceResource cOther = (InputStreamSourceResource) other;
            return inputStreamSource.equals(cOther.inputStreamSource);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return inputStreamSource.hashCode();
    }

}