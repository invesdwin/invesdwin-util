package de.invesdwin.util.marshallers.serde.lookup.request;

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
            final Class<? extends ISerde<?>> serdeClass = serdeAnnotation.request();
            if (serdeClass != Serde.DEFAULT_REQUEST.class) {
                return (ISerde<Object[]>) Reflections.getOrCreateInstance(serdeClass);
            }
        }
        return RemoteFastSerializingSerde.get();
    }

}