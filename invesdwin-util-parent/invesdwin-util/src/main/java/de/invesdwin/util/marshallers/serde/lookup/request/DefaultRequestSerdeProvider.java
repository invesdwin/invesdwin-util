package de.invesdwin.util.marshallers.serde.lookup.request;

import java.lang.reflect.Parameter;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.marshallers.serde.ISerde;
import de.invesdwin.util.marshallers.serde.TypeDelegateSerde;

@Immutable
public final class DefaultRequestSerdeProvider implements IRequestSerdeProvider {

    private final ISerde<Object> serde;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DefaultRequestSerdeProvider(final Parameter parameter) {
        this.serde = new TypeDelegateSerde(parameter.getType());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public DefaultRequestSerdeProvider(final ISerde<?> serde) {
        this.serde = (ISerde) serde;
    }

    @Override
    public ISerde<Object> getSerde(final Object[] requestArgs, final int index) {
        return serde;
    }

}
