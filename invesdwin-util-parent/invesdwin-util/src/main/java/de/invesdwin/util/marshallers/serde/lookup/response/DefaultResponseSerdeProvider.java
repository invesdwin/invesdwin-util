package de.invesdwin.util.marshallers.serde.lookup.response;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Future;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.lang.reflection.Reflections;
import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.TypeDelegateSerde;

@Immutable
public final class DefaultResponseSerdeProvider implements IResponseSerdeProvider {

    private final ISerde<Object> serde;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DefaultResponseSerdeProvider(final Method method) {
        Class<?> returnType = method.getReturnType();
        if (Future.class.isAssignableFrom(returnType)) {
            final Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType) {
                final ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
                final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length == 1) {
                    returnType = Reflections.determineClassType(actualTypeArguments[0]);
                }
            }
        }
        this.serde = new TypeDelegateSerde(returnType);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public DefaultResponseSerdeProvider(final ISerde<?> serde) {
        this.serde = (ISerde) serde;
    }

    @Override
    public ISerde<Object> getSerde(final Object[] requestArgs) {
        return serde;
    }

}
