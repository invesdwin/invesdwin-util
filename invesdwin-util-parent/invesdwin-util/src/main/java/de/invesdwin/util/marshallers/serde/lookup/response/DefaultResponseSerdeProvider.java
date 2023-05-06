package de.invesdwin.util.marshallers.serde.lookup.response;

import java.lang.reflect.Method;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.TypeDelegateSerde;

@Immutable
public final class DefaultResponseSerdeProvider implements IResponseSerdeProvider {

    private final ISerde<Object> serde;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DefaultResponseSerdeProvider(final Method method) {
        this.serde = new TypeDelegateSerde(method.getReturnType());
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
