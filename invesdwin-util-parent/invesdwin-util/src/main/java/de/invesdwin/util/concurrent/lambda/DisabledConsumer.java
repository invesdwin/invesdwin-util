package de.invesdwin.util.concurrent.lambda;

import java.util.function.Consumer;

import javax.annotation.concurrent.Immutable;

@Immutable
public class DisabledConsumer<E> implements Consumer<E> {

    @SuppressWarnings("rawtypes")
    private static final DisabledConsumer INSTANCE = new DisabledConsumer();

    @Override
    public void accept(final Object t) {
    }

    @SuppressWarnings("unchecked")
    public static <T> DisabledConsumer<T> getInstance() {
        return INSTANCE;
    }

}
