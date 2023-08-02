package de.invesdwin.util.collections.delegate.disabled;

import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.Immutable;

@Immutable
public class DisabledConcurrentMap<K, V> extends DisabledMap<K, V> implements ConcurrentMap<K, V> {

    @SuppressWarnings("rawtypes")
    private static final DisabledConcurrentMap INSTANCE = new DisabledConcurrentMap<>();

    @SuppressWarnings("unchecked")
    public static <K, V> DisabledConcurrentMap<K, V> getInstance() {
        return INSTANCE;
    }

}
