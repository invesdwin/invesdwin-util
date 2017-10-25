package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import de.invesdwin.util.time.duration.Duration;

@Immutable
public final class DurationSerializer extends JsonSerializer<Duration> {
    /**
     * Default instance that is used when no contextual configuration is needed.
     */
    public static final DurationSerializer INSTANCE = new DurationSerializer();

    private DurationSerializer() {}

    @Override
    public void serialize(final Duration value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {
        com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer.INSTANCE.serialize(Duration.toJavaTimeValue(value),
                gen, serializers);
    }
}