package de.invesdwin.util.concurrent.lock.trace.internal;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.factory.ILockCollectionFactory;
import de.invesdwin.util.collections.loadingcache.ALoadingCache;
import de.invesdwin.util.collections.loadingcache.ILoadingCache;
import de.invesdwin.util.collections.loadingcache.caffeine.CaffeineLoadingCacheMapConfig;
import de.invesdwin.util.collections.loadingcache.map.CaffeineLoadingCache;
import de.invesdwin.util.concurrent.Threads;
import de.invesdwin.util.concurrent.lock.trace.ILockTrace;
import de.invesdwin.util.concurrent.lock.trace.LockTraceEntry;
import de.invesdwin.util.error.Throwables;
import de.invesdwin.util.time.duration.Duration;

@ThreadSafe
public class EnabledLockTrace implements ILockTrace {

    private final ALoadingCache<String, Map<String, LockTraceEntry>> lockName_threadName_stackTrace = new ALoadingCache<String, Map<String, LockTraceEntry>>() {
        @Override
        protected Map<String, LockTraceEntry> loadValue(final String key) {
            return ILockCollectionFactory.getInstance(true).newConcurrentMap();
        }

        @Override
        protected ILoadingCache<String, Map<String, LockTraceEntry>> newDelegate() {
            final Integer maximumSize = getInitialMaximumSize();
            final Function<String, Map<String, LockTraceEntry>> loadValue = new Function<String, Map<String, LockTraceEntry>>() {
                @Override
                public Map<String, LockTraceEntry> apply(final String key) {
                    return loadValue(key);
                }
            };
            return new CaffeineLoadingCache<String, Map<String, LockTraceEntry>>(loadValue, maximumSize) {
                @Override
                protected CaffeineLoadingCacheMapConfig newConfig() {
                    return super.newConfig().setExpireAfterAccess(Duration.ONE_MINUTE);
                }
            };
        }

        @Override
        protected boolean isHighConcurrency() {
            return true;
        }
    };

    @Override
    public void locked(final String lockName) {
        final String threadName = Threads.getCurrentThreadName();
        final Map<String, LockTraceEntry> map = lockName_threadName_stackTrace.get(lockName);
        map.computeIfAbsent(threadName, new Function<String, LockTraceEntry>() {
            @Override
            public LockTraceEntry apply(final String t) {
                final LockTraceEntry stackTrace = new LockTraceEntry(lockName, threadName);
                stackTrace.fillInStackTrace();
                return stackTrace;
            }
        });
    }

    @Override
    public void unlocked(final String lockName) {
        final String threadName = Threads.getCurrentThreadName();
        lockName_threadName_stackTrace.get(lockName).remove(threadName);
    }

    @Override
    public boolean isLockedByThisThread(final String lockName) {
        final String threadName = Threads.getCurrentThreadName();
        return lockName_threadName_stackTrace.get(lockName).containsKey(threadName);
    }

    @Override
    public RuntimeException handleLockException(final String lockName, final Throwable lockException) {
        final StringBuilder sb = new StringBuilder();
        sb.append("CurrentLockName [");
        sb.append(lockName);
        sb.append("] CurrentThread [");
        sb.append(Threads.getCurrentThreadName());
        sb.append("]\nThe following locks are currently being held:\n*****************************");
        int countLocks = 0;
        for (final Entry<String, Map<String, LockTraceEntry>> e : lockName_threadName_stackTrace.entrySet()) {
            for (final Entry<String, LockTraceEntry> ee : e.getValue().entrySet()) {
                countLocks++;
                final String stackTrace = Throwables.getFullStackTrace(ee.getValue());
                sb.append("\nLock #");
                sb.append(countLocks);
                sb.append(": ");
                sb.append(stackTrace);
                sb.append("*****************************");
            }
        }
        return new RuntimeException(sb.toString(), lockException);
    }

}
