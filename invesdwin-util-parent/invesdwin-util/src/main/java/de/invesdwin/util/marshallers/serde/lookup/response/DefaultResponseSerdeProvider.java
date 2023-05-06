package de.invesdwin.util.marshallers.serde.lookup.response;

import java.lang.reflect.Method;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.TypeDelegateSerde;

@Immutable
public final class DefaultResponseSerdeProvider implements IResponseSerdeProvider {

    private final TypeDelegateSerde<Object> serde;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DefaultResponseSerdeProvider(final Method method) {
        this.serde = new TypeDelegateSerde(method.getReturnType());
    }

    @Override
    public ISerde<Object> getSerde(final Object[] requestArgs) {
        return serde;
    }

}
