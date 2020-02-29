package de.invesdwin.util.collections.eviction;

import javax.annotation.concurrent.Immutable;

@Immutable
public enum EvictionMode {

    LeastRecentlyAdded {
        @Override
        public <K, V> IEvictionMap<K, V> newMap(final int maximumSize) {
            /*
             * we sacrifice a bit speed here to gain a halved memory consumption for historical caches
             * 
             * also it seems using commons map causes more cache misses in junit tests which causes more queries than
             * needed
             */
            return new ArrayLeastRecentlyAddedMap<>(maximumSize);
        }
    },
    LeastRecentlyModified {
        @Override
        public <K, V> IEvictionMap<K, V> newMap(final int maximumSize) {
            return new CommonsLeastRecentlyModifiedMap<>(maximumSize);
        }
    },
    LeastRecentlyUsed {
        @Override
        public <K, V> IEvictionMap<K, V> newMap(final int maximumSize) {
            return new CommonsLeastRecentlyUsedMap<>(maximumSize);
        }
    };

    public abstract <K, V> IEvictionMap<K, V> newMap(int maximumSize);

}
