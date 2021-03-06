package de.invesdwin.util.collections.loadingcache.map;

import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;

@ThreadSafe
public class UnlimitedCachingLoadingCache<K, V> extends ASynchronizedMapLoadingCache<K, V> {

    public UnlimitedCachingLoadingCache(final Function<K, V> loadValue) {
        super(loadValue, ILockCollectionFactory.getInstance(false).newMap());
    }

    @Override
    public void increaseMaximumSize(final int maximumSize) {
        //ignore
    }

}
