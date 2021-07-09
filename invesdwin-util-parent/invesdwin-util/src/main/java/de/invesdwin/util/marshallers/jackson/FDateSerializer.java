package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.date.FDates;

@Immutable
public final class FDateSerializer extends JsonSerializer<FDate> {

    public static final FDateSerializer INSTANCE = new FDateSerializer();

    private FDateSerializer() {}

    @Override
    public void serialize(final FDate value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {
        gen.writeString(FDates.toString(value, FDate.FORMAT_ISO_DATE_TIME_MS));
    }
}