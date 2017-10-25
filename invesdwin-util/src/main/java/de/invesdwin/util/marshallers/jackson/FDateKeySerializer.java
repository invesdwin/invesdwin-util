package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;
import java.util.Date;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers;

import de.invesdwin.util.time.fdate.FDate;
import de.invesdwin.util.time.fdate.FDates;

@Immutable
public final class FDateKeySerializer extends JsonSerializer<FDate> {

    public static final FDateKeySerializer INSTANCE = new FDateKeySerializer();

    private final JsonSerializer<Object> delegate = StdKeySerializers.getStdKeySerializer(null, Date.class, false);

    private FDateKeySerializer() {}

    @Override
    public void serialize(final FDate value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {
        delegate.serialize(FDates.toDate(value), gen, serializers);
    }

}
