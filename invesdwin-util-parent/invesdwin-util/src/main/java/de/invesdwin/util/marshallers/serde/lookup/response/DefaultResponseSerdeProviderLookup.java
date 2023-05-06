package de.invesdwin.util.marshallers.serde.lookup.response;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.lookup.Serde;

@Immutable
public final class DefaultResponseSerdeProviderLookup implements IResponseSerdeProviderLookup {

    public static final DefaultResponseSerdeProviderLookup INSTANCE = new DefaultResponseSerdeProviderLookup();

    private DefaultResponseSerdeProviderLookup() {}

    @Override
    public IResponseSerdeProvider lookup(final Method method) {
        final Serde serdeAnnotation = Reflections.getAnnotation(method, Serde.class);
        if (serdeAnnotation != null) {
            final Class<? extends IResponseSerdeProvider> overrideClass = serdeAnnotation.response();
            if (overrideClass != Serde.DEFAULT_RESPONSE.class) {
                final Field instanceField = Reflections.findField(overrideClass, "INSTANCE");
                try {
                    if (instanceField != null) {
                        return (IResponseSerdeProvider) instanceField.get(null);
                    } else {
                        return overrideClass.getConstructor().newInstance();
                    }
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new DefaultResponseSerdeProvider(method);
    }

}
