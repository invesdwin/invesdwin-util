package de.invesdwin.util.marshallers.serde.lookup.request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.RemoteFastSerializingSerde;
import de.invesdwin.util.marshallers.serde.lookup.Serde;

@Immutable
public final class DefaultRequestSerdeLookup implements IRequestSerdeLookup {

    public static final DefaultRequestSerdeLookup INSTANCE = new DefaultRequestSerdeLookup();

    private DefaultRequestSerdeLookup() {}

    @SuppressWarnings("unchecked")
    @Override
    public ISerde<Object[]> lookup(final Method method) {
        final Serde serdeAnnotation = Reflections.getAnnotation(method, Serde.class);
        if (serdeAnnotation != null) {
            final Class<? extends ISerde<Object[]>> overrideClass = serdeAnnotation.request();
            if (overrideClass != Serde.DEFAULT_REQUEST.class) {
                final Field instanceField = Reflections.findField(overrideClass, "INSTANCE");
                try {
                    if (instanceField != null) {
                        return (ISerde<Object[]>) instanceField.get(null);
                    } else {
                        return overrideClass.getConstructor().newInstance();
                    }
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return RemoteFastSerializingSerde.get();
    }

}
