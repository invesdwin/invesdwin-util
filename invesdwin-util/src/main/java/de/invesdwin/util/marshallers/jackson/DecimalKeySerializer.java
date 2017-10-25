package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;

import de.invesdwin.util.math.decimal.Decimal;

@Immutable
public final class DecimalKeySerializer extends JsonSerializer<Decimal> {

    public static final DecimalKeySerializer INSTANCE = new DecimalKeySerializer();

    private final JsonSerializer<Object> delegate = StdKeySerializers.getStdKeySerializer(null, Number.class, false);

    private DecimalKeySerializer() {}

    @Override
    public void serialize(final Decimal value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {
        delegate.serialize(value, gen, serializers);
    }

}
