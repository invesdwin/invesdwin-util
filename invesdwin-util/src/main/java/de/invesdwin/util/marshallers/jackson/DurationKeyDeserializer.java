package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.duration.Duration;

@Immutable
public final class DurationKeyDeserializer extends KeyDeserializer {

    public static final DurationKeyDeserializer INSTANCE = new DurationKeyDeserializer();

    private DurationKeyDeserializer() {}

    @Override
    public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
        if (Strings.isEmpty(key)) {
            return null;
        }
        return Duration.valueOf(key);
    }

}
