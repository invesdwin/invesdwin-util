package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers.BigDecimalDeserializer;

import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public final class DecimalDeserializer extends JsonDeserializer<Decimal> {

    public static final DecimalDeserializer INSTANCE = new DecimalDeserializer();

    private DecimalDeserializer() {}

    @Override
    public Decimal deserialize(final JsonParser p, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        return Decimal.valueOf(BigDecimalDeserializer.instance.deserialize(p, ctxt));
    }

}