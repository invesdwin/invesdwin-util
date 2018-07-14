package de.invesdwin.util.collections.loadingcache.guava;

import javax.annotation.concurrent.Immutable;

import de.invesdwin.util.error.UnknownArgumentException;

@Immutable
public enum RemovalCause {
    EXPLICIT {
        @Override
        boolean wasEvicted() {
            return false;
        }
    },

    REPLACED {
        @Override
        boolean wasEvicted() {
            return false;
        }
    },

    COLLECTED {
        @Override
        boolean wasEvicted() {
            return true;
        }
    },

    EXPIRED {
        @Override
        boolean wasEvicted() {
            return true;
        }
    },

    SIZE {
        @Override
        boolean wasEvicted() {
            return true;
        }
    };

    abstract boolean wasEvicted();

    public static RemovalCause valueOf(final com.github.benmanes.caffeine.cache.RemovalCause removalCause) {
        switch (removalCause) {
        case COLLECTED:
            return COLLECTED;
        case EXPIRED:
            return EXPIRED;
        case EXPLICIT:
            return EXPLICIT;
        case REPLACED:
            return REPLACED;
        case SIZE:
            return SIZE;
        default:
            throw UnknownArgumentException.newInstance(com.github.benmanes.caffeine.cache.RemovalCause.class,
                    removalCause);
        }
    }

    public static RemovalCause valueOf(final com.google.common.cache.RemovalCause removalCause) {
        switch (removalCause) {
        case COLLECTED:
            return COLLECTED;
        case EXPIRED:
            return EXPIRED;
        case EXPLICIT:
            return EXPLICIT;
        case REPLACED:
            return REPLACED;
        case SIZE:
            return SIZE;
        default:
            throw UnknownArgumentException.newInstance(com.google.common.cache.RemovalCause.class, removalCause);
        }
    }
}
