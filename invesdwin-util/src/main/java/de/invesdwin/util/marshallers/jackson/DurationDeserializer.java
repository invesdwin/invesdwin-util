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
        return Duration.valueOf(
                com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer.INSTANCE.deserialize(p, ctxt));
    }

}