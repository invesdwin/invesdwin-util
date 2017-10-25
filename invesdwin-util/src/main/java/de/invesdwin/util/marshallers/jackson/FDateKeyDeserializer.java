package de.invesdwin.util.marshallers.jackson;

import java.io.IOException;
import java.util.Date;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;

import de.invesdwin.util.lang.Strings;
import de.invesdwin.util.time.fdate.FDate;

@Immutable
public final class FDateKeyDeserializer extends KeyDeserializer {

    public static final FDateKeyDeserializer INSTANCE = new FDateKeyDeserializer();

    private final StdKeyDeserializer delegate = StdKeyDeserializer.forType(Date.class);

    private FDateKeyDeserializer() {}

    @Override
    public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
        if (Strings.isEmpty(key)) {
            return null;
        }
        final Date date = (Date) delegate.deserializeKey(key, ctxt);
        return FDate.valueOf(date);
    }

}
