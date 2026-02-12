package de.invesdwin.util.collections.factory;

import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.norva.beanpath.collection.IBeanPathCollectionsProvider;

@Immutable
public final class FactoryBeanPathCollectionProvider implements IBeanPathCollectionsProvider {

    public static final FactoryBeanPathCollectionProvider INSTANCE = new FactoryBeanPathCollectionProvider();

    private FactoryBeanPathCollectionProvider() {}

    @Override
    public <K, V> Map<K, V> newMap() {
        return DisabledLockCollectionFactory.INSTANCE.newMap();
    }

    @Override
    public <K, V> Map<K, V> newConcurrentMap() {
        return SynchronizedLockCollectionFactory.INSTANCE.newConcurrentMap();
    }

    @Override
    public <T> Set<T> newSet() {
        return DisabledLockCollectionFactory.INSTANCE.newSet();
    }

}
