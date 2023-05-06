package de.invesdwin.util.marshallers.serde.lookup.response;

import java.lang.reflect.Method;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.lookup.Serde;

@Immutable
public final class DefaultResponseSerdeProviderLookup implements IResponseSerdeProviderLookup {

    public static final DefaultResponseSerdeProviderLookup INSTANCE = new DefaultResponseSerdeProviderLookup();

    private DefaultResponseSerdeProviderLookup() {}

    @Override
    public IResponseSerdeProvider lookup(final Method method) {
        final Serde serdeAnnotation = Reflections.getAnnotation(method, Serde.class);
        if (serdeAnnotation != null) {
            final Class<? extends IResponseSerdeProvider> serdeProviderClass = serdeAnnotation.responseProvider();
            if (serdeProviderClass != Serde.DEFAULT_RESPONSE_PROVIDER.class) {
                return Reflections.getOrCreateInstance(serdeProviderClass);
            }
            final Class<? extends ISerde<?>> serdeClass = serdeAnnotation.response();
            if (serdeProviderClass != Serde.DEFAULT_RESPONSE.class) {
                final ISerde<?> serde = Reflections.getOrCreateInstance(serdeClass);
                return new DefaultResponseSerdeProvider(serde);
            }
        }
        return new DefaultResponseSerdeProvider(method);
    }

}
