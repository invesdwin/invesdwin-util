package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;

import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public final class DecimalSerializer extends JsonSerializer<Decimal> {

    public static final DecimalSerializer INSTANCE = new DecimalSerializer();

    private DecimalSerializer() {}

    @Override
    public void serialize(final Decimal value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {
        NumberSerializer.instance.serialize(value, gen, serializers);
    }

}
