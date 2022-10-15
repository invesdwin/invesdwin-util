package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import de.invesdwin.util.lang.string.Strings;
import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public final class DecimalKeyDeserializer extends KeyDeserializer {

    public static final DecimalKeyDeserializer INSTANCE = new DecimalKeyDeserializer();

    private DecimalKeyDeserializer() {}

    @Override
    public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
        if (Strings.isEmpty(key)) {
            return null;
        }
        return Decimal.valueOf(key);
    }

}