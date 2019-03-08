package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import de.invesdwin.util.time.duration.Duration;

@Immutable
public final class DurationDeserializer extends JsonDeserializer<Duration> {

    public static final DurationDeserializer INSTANCE = new DurationDeserializer();

    private DurationDeserializer() {}

    @Override
    public Duration deserialize(final JsonParser p, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        final String string = p.getText().trim();
        if (string.length() == 0) {
            return null;
        }
        return Duration.valueOf(string);
    }

}