package de.invesdwin.util.collections.loadingcache.historical;

import javax.annotation.concurrent.NotThreadSafe;

import org.junit.jupiter.api.Test;

import de.invesdwin.util.assertions.Assertions;
import de.invesdwin.util.math.expression.lambda.IEvaluateGenericFDate;
import de.invesdwin.util.time.date.FDate;

/**
 * The cachingEnabled flag of AHistoricalCache must follow the newCachingEnabled law from the very start: null means
 * unlimited, 0 (DISABLED_MAXIMUM_SIZE) means no caching at all. It must be initialised from getInitialMaximumSize()
 * and not from getMaximumSize(), because the maximumSize field initializer runs after the cachingEnabled one in
 * declaration order and would still be null there - which made every cache report isCachingEnabled() true.
 */
// CHECKSTYLE:OFF
@NotThreadSafe
public class AHistoricalCacheCachingEnabledTest {
    //CHECKSTYLE:ON

    @Test
    public void testDisabledInitialSizeMeansCachingDisabled() {
        final DisabledSizeCache cache = new DisabledSizeCache();
        Assertions.assertThat(cache.getMaximumSize()).isEqualTo(AHistoricalCache.DISABLED_MAXIMUM_SIZE);
        Assertions.assertThat(cache.isCachingEnabled()).isFalse();
    }

    @Test
    public void testPositiveInitialSizeMeansCachingEnabled() {
        final LimitedSizeCache cache = new LimitedSizeCache();
        Assertions.assertThat(cache.isCachingEnabled()).isTrue();
    }

    @Test
    public void testUnlimitedInitialSizeMeansCachingEnabled() {
        final UnlimitedSizeCache cache = new UnlimitedSizeCache();
        Assertions.assertThat(cache.getMaximumSize()).isNull();
        Assertions.assertThat(cache.isCachingEnabled()).isTrue();
    }

    private abstract static class ATestCachingEnabledCache extends AHistoricalCache<FDate> {

        @Override
        protected IEvaluateGenericFDate<FDate> newLoadValue() {
            return key -> key.asFDate();
        }

        @Override
        protected FDate innerExtractKey(final FDate value) {
            return value;
        }
    }

    private static final class DisabledSizeCache extends ATestCachingEnabledCache {

        @Override
        protected Integer getInitialMaximumSize() {
            return AHistoricalCache.DISABLED_MAXIMUM_SIZE;
        }
    }

    private static final class LimitedSizeCache extends ATestCachingEnabledCache {

        @Override
        protected Integer getInitialMaximumSize() {
            return Integer.valueOf(100);
        }
    }

    private static final class UnlimitedSizeCache extends ATestCachingEnabledCache {

        @Override
        protected Integer getInitialMaximumSize() {
            return AHistoricalCache.UNLIMITED_MAXIMUM_SIZE;
        }
    }
}
