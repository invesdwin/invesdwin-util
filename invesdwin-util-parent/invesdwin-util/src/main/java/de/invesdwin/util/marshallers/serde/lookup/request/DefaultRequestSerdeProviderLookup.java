package de.invesdwin.util.marshallers.serde.lookup.request;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.basic.EmptyObjectArraySerde;

@Immutable
public final class DefaultRequestSerdeProviderLookup implements IRequestSerdeProviderLookup {

    public static final DefaultRequestSerdeProviderLookup INSTANCE = new DefaultRequestSerdeProviderLookup();

    private DefaultRequestSerdeProviderLookup() {}

    @Override
    public ISerde<Object[]> lookup(final Method method) {
        final Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return EmptyObjectArraySerde.GET;
        }
        final IRequestSerdeProvider[] providers = new IRequestSerdeProvider[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            providers[i] = lookup(parameter);
        }
        final boolean varArgs = parameters[parameters.length - 1].isVarArgs();
        return new RequestArgsSerde(providers, varArgs);

    }

    private IRequestSerdeProvider lookup(final Parameter parameter) {
        final RequestSerde serdeAnnotation = Reflections.getAnnotation(parameter, RequestSerde.class);
        if (serdeAnnotation != null) {
            final Class<? extends IRequestSerdeProvider> serdeProviderClass = serdeAnnotation.provider();
            if (serdeProviderClass != RequestSerde.DEFAULT_PROVIDER.class) {
                return Reflections.getOrCreateInstance(serdeProviderClass);
            }
            final Class<? extends ISerde<?>> serdeClass = serdeAnnotation.serde();
            if (serdeClass != RequestSerde.DEFAULT_SERDE.class) {
                final ISerde<?> serde = Reflections.getOrCreateInstance(serdeClass);
                return new DefaultRequestSerdeProvider(serde);
            }
        }
        return new DefaultRequestSerdeProvider(parameter);
    }

}
