package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;

import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class FDateDeserializer extends JsonDeserializer<FDate> {

    public static final FDateDeserializer INSTANCE = new FDateDeserializer();

    private FDateDeserializer() {}

    @Override
    public FDate deserialize(final JsonParser p, final DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        return FDate.valueOf(DateDeserializer.instance.deserialize(p, ctxt));
    }

}