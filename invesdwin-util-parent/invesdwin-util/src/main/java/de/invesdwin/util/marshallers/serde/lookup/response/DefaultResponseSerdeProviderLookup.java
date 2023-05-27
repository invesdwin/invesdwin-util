package de.invesdwin.util.marshallers.serde.lookup.response;

import java.lang.reflect.Method;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.ISerde;

@Immutable
public final class DefaultResponseSerdeProviderLookup implements IResponseSerdeProviderLookup {

    public static final DefaultResponseSerdeProviderLookup INSTANCE = new DefaultResponseSerdeProviderLookup();

    private DefaultResponseSerdeProviderLookup() {}

    @Override
    public IResponseSerdeProvider lookup(final Method method) {
        final ResponseSerde serdeAnnotation = Reflections.getAnnotation(method, ResponseSerde.class);
        if (serdeAnnotation != null) {
            final Class<? extends IResponseSerdeProvider> serdeProviderClass = serdeAnnotation.provider();
            if (serdeProviderClass != ResponseSerde.DEFAULT_PROVIDER.class) {
                return Reflections.getOrCreateInstance(serdeProviderClass);
            }
            final Class<? extends ISerde<?>> serdeClass = serdeAnnotation.serde();
            if (serdeClass != ResponseSerde.DEFAULT_SERDE.class) {
                final ISerde<?> serde = Reflections.getOrCreateInstance(serdeClass);
                return new DefaultResponseSerdeProvider(serde);
            }
        }
        return new DefaultResponseSerdeProvider(method);
    }

}
