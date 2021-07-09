package de.invesdwin.util.marshallers.jackson;

import javax.annotation.concurrent.NotThreadSafe;

import com.fasterxml.jackson.databind.module.SimpleModule;

import de.invesdwin.util.math.decimal.Decimal;
import de.invesdwin.util.time.date.FDate;
import de.invesdwin.util.time.duration.Duration;

@NotThreadSafe
public class InvesdwinUtilModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public InvesdwinUtilModule() {
        addDeserializer(FDate.class, FDateDeserializer.INSTANCE);
        addSerializer(FDate.class, FDateSerializer.INSTANCE);
        addKeySerializer(FDate.class, FDateKeySerializer.INSTANCE);
        addKeyDeserializer(FDate.class, FDateKeyDeserializer.INSTANCE);

        addDeserializer(Decimal.class, DecimalDeserializer.INSTANCE);
        addSerializer(Decimal.class, DecimalSerializer.INSTANCE);
        addKeySerializer(Decimal.class, DecimalKeySerializer.INSTANCE);
        addKeyDeserializer(Decimal.class, DecimalKeyDeserializer.INSTANCE);

        addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
        addSerializer(Duration.class, DurationSerializer.INSTANCE);
        addKeySerializer(Duration.class, DurationKeySerializer.INSTANCE);
        addKeyDeserializer(Duration.class, DurationKeyDeserializer.INSTANCE);
    }

    @Override
    public String getModuleName() {
        return getClass().getSimpleName();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return this == o;
    }

}
