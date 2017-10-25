package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class FDateKeyDeserializer extends KeyDeserializer {

    public static final FDateKeyDeserializer INSTANCE = new FDateKeyDeserializer();

    private FDateKeyDeserializer() {}

    @Override
    public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
        if (Strings.isEmpty(key)) {
            return null;
        }
        return FDate.valueOf(key, FDate.FORMAT_ISO_DATE_TIME_MS);
    }

}
