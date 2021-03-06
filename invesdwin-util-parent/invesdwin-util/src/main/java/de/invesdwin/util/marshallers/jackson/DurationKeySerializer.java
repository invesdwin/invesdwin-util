package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import de.invesdwin.util.time.duration.Duration;

@Immutable
public final class DurationKeySerializer extends JsonSerializer<Duration> {

    public static final DurationKeySerializer INSTANCE = new DurationKeySerializer();

    private DurationKeySerializer() {}

    @Override
    public void serialize(final Duration value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {
        gen.writeString(value.toString());
    }

}
