package de.invesdwin.util.collections.delegate.disabled;

import java.util.Set;

import javax.annotation.concurrent.Immutable;

@Immutable
public class DisabledSet<E> extends DisabledCollection<E> implements Set<E> {

    @SuppressWarnings("rawtypes")
    private static final DisabledSet INSTANCE = new DisabledSet<>();

    @SuppressWarnings("unchecked")
    public static <T> DisabledSet<T> getInstance() {
        return INSTANCE;
    }

}
