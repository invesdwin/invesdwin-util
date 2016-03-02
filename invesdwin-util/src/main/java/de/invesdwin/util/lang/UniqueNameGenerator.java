package de.invesdwin.util.lang;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

import de.invesdwin.util.collections.loadingcache.ALoadingCache;

@ThreadSafe
public final class UniqueNameGenerator {

    private final ALoadingCache<String, AtomicInteger> name_sequencenumber = new ALoadingCache<String, AtomicInteger>() {
        @Override
        protected AtomicInteger loadValue(final String key) {
            return new AtomicInteger();
        }
    };

    /**
     * Generates IDs in the schema of [Name]_[SequenceNumber].
     */
    public synchronized String get(final String name) {
        final AtomicInteger sequenceNumber = name_sequencenumber.get(name);
        final int sequenceNumberIncremented = sequenceNumber.incrementAndGet();
        if (sequenceNumberIncremented == 1) {
            return name;
        } else {
            return Strings.addSuffixToFileName(name, "_" + (sequenceNumberIncremented - 1));
        }
    }

}
